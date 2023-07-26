package esaph.filing.LabelPicker.Adapter;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.R;
import esaph.filing.Utils.ListObserver;

public class AdapterKatalog extends BaseAdapter
{
    private List<ColorBinder> mDataBaseListDisplay = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> listObserverWeakReference;

    public AdapterKatalog(Context context,
                         ListObserver listObserver)
    {
        this.inflater = LayoutInflater.from(context);
        this.listObserverWeakReference = new WeakReference<>(listObserver);
    }

    public List<ColorBinder> getmDataBaseListDisplay()
    {
        return mDataBaseListDisplay;
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
        private TextView textViewName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ColorBinder colorBinder = mDataBaseListDisplay.get(position);
        ViewHolderTour viewHolderTour;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_katalog, parent, false);
            viewHolderTour = new ViewHolderTour();
            viewHolderTour.textViewName = convertView.findViewById(R.id.item_katalog_TextViewName);
            convertView.setTag(viewHolderTour);
        }
        else
        {
            viewHolderTour = (ViewHolderTour) convertView.getTag();
        }

        viewHolderTour.textViewName.setText(colorBinder.getmImportance());
        viewHolderTour.textViewName.setBackgroundResource(R.drawable.background_card_color);

        Drawable background = viewHolderTour.textViewName.getBackground().mutate(); //Should use mutate, to prevent sharing the drawablestate!
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(colorBinder.getmColor());
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(colorBinder.getmColor());
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(colorBinder.getmColor());
        }

        return convertView;
    }


}
