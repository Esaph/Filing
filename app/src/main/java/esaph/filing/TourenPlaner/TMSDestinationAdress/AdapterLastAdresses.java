package esaph.filing.TourenPlaner.TMSDestinationAdress;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import esaph.filing.R;
import esaph.filing.TourenPlaner.TMSDestinationAdress.background.RemoveAdress;
import esaph.filing.TourenPlaner.TMSDestinationAdress.model.MostUsedAdress;
import esaph.filing.Utils.ListObserver;

public class AdapterLastAdresses extends ArrayAdapter
{
    private LayoutInflater inflater;
    private List<MostUsedAdress> list;
    private ListObserver listObserver;

    public AdapterLastAdresses(@NonNull Context context, int resource, List<MostUsedAdress> list, ListObserver listObserver)
    {
        super(context, resource, list);
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.listObserver = listObserver;
    }

    private class ViewHolderMostUsedAdress
    {
        private TextView textViewAdress;
        private ImageView imageViewDeleteAdress;
    }


    public void addAll(List<MostUsedAdress> items)
    {
        list.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged()
    {
        if(listObserver != null)
        {
            listObserver.observDataChange(list.isEmpty());
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getPosition(@Nullable Object item)
    {
        for (int counter = 0; counter < list.size(); counter++)
        {
            if(item instanceof MostUsedAdress && item.equals(list.get(counter)))
            {
                return counter;
            }
        }
        return -1;
    }

    private View getSpinnerLayout(int position, View convertView, ViewGroup parent)
    {
        final MostUsedAdress mostUsedAdress = list.get(position);
        ViewHolderMostUsedAdress viewHolderMostUsedAdress;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_last_adresses, parent, false);
            viewHolderMostUsedAdress = new ViewHolderMostUsedAdress();
            viewHolderMostUsedAdress.textViewAdress = convertView.findViewById(R.id.item_last_adresses_TextViewAdress);
            viewHolderMostUsedAdress.imageViewDeleteAdress = convertView.findViewById(R.id.item_last_adresses_ImageViewDelete);
            convertView.setTag(viewHolderMostUsedAdress);
        }
        else
        {
            viewHolderMostUsedAdress = (ViewHolderMostUsedAdress) convertView.getTag();
        }

        viewHolderMostUsedAdress.textViewAdress.setText(mostUsedAdress.getAdress());
        viewHolderMostUsedAdress.imageViewDeleteAdress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new RemoveAdress(getContext(),
                        mostUsedAdress,
                        new RemoveAdress.RemoveAdressDoneListener()
                        {
                            @Override
                            public void onAdressRemoved(boolean success)
                            {
                                if(success)
                                {
                                    list.remove(mostUsedAdress);
                                    notifyDataSetChanged();
                                }
                            }
                        }).execute();
            }
        });

        Glide.with(viewHolderMostUsedAdress.imageViewDeleteAdress.getContext())
                .load(R.drawable.ic_delete)
                .into(viewHolderMostUsedAdress.imageViewDeleteAdress);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return getSpinnerLayout(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return getSpinnerLayout(position, convertView, parent);
    }
}