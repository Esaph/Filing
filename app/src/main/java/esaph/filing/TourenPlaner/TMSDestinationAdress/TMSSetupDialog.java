package esaph.filing.TourenPlaner.TMSDestinationAdress;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import esaph.filing.Account.HPreferences;
import esaph.filing.R;
import esaph.filing.TourenPlaner.TMSDestinationAdress.background.LoadLatestAdresses;
import esaph.filing.TourenPlaner.TMSDestinationAdress.background.SaveNewAdress;
import esaph.filing.TourenPlaner.TMSDestinationAdress.model.MostUsedAdress;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.ListObserver;

public class TMSSetupDialog extends EsaphActivity implements ListObserver
{
    private EditText editTextDestAdress;
    private ImageButton imageButtonAddAdress;
    private Button buttonLos;
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tms_setup_dialog);

        editTextDestAdress = findViewById(R.id.layout_tms_setup_dialog_EdittextDestinationAdress);
        imageButtonAddAdress = findViewById(R.id.layout_tms_setup_dialog_ImageButtonAddAdress);
        spinner = findViewById(R.id.layout_tms_setup_dialog_spinner_adresses);
        buttonLos = findViewById(R.id.layout_tms_setup_dialog_ButtonLos);

        imageButtonAddAdress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editTextDestAdress.setEnabled(false);
                String newAdress = editTextDestAdress.getText().toString();
                if(!newAdress.isEmpty())
                {
                    new SaveNewAdress(getApplicationContext(),
                            new MostUsedAdress(editTextDestAdress.getText().toString()),
                            new SaveNewAdress.SaveNewAdressDoneListener()
                            {
                                @Override
                                public void onAdressSaved(boolean success)
                                {
                                    if(isFinishing()) return;

                                    editTextDestAdress.setEnabled(true);
                                    editTextDestAdress.setText("");

                                    if(adapterLastAdresses != null)
                                    {
                                        adapterLastAdresses.clear();
                                        loadListAdresses();
                                    }
                                }
                            }).execute();
                }
            }
        });

        editTextDestAdress.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                imageButtonAddAdress.setVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        buttonLos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    MostUsedAdress mostUsedAdress = (MostUsedAdress) spinner.getItemAtPosition(spinner.getSelectedItemPosition());
                    Context context = getApplicationContext();
                    if(context != null)
                    {
                        new HPreferences(context).setTMSDestination(mostUsedAdress.getAdress());
                        setResult(0, new Intent());
                        finish();
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "imageButtonAddAdress() failed: " + ec);
                }
            }
        });

        adapterLastAdresses = new AdapterLastAdresses(getApplicationContext(),
                R.layout.item_last_adresses, new ArrayList<MostUsedAdress>(), TMSSetupDialog.this);

        spinner.setAdapter(adapterLastAdresses);

        loadListAdresses();
    }

    private AdapterLastAdresses adapterLastAdresses;
    private void loadListAdresses()
    {
        new LoadLatestAdresses(getApplicationContext(), new LoadLatestAdresses.LoadLatestAdressesListener()
        {
            @Override
            public void onLoaded(List<MostUsedAdress> list)
            {
                if(isFinishing()) return;

                adapterLastAdresses.addAll(list);

                HPreferences preferences = new HPreferences(getApplicationContext());
                try
                {
                    String tmsDestination = preferences.getTMSDestination();
                    if(tmsDestination != null && !tmsDestination.isEmpty())
                    {
                        int pos = adapterLastAdresses.getPosition(new MostUsedAdress(tmsDestination));
                        if(pos > -1)
                        {
                            spinner.setSelection(pos);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            HPreferences preferences = new HPreferences(getApplicationContext());
            if(preferences.getTMSDestination() != null
                    && !preferences.getTMSDestination().isEmpty())
            {
                finish();
            }
            else
            {
                Toast.makeText(TMSSetupDialog.this, getResources().getString(R.string.toast_need_adress), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "TMSSetupdialog onBackPressed(): " + ec);
        }
    }

    @Override
    public void observDataChange(boolean isEmpty)
    {
        if(isFinishing()) return;

        buttonLos.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}