package esaph.filing.CardsShowingFromList.TransferWindow.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.Sorting.CompListSortByName;
import esaph.filing.R;
import esaph.filing.Utils.ListObserver;

public class AdapterListenPickerTransferAnzeigen extends BaseAdapter implements Filterable
{
    private List<BoardListe> mDisplayList = new ArrayList<>();
    private List<BoardListe> mDataBaseListOriginal = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> adapterDataEmptyListenerWeakReference;
    private BoardListe boardListeAktuell;

    public AdapterListenPickerTransferAnzeigen(Context context,
                                               BoardListe boardListeAktuell,
                                               ListObserver adapterDataEmptyListener)
    {
        this.inflater = LayoutInflater.from(context);
        this.boardListeAktuell = boardListeAktuell;
        this.adapterDataEmptyListenerWeakReference = new WeakReference<>(adapterDataEmptyListener);
    }

    private void recyclerViewNotify()
    {
        Collections.sort(this.mDataBaseListOriginal, new CompListSortByName());

        ListObserver adapterDataEmptyListener = adapterDataEmptyListenerWeakReference.get();
        if(adapterDataEmptyListener != null)
        {
            adapterDataEmptyListener.observDataChange(mDataBaseListOriginal.isEmpty());
        }
    }

    public void clearData()
    {
        mDataBaseListOriginal.clear();
        getFilter().filter("");
    }

    public void removeItem(BoardListe boardListe)
    {
        int size = mDataBaseListOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(mDataBaseListOriginal.get(counter).getmBoardListeId() == boardListe.getmBoardListeId())
            {
                mDataBaseListOriginal.remove(counter);
                getFilter().filter("");
                break;
            }
        }
    }

    public void addItems(List<BoardListe> list)
    {
        this.mDataBaseListOriginal.addAll(list);
        getFilter().filter("");
    }

    public void addItem(BoardListe board)
    {
        this.mDataBaseListOriginal.add(board);
        getFilter().filter("");
    }

    private class ViewHolderListe
    {
        private View rootView;
        private TextView textViewListenname;
        private TextView textViewEintragAnzahl;
    }

    @Override
    public int getCount()
    {
        return mDisplayList.size();
    }

    public BoardListe getItem(int position)
    {
        return mDisplayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolderListe viewHolderListe;
        BoardListe boardListe = mDisplayList.get(position);
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_list_picker, parent, false);
            viewHolderListe = new ViewHolderListe();

            viewHolderListe.rootView = convertView;
            viewHolderListe.textViewListenname = convertView.findViewById(R.id.item_list_picker_TextViewListName);
            viewHolderListe.textViewEintragAnzahl = convertView.findViewById(R.id.item_list_picker_TextViewListTeil);

            convertView.setTag(viewHolderListe);
        }
        else
        {
            viewHolderListe = (ViewHolderListe) convertView.getTag();
        }

        if(boardListe.equals(boardListeAktuell))
        {
            viewHolderListe.textViewListenname.setAlpha(0.3f);
        }
        else
        {
            viewHolderListe.textViewListenname.setAlpha(1f);
        }

        viewHolderListe.textViewListenname.setText(boardListe.getmListenName());
        viewHolderListe.textViewEintragAnzahl.setText(convertView.getResources().getString(R.string.auftraegeAnzahl,
                boardListe.getAuftragKartenListe() != null ?
                        boardListe.getAuftragKartenListe().size()
                        : "0"
        ));
        return convertView;
    }

    public int getCountOriginal()
    {
        return mDataBaseListOriginal.size();
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
                mDisplayList = (List<BoardListe>) results.values;
                recyclerViewNotify();
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

                ArrayList<BoardListe> FilteredArrayNames = new ArrayList<BoardListe>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mDataBaseListOriginal.size(); i++)
                {
                    BoardListe dataNames = mDataBaseListOriginal.get(i);
                    if (dataNames.getmListenName().toLowerCase().startsWith(constraint.toString()))
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


}
