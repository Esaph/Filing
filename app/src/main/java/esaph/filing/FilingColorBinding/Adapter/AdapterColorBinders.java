package esaph.filing.FilingColorBinding.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.R;
import esaph.filing.Utils.DividerView;
import esaph.filing.Utils.ListObserver;

public class AdapterColorBinders extends BaseAdapter
{
    private List<ColorBinder> mDataBaseListDisplay = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> listObserverWeakReference;

    public AdapterColorBinders(Context context,
                               ListObserver listObserver)
    {
        this.inflater = LayoutInflater.from(context);
        this.listObserverWeakReference = new WeakReference<>(listObserver);
    }

    public void clear()
    {
        mDataBaseListDisplay.clear();
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged()
    {
        ListObserver adapterDataEmptyListener = listObserverWeakReference.get();
        if(adapterDataEmptyListener != null)
        {
            adapterDataEmptyListener.observDataChange(mDataBaseListDisplay.isEmpty());
        }
        super.notifyDataSetChanged();
    }

    public void addItems(List<ColorBinder> list)
    {
        this.mDataBaseListDisplay.addAll(list);
        notifyDataSetChanged();
    }

    public int getCountOriginal()
    {
        return mDataBaseListDisplay.size();
    }

    @Override
    public int getCount()
    {
        return mDataBaseListDisplay.size();
    }

    @Override
    public ColorBinder getItem(int position)
    {
        return mDataBaseListDisplay.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private class ViewHolderTour
    {
        private EditText editTextName;
        private View dividerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ColorBinder colorBinder = mDataBaseListDisplay.get(position);
        ViewHolderTour viewHolderTour;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_color_binder, parent,false);
            viewHolderTour = new ViewHolderTour();
            viewHolderTour.editTextName = convertView.findViewById(R.id.item_color_binder_TextViewName);
            viewHolderTour.dividerView = convertView.findViewById(R.id.item_color_binder_dividerView);
            convertView.setTag(viewHolderTour);
        }
        else
        {
            viewHolderTour = (ViewHolderTour) convertView.getTag();
        }

        viewHolderTour.editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                colorBinder.setmImportance(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        viewHolderTour.editTextName.setText(colorBinder.getmImportance());
        viewHolderTour.dividerView.setBackgroundColor(colorBinder.getmColor());
        return convertView;
    }


}
