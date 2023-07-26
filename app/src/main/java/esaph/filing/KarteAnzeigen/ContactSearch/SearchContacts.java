package esaph.filing.KarteAnzeigen.ContactSearch;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import esaph.filing.KarteAnzeigen.ContactSearch.CursorAdapter.KundenSearchAdapter;
import esaph.filing.KundenManagement.ContactDTO;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;

public class SearchContacts extends EsaphActivity
{
    private KundenSearchAdapter kundenSearchAdapter;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.activity_search_contacts_AutoCompleteTextView);

        kundenSearchAdapter = new KundenSearchAdapter(this, R.layout.item_contact_search_layout);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                kundenSearchAdapter.setSearchInput(!TextUtils.isEmpty(s.toString()) ? s.toString() : null);
                kundenSearchAdapter.forceLoad();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextView.setAdapter(kundenSearchAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent data = new Intent();
                data.putExtra(ContactDTO.SERIAZABLE_ID, kundenSearchAdapter.getItem(position));
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
