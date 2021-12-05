package esaph.filing.CardsShowingFromList.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.Adapter.GetViewTypeHandling.Handlers.ViewTypeHandlerKarte;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.CardsShowingFromList.Model.Auftrag.ViewType;
import esaph.filing.CardsShowingFromList.Sorting.KartenComperator;
import esaph.filing.CardsShowingFromList.Sorting.KartenSortMethods;
import esaph.filing.CardsShowingFromList.TransferWindow.DialogTransferAuftrag;
import esaph.filing.R;
import esaph.filing.Utils.ListObserver;
import esaph.filing.workers.ArchiviereAuftrag;
import esaph.filing.workers.TransferAuftrag;

public class AdapterKartenAnzeigen extends BaseAdapter implements Filterable
{
    private Activity activity;
    private FragmentManager fragmentManager;
    private BoardListe boardListe;
    private Board board;
    private List<Auftrag> mDisplayList = new ArrayList<>();
    private List<Auftrag> mDataBaseListOriginal = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> adapterDataEmptyListenerWeakReference;
    private KartenSortMethods kartenSortMethods;

    public AdapterKartenAnzeigen(Activity activity,
                                 BoardListe boardListe,
                                 Board board,
                                 FragmentManager fragmentManager,
                                 ListObserver adapterDataEmptyListener,
                                 KartenSortMethods kartenSortMethods)
    {
        this.activity = activity;
        this.boardListe = boardListe;
        this.fragmentManager = fragmentManager;
        this.board = board;
        this.kartenSortMethods = kartenSortMethods;
        this.inflater = LayoutInflater.from(activity);
        this.adapterDataEmptyListenerWeakReference = new WeakReference<>(adapterDataEmptyListener);
    }

    public void setKartenSortMethods(KartenSortMethods kartenSortMethods)
    {
        this.kartenSortMethods = kartenSortMethods;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                mDisplayList = (List<Auftrag>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();
                if(constraint.toString().isEmpty())
                {
                    results.values = mDataBaseListOriginal;
                    return results;
                }
                ArrayList<Auftrag> FilteredArrayNames = new ArrayList<Auftrag>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mDataBaseListOriginal.size(); i++)
                {
                    Auftrag dataNames = mDataBaseListOriginal.get(i);
                    if (dataNames.getmAufgabe().toLowerCase().contains(constraint.toString()))
                    {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    @Override
    public void notifyDataSetChanged()
    {
        Collections.sort(mDataBaseListOriginal, KartenComperator.getComperator(kartenSortMethods));

        ListObserver adapterDataEmptyListener = adapterDataEmptyListenerWeakReference.get();
        if(adapterDataEmptyListener != null)
        {
            adapterDataEmptyListener.observDataChange(mDataBaseListOriginal.isEmpty());
        }
        super.notifyDataSetChanged();
    }

    public void clearData()
    {
        mDataBaseListOriginal.clear();
        getFilter().filter("");
    }


    public void removeItem(Auftrag auftrag)
    {
        int size = mDataBaseListOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(mDataBaseListOriginal.get(counter).getmAuftragsId() == auftrag.getmAuftragsId())
            {
                mDataBaseListOriginal.remove(counter);
                getFilter().filter("");
                break;
            }
        }
    }

    public void updateItem(Auftrag auftrag)
    {
        int size = mDataBaseListOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(mDataBaseListOriginal.get(counter).getmAuftragsId() == auftrag.getmAuftragsId())
            {
                mDataBaseListOriginal.set(counter, auftrag);
                getFilter().filter("");
                break;
            }
        }
    }

    public void addItem(Auftrag auftrag)
    {
        if(canAdd(auftrag.getmAuftragsId()))
        {
            this.mDataBaseListOriginal.add(auftrag);
        }
        getFilter().filter("");
    }

    private boolean canAdd(long ID_Check)
    {
        int size = mDataBaseListOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(mDataBaseListOriginal.get(counter).getmAuftragsId() == ID_Check)
            {
                return false;
            }
        }
        return true;
    }

