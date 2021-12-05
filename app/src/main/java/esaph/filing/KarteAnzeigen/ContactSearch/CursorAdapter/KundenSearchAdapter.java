package esaph.filing.KarteAnzeigen.ContactSearch.CursorAdapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

import esaph.filing.KundenManagement.ContactDTO;
import esaph.filing.KundenManagement.ContactHelper;
import esaph.filing.R;

public class KundenSearchAdapter extends ArrayAdapter implements LoaderManager.LoaderCallbacks<List<ContactDTO>>
{
    private AppCompatActivity activity;
    private List<ContactDTO> contactDTOList;
    private String searchInput;

    public KundenSearchAdapter(@NonNull AppCompatActivity activity, int resource) {
        super(activity, resource);
        this.activity = activity;
        this.contactDTOList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }

    public void forceLoad()
    {
        // Call getSupportLoaderManager and store it in a LoaderManager variable
        LoaderManager loaderManager = LoaderManager.getInstance(activity);
        // Get our Loader by calling getLoader and passing the ID we specified
        Loader<List<ContactDTO>> loader = loaderManager.getLoader(0);
        // If the Loader was null, initialize it. Else, restart it.
        if(loader==null){
            loaderManager.initLoader(0, null, this).forceLoad();
        }else{
            loaderManager.restartLoader(0, null, this).forceLoad();
        }
    }


    private class ViewHolderContact
    {
        private TextView textViewDisplayName;
        private TextView textViewPhoneNumber;
        private TextView textViewAdresse;
    }

    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ContactDTO contactDTO = contactDTOList.get(position);
        ViewHolderContact viewHolderContact;
        if(convertView == null)
        {
            viewHolderContact = new ViewHolderContact();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_search_layout, parent, false);
            viewHolderContact.textViewDisplayName = (TextView) convertView.findViewById(R.id.item_contact_search_layout_TextViewKundenname);
            viewHolderContact.textViewPhoneNumber = (TextView) convertView.findViewById(R.id.item_contact_search_layout_TextViewTelefon);
            viewHolderContact.textViewAdresse = (TextView) convertView.findViewById(R.id.item_contact_search_layout_TextViewAdresse);
            convertView.setTag(viewHolderContact);
        }
        else
        {
            viewHolderContact = (ViewHolderContact) convertView.getTag();
        }


        viewHolderContact.textViewDisplayName.setText(contactDTO.getDisplayName());


        viewHolderContact.textViewPhoneNumber.setText(!contactDTO.getPhoneList().isEmpty()
                ? contactDTO.getPhoneList().get(0).getDataValue() : convertView.getResources().getString(R.string.phoneMissing));
        viewHolderContact.textViewAdresse.setText(!TextUtils.isEmpty(contactDTO.getFormatedAdress())
                ? contactDTO.getFormatedAdress() : convertView.getResources().getString(R.string.adressMissing));

        return convertView;
    }

    @Override
    public int getCount() {
        return contactDTOList.size();
    }

    @Override
    public ContactDTO getItem(int position) {
        return contactDTOList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @NonNull
    @Override
    public Loader<List<ContactDTO>> onCreateLoader(int id, @Nullable Bundle args)
    {
        return new AsyncTaskLoader<List<ContactDTO>>(activity) {
            @Override
            public List<ContactDTO> loadInBackground()
            {
                return new ContactHelper().searchAllContacts(activity, searchInput);

                /*
                List<ContactDTO> contactDTOList = new ArrayList<>();
                try
                {
                    String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                            + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                            + ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%?%' ))".replace("?", searchInput != null ? searchInput : "");

                    System.out.println("Searchwueryx: " + select + " input: " + searchInput);

                    String[] projection = new String[]{
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts.CONTACT_STATUS,
                            ContactsContract.Contacts.CONTACT_PRESENCE,
                            ContactsContract.Contacts.PHOTO_ID,
                            ContactsContract.Contacts.LOOKUP_KEY,
                    };


                    ContentResolver cr = activity.getContentResolver();
                    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            projection,
                            select,
                            null,
                            ContactsContract.Contacts.DISPLAY_NAME);

                    if (cursor != null && cursor.moveToFirst())
                    {
                        ContactDTO contactDTO = new ContactDTO();

                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                        List<DataDTO> numberList = new ArrayList<>();
                        while (phones != null && phones.moveToNext())
                        {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            DataDTO dataDTO = new DataDTO();
                            dataDTO.setDataType(type);
                            dataDTO.setDataValue(number);
                            numberList.add(dataDTO);
                        }

                        contactDTO.setPhoneList(numberList);
                        contactDTOList.add(contactDTO);

                        phones.close();
                    }
                    cursor.close();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "RunnableSearchContact() failed: " + ec);
                }
                return contactDTOList;*/
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ContactDTO>> loader, List<ContactDTO> data)
    {
        swapData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ContactDTO>> loader) {
        swapData(null);
    }

    private void swapData(List<ContactDTO> list)
    {
        if(list == null)
        {
            this.contactDTOList.clear();
            return;
        }

        this.contactDTOList = list;
        notifyDataSetChanged();
    }
}