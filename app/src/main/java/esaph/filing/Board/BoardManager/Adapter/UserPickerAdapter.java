package esaph.filing.Board.BoardManager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import esaph.filing.Account.User;
import esaph.filing.R;

public class UserPickerAdapter extends BaseAdapter implements Filterable
{
    private List<User> mDisplayList = new ArrayList<>();
    private List<User> mDataBaseListOriginal = new ArrayList<>();
    private LayoutInflater inflater;
    private List<User> listSelectedUser = new ArrayList<>();

    public UserPickerAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    public void addData(List<User> list)
    {
        this.mDataBaseListOriginal.addAll(list);
        getFilter().filter("");
    }

    public List<User> getListSelectedUser()
    {
        return listSelectedUser;
    }

    @Override
    public int getCount()
    {
        return mDisplayList.size();
    }

    @Override
    public User getItem(int position) {
        return mDisplayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolderUser
    {
        private TextView textViewUsername;
        private TextView textViewNameNachname;
        private CheckBox checkBoxSelected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolderUser viewHolderUser;
        User userProfile = mDisplayList.get(position);
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            viewHolderUser = new ViewHolderUser();
            viewHolderUser.textViewUsername = convertView.findViewById(R.id.item_user_TextViewBenutzername);
            viewHolderUser.textViewNameNachname = convertView.findViewById(R.id.item_user_TextViewNameNachname);
            viewHolderUser.checkBoxSelected = convertView.findViewById(R.id.item_user_ImageViewSelected);
            convertView.setTag(viewHolderUser);
        }
        else
        {
            viewHolderUser = (ViewHolderUser) convertView.getTag();
        }

        String mNameNachname = userProfile.getGivenName() + " " + userProfile.getFamilyName();
        viewHolderUser.textViewUsername.setText(userProfile.getUsername());
        viewHolderUser.textViewNameNachname.setText(mNameNachname);
        viewHolderUser.checkBoxSelected.setChecked(listSelectedUser.contains(userProfile));

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
                mDisplayList = (List<User>) results.values;
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

                ArrayList<User> FilteredArrayNames = new ArrayList<User>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mDataBaseListOriginal.size(); i++)
                {
                    User dataNames = mDataBaseListOriginal.get(i);
                    if (dataNames.getGivenName().toLowerCase().startsWith(constraint.toString())
                            || dataNames.getUsername().toLowerCase().startsWith(constraint.toString())
                            || dataNames.getFamilyName().toLowerCase().startsWith(constraint.toString()))
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
