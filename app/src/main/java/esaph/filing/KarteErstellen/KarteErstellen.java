package esaph.filing.KarteErstellen;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.ActivityListPicker;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.CardsShowingFromList.Model.Auftrag.ViewType;
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.KarteAnzeigen.ContactSearch.SearchContacts;
import esaph.filing.LabelPicker.KatalogChooser;
import esaph.filing.KundenManagement.ContactDTO;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.workers.CreateNewAuftrag;

public class KarteErstellen extends EsaphActivity implements DatePickerDialog.OnDateSetListener
{
    private Board board;
    private BoardListe boardListeSelected;
    private Auftrag auftragKarte;
    private View viewColorLine;
    private TextView textViewChangeColor;
    private TextView textViewBack;
    private TextView textViewDisplayName;
    private TextView textViewPhoneNumber;
    private TextView textViewAdresse;
    private Button buttonFertig;
    private TextView textViewDatumFrist;
    private View viewRootLayoutKunde;
    private TextView textViewListChoose;


    private EditText editTextAufgabe;
    private EditText editTextBeschreibung;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte_erstellen);

        HPreferences hPreferences = new HPreferences(getApplicationContext());

        board = hPreferences.getWorkingBench();

        auftragKarte = new Auftrag();
        auftragKarte.setmAblaufUhrzeit(System.currentTimeMillis());
        auftragKarte.setmColorCode(ContextCompat.getColor(getApplicationContext(), R.color.cardColorLime));
        auftragKarte.setViewType(ViewType.AUFTRAG_KARTE);


        textViewListChoose = findViewById(R.id.activity_karte_erstellen_TextViewListChoose);
        textViewDatumFrist = findViewById(R.id.activity_karte_erstellen_TextViewFrist);
        textViewDisplayName = (TextView) findViewById(R.id.item_contact_search_layout_TextViewKundenname);
        textViewPhoneNumber = (TextView) findViewById(R.id.item_contact_search_layout_TextViewTelefon);
        textViewAdresse = (TextView) findViewById(R.id.item_contact_search_layout_TextViewAdresse);

        editTextAufgabe = (EditText) findViewById(R.id.activity_karte_erstellen_EditTextAufgabe);
        editTextBeschreibung = (EditText) findViewById(R.id.activity_karte_erstellen_EditTextBeschreibung);
        viewRootLayoutKunde = (View) findViewById(R.id.item_contact_search_layout_LinearLayoutRoot);
        buttonFertig = (Button) findViewById(R.id.activity_karte_erstellen_ButtonFertig);

        textViewBack = (TextView) findViewById(R.id.activity_karte_erstellen_TextViewBack);
        viewColorLine = (View) findViewById(R.id.activity_karte_erstellen_ViewCardColor);
        textViewChangeColor = (TextView) findViewById(R.id.activity_karte_erstellen_TextViewChangeColor);
        textViewChangeColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(KarteErstellen.this, KatalogChooser.class);
                startActivityForResult(intent, KarteErstellen.REQUEST_CODE_SELECT_KATALOG);
            }
        });

        editTextAufgabe.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
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
                auftragKarte.setmBeschreibung(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {
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

        buttonFertig.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new CreateNewAuftrag(KarteErstellen.this,
                        auftragKarte,
                        boardListeSelected,
                        new CreateNewAuftrag.CreateNewAuftragListener()
                        {
                            @Override
                            public void onResult(Auftrag auftragKarte, boolean success)
                            {
                                if(success)
                                {
                                    KarteErstellen.this.auftragKarte = auftragKarte;
                                    Intent intent = new Intent();
                                    intent.putExtra("LID", boardListeSelected.getmBoardListeId());
                                    intent.putExtra(Auftrag.SERIAZABLE_ID, auftragKarte);
                                    setResult(1, intent);
                                    finish();
                                }
                                else
                                {
                                    new AlertDialog.Builder(KarteErstellen.this).setTitle(R.string.error_karte_erstellen_title)
                                            .setMessage(R.string.error_karte_erstellen)
                                            .show();
                                }
                            }
                        }).transfer();
            }
        });

        textViewListChoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(KarteErstellen.this, ActivityListPicker.class);
                intent.putExtra(Board.extra_Board, board);
                startActivityForResult(intent, KarteErstellen.REQUEST_CODE_LIST_CHOOSE);
            }
        });

        viewRootLayoutKunde.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentChangeClient = new Intent(KarteErstellen.this, SearchContacts.class);
                startActivityForResult(intentChangeClient, KarteErstellen.REQUEST_CODE_CHOOSE_CLIENT);
            }
        });

        textViewDatumFrist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                new DatePickerDialog(KarteErstellen.this,KarteErstellen.this, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        updateUI();
        setCardColor();
    }


    private boolean wantedZeitreisen = false;
    private void updateUI()
    {
        if(boardListeSelected != null)
        {
            textViewListChoose.setText(boardListeSelected.getmListenName());
        }

        if(wantedZeitreisen)
        {
            textViewDatumFrist.setText(getResources().getString(R.string.zeitReisen));
        }
        else
        {
            textViewDatumFrist.setText(auftragKarte.getmAblaufUhrzeit() >= System.currentTimeMillis() ? simpleDateFormat.format(auftragKarte.getmAblaufUhrzeit()) :
                    getResources().getString(R.string.fristDatumAuswaehlen));
        }



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
        }
        else
        {
            textViewDisplayName.setText(getResources().getString(R.string.tapForClientChoose));
        }
    }

    private void setCardColor()
    {
        textViewListChoose.setTextColor(auftragKarte.getmColorCode());
        textViewListChoose.setHintTextColor(auftragKarte.getmColorCode());
        textViewChangeColor.setTextColor(auftragKarte.getmColorCode());
        viewColorLine.setBackgroundColor(auftragKarte.getmColorCode());
        buttonFertig.setBackgroundColor(auftragKarte.getmColorCode());
    }

    private static final int REQUEST_CODE_SELECT_KATALOG = 241;
    private static final int REQUEST_CODE_LIST_CHOOSE = 1005;
    private static final int REQUEST_CODE_CHOOSE_CLIENT = 566;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            if(requestCode == KarteErstellen.REQUEST_CODE_CHOOSE_CLIENT)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    ContactDTO contactDTO = (ContactDTO) bundle.getSerializable(ContactDTO.SERIAZABLE_ID);
                    if(contactDTO != null)
                    {
                        this.auftragKarte.setContactDTO(contactDTO);
                        updateUI();
                    }
                }
            }
            else if(requestCode == KarteErstellen.REQUEST_CODE_LIST_CHOOSE)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    BoardListe boardListe = (BoardListe) bundle.getSerializable(BoardListe.extra_BoardListe);
                    if(boardListe != null)
                    {
                        this.boardListeSelected = boardListe;
                        updateUI();
                    }
                }
            }
            else if(requestCode == KarteErstellen.REQUEST_CODE_SELECT_KATALOG)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    ColorBinder colorBinder = (ColorBinder) bundle.getSerializable(ColorBinder.extra);
                    if(colorBinder != null)
                    {
                        auftragKarte.setmColorCode(colorBinder.getmColor());
                        setCardColor();
                    }
                }
            }
        }
    }


    private void checkIfCommitChanges()
    {
        if(!editTextAufgabe.getText().toString().isEmpty() && !textViewListChoose.getText().toString().isEmpty())
        {
            buttonFertig.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonFertig.setVisibility(View.GONE);
        }
    }


    private Intent intentSavedCallingIntent;
    private final static int REQUEST_PERMISSION_PHONE = 777;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == KarteErstellen.REQUEST_PERMISSION_PHONE)
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


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(KarteErstellen.this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
                auftragKarte.setmAblaufUhrzeit(calendar.getTimeInMillis());

                wantedZeitreisen = calendar.getTimeInMillis() < System.currentTimeMillis();

                updateUI();
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true);//Yes 24 hour time

        mTimePicker.setTitle(getResources().getString(R.string.pickTime));
        mTimePicker.show();
    }
}