    public void addItems(List<Auftrag> list)
    {
        this.mDataBaseListOriginal.addAll(list);
        getFilter().filter("");
    }

    public class ViewHolderKarte
    {
        public View viewColorLine;
        public TextView textViewAufgabe;
        public ImageView imageViewTransfer;
        public Button buttonDone;
    }

    public class ViewHolderListe
    {
        public TextView textViewAblauf;
        public View viewColorLine;
        public TextView textViewBeschreibung;
        public ImageView imageViewMore;
        public Button buttonDone;
    }

    private ViewTypeHandlerKarte viewTypeHandlerKarte = new ViewTypeHandlerKarte();
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Auftrag auftrag = mDisplayList.get(position);
        if(getItemViewType(position) == ViewType.AUFTRAG_KARTE.ordinal())
        {
            final ViewHolderKarte viewHolderKarte;
            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.item_today_task, parent, false);
                viewHolderKarte = new ViewHolderKarte();
                viewHolderKarte.viewColorLine = convertView.findViewById(R.id.item_today_task_View_color);
                viewHolderKarte.textViewAufgabe = convertView.findViewById(R.id.item_today_task_TextView_Aufgabe);
                viewHolderKarte.buttonDone = convertView.findViewById(R.id.item_today_task_Button_Erledigt);
                viewHolderKarte.imageViewTransfer = convertView.findViewById(R.id.item_today_task_ImageViewTransferTask);
                convertView.setTag(viewHolderKarte);
            }
            else
            {
                viewHolderKarte = (ViewHolderKarte) convertView.getTag();
            }

            viewHolderKarte.imageViewTransfer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogTransferAuftrag dialogTransferAuftrag = new DialogTransferAuftrag(auftrag,
                            boardListe,
                            board,
                            (TransferAuftrag.TransferAutragListener) activity);

                    dialogTransferAuftrag.show(fragmentManager, DialogTransferAuftrag.class.getName());
                }
            });

            viewHolderKarte.buttonDone.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    new ArchiviereAuftrag(activity,
                            auftrag,
                            new ArchiviereAuftrag.ArchiviereAuftragListener()
                            {
                                @Override
                                public void onResult(Auftrag auftragKarte, boolean success)
                                {
                                    if(success)
                                    {
                                        removeItem(auftragKarte);
                                    }
                                    else
                                    {
                                        if(activity != null && !activity.isFinishing())
                                        {
                                            new AlertDialog.Builder(activity).setTitle(R.string.error_archivieren_title)
                                                    .setMessage(R.string.error_archivieren)
                                                    .show();
                                        }
                                    }
                                }
                            })
                            .transferSynch();
                }
            });

            viewTypeHandlerKarte.handleViewType(activity, auftrag, viewHolderKarte);
        }
        else if(getItemViewType(position) == ViewType.AUFTAG_LISTE.ordinal())
        {
            ViewHolderListe viewHolderListe;
            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.item_today_task, parent, false);
                viewHolderListe = new ViewHolderListe();
                viewHolderListe.viewColorLine = convertView.findViewById(R.id.item_today_task_View_color);
                viewHolderListe.textViewBeschreibung = convertView.findViewById(R.id.item_today_task_TextView_Aufgabe);
                viewHolderListe.buttonDone = convertView.findViewById(R.id.item_today_task_Button_Erledigt);
                convertView.setTag(viewHolderListe);
            }
            else
            {
                viewHolderListe = (ViewHolderListe) convertView.getTag();
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mDisplayList.size();
    }

    @Override
    public Auftrag getItem(int position) {
        return mDisplayList.get(position);
    }

    public int getItemCountOriginal()
    {
        return mDataBaseListOriginal.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mDisplayList.get(position).getViewType().ordinal();
    }

    @Override
    public int getViewTypeCount()
    {
        return ViewType.values().length;
    }

}
