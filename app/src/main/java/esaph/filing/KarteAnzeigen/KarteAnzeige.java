package esaph.filing.KarteAnzeigen;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.KarteAnzeigen.ContactSearch.SearchContacts;
import esaph.filing.LabelPicker.KatalogChooser;
import esaph.filing.KundenManagement.ContactDTO;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.TourenPlaner.EsaphGoogleMapsRequestBuilder;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.workers.UpdateAuftrag;

public class KarteAnzeige extends EsaphActivity implements DatePickerDialog.OnDateSetListener
{
    private BoardListe boardListe;
    private Auftrag auftragKarte;
    private View viewColorLine;
    private TextView textViewChangeColor;
    private TextView textViewBack;
    private ImageButton imageButtonAnrufen;
    private ImageButton imageButtonRouteStarten;
    private TextView textViewDisplayName;
    private TextView textViewPhoneNumber;
    private TextView textViewAdresse;
    private TextView textViewDatumFrist;
    private View viewRootLayoutKunde;
    private Button buttonFertigOrUpdate;

    private EditText editTextAufgabe;
    private EditText editTextBeschreibung;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte_anzeige);
        final Intent intent = getIntent();
        if(intent != null)
        {
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                auftragKarte = (Auftrag) bundle.getSerializable(Auftrag.SERIAZABLE_ID);
                boardListe = (BoardListe) bundle.getSerializable(BoardListe.extra_BoardListe);
            }
        }

        textViewDatumFrist = findViewById(R.id.activity_karte_anzeigen_TextViewFrist);
        textViewDisplayName = (TextView) findViewById(R.id.item_contact_search_layout_TextViewKundenname);
        textViewPhoneNumber = (TextView) findViewById(R.id.item_contact_search_layout_TextViewTelefon);
        textViewAdresse = (TextView) findViewById(R.id.item_contact_search_layout_TextViewAdresse);

        buttonFertigOrUpdate = (Button) findViewById(R.id.activity_karten_anzeigen_ButtonFertig);

        imageButtonAnrufen = (ImageButton) findViewById(R.id.activity_karten_anzeigen_ImageButtonAnrufen);
        imageButtonRouteStarten = (ImageButton) findViewById(R.id.activity_karten_anzeigen_ImageButtonRouteStarten);

        editTextAufgabe = (EditText) findViewById(R.id.activity_karten_anzeigen_EditTextAufgabe);
        editTextBeschreibung = (EditText) findViewById(R.id.activity_karten_anzeigen_EditTextBeschreibung);
        viewRootLayoutKunde = (View) findViewById(R.id.item_contact_search_layout_LinearLayoutRoot);

        textViewBack = (TextView) findViewById(R.id.activity_karten_anzeigen_TextViewBack);
        viewColorLine = (View) findViewById(R.id.activity_karten_anzeigen_ViewCardColor);
        textViewChangeColor = (TextView) findViewById(R.id.activity_karten_anzeigen_TextViewChangeColor);
        textViewChangeColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(KarteAnzeige.this, KatalogChooser.class);
                startActivityForResult(intent, KarteAnzeige.REQUEST_CODE_SELECT_KATALOG);
            }
        });

        editTextAufgabe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                updatedElement = true;
                auftragKarte.setmAufgabe(s.toString());
                checkIfCommitChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextBeschreibung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                updatedElement = true;
                auftragKarte.setmBeschreibung(s.toString());
                checkIfCommitChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        imageButtonAnrufen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ContextCompat.checkSelfPermission(KarteAnzeige.this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    ContactDTO contactDTO = auftragKarte.getContactDTO();
                    if(contactDTO != null && !contactDTO.getPhoneList().isEmpty())
                    {
                        Intent intentStartcall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactDTO.getPhoneList().get(0).getDataValue()));
                        startActivity(intentStartcall);
                    }
                }
                else
                {
                    ContactDTO contactDTO = auftragKarte.getContactDTO();
                    if(contactDTO != null && !contactDTO.getPhoneList().isEmpty())
                    {
                        intentSavedCallingIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactDTO.getPhoneList().get(0).getDataValue()));

                        ActivityCompat.requestPermissions(KarteAnzeige.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                KarteAnzeige.REQUEST_PERMISSION_PHONE);
                    }
                }
            }
        });

        imageButtonRouteStarten.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ContactDTO contactDTO = auftragKarte.getContactDTO();
                if(contactDTO != null && !TextUtils.isEmpty(contactDTO.getFormatedAdress()))
                {
                    startActivity(EsaphGoogleMapsRequestBuilder.obtain(contactDTO.getFormatedAdress()).build());
                }
            }
        });

        viewRootLayoutKunde.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentChangeClient = new Intent(KarteAnzeige.this, SearchContacts.class);
                startActivityForResult(intentChangeClient, KarteAnzeige.REQUEST_CODE_CHOOSE_CLIENT);
            }
        });

        textViewDatumFrist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                new DatePickerDialog(KarteAnzeige.this, KarteAnzeige.this, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonFertigOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateAuftrag(KarteAnzeige.this,
                        auftragKarte,
                        boardListe,
                        new UpdateAuftrag.UpdateAuftragListener()
                        {
                            @Override
                            public void onResult(Auftrag auftragKarte, boolean success)
                            {
                                if(success)
                                {
                                    updatedElement = false;
                                    Intent intent = new Intent();
                                    intent.putExtra(Auftrag.SERIAZABLE_ID, auftragKarte);
                                    setResult(1, intent);
                                    finish();
                                }
                                else
                                {
                                    new AlertDialog.Builder(KarteAnzeige.this).setTitle(R.string.error_karte_update_title)
                                            .setMessage(R.string.error_karte_update)
                                            .show();
                                }
                            }
                        }).transferSynch();
            }
        });

        updateUI();
        setCardColor();
    }


    private void updateUI()
    {
        textViewDatumFrist.setText(auftragKarte.getmAblaufUhrzeit() > System.currentTimeMillis() ? simpleDateFormat.format(auftragKarte.getmAblaufUhrzeit()) :
                getResources().getString(R.string.fristDatumAuswaehlen));
        editTextAufgabe.setText(auftragKarte.getmAufgabe());
        editTextBeschreibung.setText(auftragKarte.getmBeschreibung());

        ContactDTO contactDTO = auftragKarte.getContactDTO();
        if(contactDTO != null)
        {
            textViewDisplayName.setText(contactDTO.getDisplayName());
            textViewPhoneNumber.setText(!contactDTO.getPhoneList().isEmpty()
                    ? contactDTO.getPhoneList().get(0).getDataValue() : getResources().getString(R.string.phoneMissing));

            textViewAdresse.setText(!TextUtils.isEmpty(contactDTO.getFormatedAdress())
                    ? contactDTO.getFormatedAdress() : getResources().getString(R.string.adressMissing));

            if(!TextUtils.isEmpty(contactDTO.getFormatedAdress()))
            {
                imageButtonRouteStarten.setVisibility(View.VISIBLE);
            }

            if(!contactDTO.getPhoneList().isEmpty())
            {
                imageButtonAnrufen.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            textViewDisplayName.setText(getResources().getString(R.string.tapForClientChoose));
            imageButtonAnrufen.setVisibility(View.GONE);
            imageButtonRouteStarten.setVisibility(View.GONE);
        }

        checkIfCommitChanges();
    }


    private void setCardColor()
    {
        textViewChangeColor.setTextColor(auftragKarte.getmColorCode());
        viewColorLine.setBackgroundColor(auftragKarte.getmColorCode());
        buttonFertigOrUpdate.setBackgroundColor(auftragKarte.getmColorCode());
    }

    private final static int REQUEST_CODE_CHOOSE_CLIENT = 566;
    private static final int REQUEST_CODE_SELECT_KATALOG = 241;

    private boolean updatedElement = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            if(requestCode == KarteAnzeige.REQUEST_CODE_CHOOSE_CLIENT)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    ContactDTO contactDTO = (ContactDTO) bundle.getSerializable(ContactDTO.SERIAZABLE_ID);
                    if(contactDTO != null)
                    {
                        updatedElement = true;
                        this.auftragKarte.setContactDTO(contactDTO);
                        updateUI();
                    }
                }
            }
            else if(requestCode == KarteAnzeige.REQUEST_CODE_SELECT_KATALOG)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    ColorBinder colorBinder = (ColorBinder) bundle.getSerializable(ColorBinder.extra);
                    if(colorBinder != null)
                    {
                        auftragKarte.setmColorCode(colorBinder.getmColor());
                        updatedElement = true;
                        setCardColor();
                        checkIfCommitChanges();
                    }
                }
            }
        }
    }


    private void checkIfCommitChanges()
    {
        if(updatedElement)
        {
            buttonFertigOrUpdate.setVisibility(View.VISIBLE);
            buttonFertigOrUpdate.setEnabled(true);
        }
        else
        {
            buttonFertigOrUpdate.setVisibility(View.INVISIBLE);
            buttonFertigOrUpdate.setEnabled(false);
        }
    }


    private Intent intentSavedCallingIntent;
    private final static int REQUEST_PERMISSION_PHONE = 777;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == KarteAnzeige.REQUEST_PERMISSION_PHONE)
        {
            boolean allPermissionGranted = true;
            for (int result :
                    grantResults)
            {
                if(result != PackageManager.PERMISSION_GRANTED)
                {
                    allPermissionGranted = false;
                    break;
                }
            }

            if(allPermissionGranted && intentSavedCallingIntent != null)
            {
                startActivity(intentSavedCallingIntent);
                intentSavedCallingIntent = null;
            }
        }
    }


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        updatedElement = true;
        auftragKarte.setmAblaufUhrzeit(calendar.getTimeInMillis());
        updateUI();
    }
}