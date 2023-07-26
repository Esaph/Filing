package esaph.filing.logging.Adapter;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import esaph.filing.R;
import esaph.filing.Utils.ListObserver;
import esaph.filing.logging.Adapter.model.EsaphLog;
import esaph.filing.logging.Adapter.sorting.EsaphLogComperator;

public class AdapterLogging extends BaseAdapter implements Filterable
{
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private List<EsaphLog> mDisplayList = new ArrayList<>();
    private List<EsaphLog> mDataBaseListOriginal = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> adapterDataEmptyListenerWeakReference;

    public AdapterLogging(Context context, ListObserver adapterDataEmptyListener)
    {
        this.inflater = LayoutInflater.from(context);
        this.adapterDataEmptyListenerWeakReference = new WeakReference<>(adapterDataEmptyListener);
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
                mDisplayList = (List<EsaphLog>) results.values;
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
                ArrayList<EsaphLog> FilteredArrayNames = new ArrayList<EsaphLog>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mDataBaseListOriginal.size(); i++)
                {
                    EsaphLog dataNames = mDataBaseListOriginal.get(i);
                    if (dataNames.getLog().toLowerCase().contains(constraint.toString())
                        || dataNames.getUsername().toLowerCase().startsWith(constraint.toString()))
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


    private void recyclerViewNotify()
    {
        Collections.sort(this.mDataBaseListOriginal, new EsaphLogComperator());

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

    public void addItems(List<EsaphLog> list)
    {
        this.mDataBaseListOriginal.addAll(list);
        getFilter().filter("");
    }

    public void addItem(EsaphLog esaphLog)
    {
        this.mDataBaseListOriginal.add(esaphLog);
        getFilter().filter("");
    }


    private class ViewHolderLogging
    {
        private TextView textViewUser;
        private TextView textViewTime;
        private TextView textViewLog;
    }

    public int getCountOriginal()
    {
        return mDataBaseListOriginal.size();
    }

    @Override
    public int getCount()
    {
        return mDisplayList.size();
    }

    public EsaphLog getItem(int position) {
        return mDisplayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        EsaphLog esaphLog = mDisplayList.get(position);
        ViewHolderLogging viewHolderLogging;
        if(convertView == null)
        {
            viewHolderLogging = new ViewHolderLogging();
            convertView = inflater.inflate(R.layout.item_log_protokoll, parent, false);
            viewHolderLogging.textViewUser = convertView.findViewById(R.id.item_log_protokoll_TextViewUsername);
            viewHolderLogging.textViewTime = convertView.findViewById(R.id.item_log_protokoll_TextViewUhrzeit);
            viewHolderLogging.textViewLog = convertView.findViewById(R.id.item_log_protokoll_TextViewLog);
            convertView.setTag(viewHolderLogging);
        }
        else
        {
            viewHolderLogging = (ViewHolderLogging) convertView.getTag();
        }

        Spannable wordtoSpan = new SpannableString(esaphLog.getLog());

        String toOb = esaphLog.getLog();
        int indexFirst = toOb.indexOf("\"");
        int indexNext = toOb.indexOf("\"", indexFirst+1);

        if(indexFirst > -1 && indexNext > indexFirst)
        {
            wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(convertView.getContext(),
                    R.color.colorAccent)),
                    indexFirst,
                    indexNext+1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }



        viewHolderLogging.textViewUser.setText(esaphLog.getUsername());
        viewHolderLogging.textViewTime.setText(simpleDateFormat.format(esaphLog.getLogTime()));
        viewHolderLogging.textViewLog.setText(wordtoSpan);

        return convertView;
    }
}