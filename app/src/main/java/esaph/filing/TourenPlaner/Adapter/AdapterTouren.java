package esaph.filing.TourenPlaner.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import esaph.filing.Board.Model.Board;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.KundenManagement.ContactDTO;
import esaph.filing.R;
import esaph.filing.TourenPlaner.Adapter.Sorting.CompTourenTime;
import esaph.filing.Utils.DividerView;
import esaph.filing.Utils.ListObserver;

public class AdapterTouren extends BaseAdapter implements Filterable
{
    private Context context;
    private List<Auftrag> mDisplayList = new ArrayList<>();
    private final List<Auftrag> mDataBaseListOriginal = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> listObserverWeakReference;
    private String addressLastZiel;

    public AdapterTouren(Context context,
                         ListObserver listObserver,
                         String addressLastZiel)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.addressLastZiel = addressLastZiel;
        this.listObserverWeakReference = new WeakReference<>(listObserver);
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

    public void clear()
    {
        mDisplayList.clear();
        mDataBaseListOriginal.clear();
        getFilter().filter("");
    }

    public List<Auftrag> getmDisplayList()
    {
        return mDisplayList;
    }

    @Override
    public void notifyDataSetChanged()
    {
        Collections.sort(mDataBaseListOriginal, new CompTourenTime());

        ListObserver adapterDataEmptyListener = listObserverWeakReference.get();
        if(adapterDataEmptyListener != null)
        {
            adapterDataEmptyListener.observDataChange(mDataBaseListOriginal.isEmpty());
        }
        super.notifyDataSetChanged();
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
        this.mDataBaseListOriginal.addAll(0, list);
        getFilter().filter("");
    }

    public int getCountOriginal()
    {
        return mDataBaseListOriginal.size();
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

    @Override
    public int getCount()
    {
        return mDisplayList.size();
    }

    @Override
    public Auftrag getItem(int position)
    {
        return mDisplayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private class ViewHolderTour
    {
        private TextView textViewTourAdresse;
        private TextView textViewTourKundenname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Auftrag tour = mDisplayList.get(position);
        ViewHolderTour viewHolderTour;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_tour, parent, false);
            viewHolderTour = new ViewHolderTour();
            viewHolderTour.textViewTourKundenname = convertView.findViewById(R.id.item_tourTextViewKundenname);
            viewHolderTour.textViewTourAdresse = convertView.findViewById(R.id.item_tourTextViewAdresse);
            convertView.setTag(viewHolderTour);
        }
        else
        {
            viewHolderTour = (ViewHolderTour) convertView.getTag();
        }

        viewHolderTour.textViewTourKundenname.setText(tour.getContactDTO().getDisplayName());

        String formatedAddress = tour.getContactDTO().getFormatedAdress();
        viewHolderTour.textViewTourAdresse.setText(TextUtils.isEmpty(formatedAddress)
                ? convertView.getResources().getString(R.string.adressMissing) : formatedAddress);

        return convertView;
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
                    ContactDTO contactDTO = dataNames.getContactDTO();
                    if(contactDTO != null)
                    {
                        if (contactDTO.getDisplayName().toLowerCase().startsWith(constraint.toString())
                            || contactDTO.getFormatedAdress().startsWith(constraint.toString()))
                        {
                            FilteredArrayNames.add(dataNames);
                        }
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
