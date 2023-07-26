package esaph.filing.Board.BoardManager.MitgliederAdapter;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import esaph.filing.Account.User;
import esaph.filing.R;

public class AdapterBoardMitglieder extends BaseAdapter
{
    private Context context;
    private List<User> list = new ArrayList<>();
    private LayoutInflater inflater;

    public AdapterBoardMitglieder(Context context)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    public void setList(List<User> list)
    {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void removeUser(User userProfile)
    {
        int size = list.size();
        for (int counter = 0; counter < size; counter++)
        {
            if(userProfile.getUid().equals(list.get(counter).getUid()))
            {
                list.remove(counter);
                notifyDataSetChanged();
                break;
            }
        }
    }

    private class ViewHolderMitglied
    {
        private TextView textViewBenutzername;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        User userProfile = list.get(position);

        ViewHolderMitglied viewHolderMitglied;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_mitglied, parent, false);
            viewHolderMitglied = new ViewHolderMitglied();
            viewHolderMitglied.textViewBenutzername = convertView.findViewById(R.id.item_mitglied_textViewName);
            convertView.setTag(viewHolderMitglied);
        }
        else
        {
            viewHolderMitglied = (ViewHolderMitglied) convertView.getTag();
        }

        viewHolderMitglied.textViewBenutzername.setText(userProfile.getUsername());
        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public User getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
