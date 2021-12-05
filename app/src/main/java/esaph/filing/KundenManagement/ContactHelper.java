package esaph.filing.KundenManagement;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContactHelper
{

    private List<Integer> getRawContactsIdList(Context context)
    {
        List<Integer> ret = new ArrayList<Integer>();

        Cursor cursor = null;
        try
        {
            ContentResolver contentResolver = context.getContentResolver();
            // Row contacts content uri( access raw_contacts table. ).
            Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
            // Return _id column in contacts raw_contacts table.
            String queryColumnArr[] = new String[]{ContactsContract.RawContacts._ID};
            // Query raw_contacts table and return raw_contacts table _id.

            cursor = contentResolver.query(rawContactUri, queryColumnArr, null, null, null);
            if(cursor!=null)
            {
                cursor.moveToFirst();
                do
                    {
                    int idColumnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                    int rawContactsId = cursor.getInt(idColumnIndex);
                    ret.add(rawContactsId);
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.e(getClass().getName(), "getRawContactsIdList() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }

        return ret;
    }


    private List<String> getContactsIdListByName(Context context, String searchInput)
    {
        List<String> ret = new ArrayList<String>();

        Cursor cursor = null;
        try
        {
            ContentResolver contentResolver = context.getContentResolver();
            // Row contacts content uri( access raw_contacts table. ).
            Uri rawContactUri = ContactsContract.Contacts.CONTENT_URI;
            // Return _id column in contacts raw_contacts table.
            String queryColumnArr[] = new String[]{ContactsContract.Contacts._ID};

            String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                    + ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%?%' ))".replace("?", searchInput != null ? searchInput : "");


            // Query raw_contacts table and return raw_contacts table _id.

            cursor = contentResolver.query(rawContactUri, queryColumnArr, select, null, null);
            if(cursor != null)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        ret.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                    }
                    while(cursor.moveToNext());
                }
            }
        }
        catch (Exception ec)
        {
            Log.e(getClass().getName(), "getContactsIdListByName() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }

        return ret;
    }



    private void getLocationAddress(ContactDTO contactDTO, ContentResolver contentResolver, String _ID)
    {
        Cursor cursor = null;

        try
        {
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + _ID,null, null);

            if(cursor != null && cursor.moveToFirst())
            {
                String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));


                contactDTO.setOtherAddressFormat(country, postcode, city, street);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLocationAddress() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }
    }

    private void getPhoneNumber(ContactDTO contactDTO, ContentResolver contentResolver, String _ID)
    {
        Cursor cursor = null;

        try
        {
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + _ID,null, null);

            if(cursor != null && cursor.moveToFirst())
            {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // Phone.TYPE == data2
                int phoneTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phoneTypeStr = getPhoneTypeString(phoneTypeInt);

                if(contactDTO.getPhoneList() == null) contactDTO.setPhoneList(new ArrayList<DataDTO>());

                DataDTO dataDTOPhone = new DataDTO();
                dataDTOPhone.setDataType(phoneTypeInt);
                dataDTOPhone.setDataValue(phoneNumber);
                contactDTO.getPhoneList().add(dataDTOPhone);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLocationAddress() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }
    }


    public List<ContactDTO> searchAllContacts(Context context, String inputSearch)
    {
        List<ContactDTO> ret = new ArrayList<ContactDTO>();
        // Get all raw contacts id list.

        List<String> contactIdList = getContactsIdListByName(context, inputSearch);

        int contactListSize = contactIdList.size();

        ContentResolver contentResolver = context.getContentResolver();

        //

        // Loop in the raw contacts list.
        for(int position=0;position<contactListSize;position++)
        {
            ContactDTO contactDTO = new ContactDTO();


            Cursor cursor = null;

            try
            {
                cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        null, ContactsContract.Contacts._ID + " = " + contactIdList.get(position), null, null);

                if(cursor != null)
                {
                    if(cursor.moveToFirst())
                    {
                        String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        contactDTO.setDisplayName(displayName);

                        /*
                        String givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));

                        String familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));



                        contactDTO.setGivenName(givenName);
                        contactDTO.setFamilyName(familyName);*/

                        getPhoneNumber(contactDTO, contentResolver, contactIdList.get(position));
                        getLocationAddress(contactDTO, contentResolver, contactIdList.get(position));
                    }
                }

            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "searchAllContacts() failed: " + ec);
            }
            finally
            {
                if(cursor != null)
                {
                    cursor.close();
                }
            }

            ret.add(contactDTO);
        }

        return ret;
    }

    private String getEmailTypeString(int dataType)
    {
        String ret = "";

        if(ContactsContract.CommonDataKinds.Email.TYPE_HOME == dataType)
        {
            ret = "Home";
        }
        else if(ContactsContract.CommonDataKinds.Email.TYPE_WORK==dataType)
        {
            ret = "Work";
        }
        return ret;
    }

    private String getPhoneTypeString(int dataType)
    {
        String ret = "";

        if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME == dataType)
        {
            ret = "Home";
        }
        else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK == dataType)
        {
            ret = "Work";
        }
        else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == dataType)
        {
            ret = "Mobile";
        }
        return ret;
    }

}