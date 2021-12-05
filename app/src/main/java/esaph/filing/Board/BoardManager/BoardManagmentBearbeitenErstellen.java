package esaph.filing.Board.BoardManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import esaph.filing.Account.User;
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.Model.BoardPolicy;
import esaph.filing.Board.BoardManager.MitgliederAdapter.AdapterBoardMitglieder;
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.FilingColorBinding.FilingColorBind;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.EsaphDialog;
import esaph.filing.workers.CreateNewBoard;
import esaph.filing.workers.UpdateBoard;

public class BoardManagmentBearbeitenErstellen extends EsaphActivity
{
    public static final int RESULT_CODE_BOARD_DATA_CHANGED = 10;
    private Board board;
    private Spinner spinner;
    private TextView textViewAddTeilnehmer;
    private EditText editTextBoardName;
    private GridView gridViewMitglieder;
    private TextView textViewFarbpallette;
    private AdapterBoardMitglieder adapterMitglieder;
    private Button buttonFertig;
    private ManagmentMode managmentMode;
    private TextView textViewBack;

    private enum ManagmentMode
    {
        EDIT, CREATE
    }

    private List<ColorBinder> generateList()
    {
        List<ColorBinder> list = new ArrayList<>();
        int[] rainbow = getResources().getIntArray(R.array.filingColors);

        for (int i = 0; i < rainbow.length; i++)
        {
            list.add(new ColorBinder("", rainbow[i]));
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_managment_bearbeiten_erstellen);

        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                board = (Board) bundle.getSerializable(Board.extra_Board);
            }
        }

        if(board == null)
        {
            board = new Board(1,
                    "",
                    BoardPolicy.POLICY_PRIVATE);
            board.setColorBinders(generateList());
            managmentMode = ManagmentMode.CREATE;
        }
        else
        {
            managmentMode = ManagmentMode.EDIT;
        }

        spinner = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_SpinnerPrivacy);
        textViewBack = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_TextViewBack);
        textViewAddTeilnehmer = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_TextViewAddTeilnehmer);
        editTextBoardName = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_EditTextBoardName);
        gridViewMitglieder = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_GridViewMitglieder);
        buttonFertig = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_ButtonFertig);
        textViewFarbpallette = findViewById(R.id.activity_board_managment_bearbeiten_erstellen_TextViewFarbpallette);

        textViewFarbpallette.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentFarb = new Intent(BoardManagmentBearbeitenErstellen.this, FilingColorBind.class);
                intentFarb.putExtra(ColorBinder.extra, (Serializable) board.getColorBinders());
                startActivityForResult(intentFarb, BoardManagmentBearbeitenErstellen.COLOR_PALETTE_CONFIGURATOR);
            }
        });


        final List<BoardPolicyItem> list = new ArrayList<>();
        list.add(new BoardPolicyItem(BoardPolicy.POLICY_PUBLIC,
                getResources().getString(R.string.board_privacy_public),
                getResources().getString(R.string.board_privacy_public_description),
                R.drawable.ic_unlocked));

        list.add(new BoardPolicyItem(BoardPolicy.POLICY_PUBLIC,
                getResources().getString(R.string.board_privacy_private),
                getResources().getString(R.string.board_privacy_private_description),
                R.drawable.ic_locked));

        AdapterPrivayChoose arrayAdapterPrivacyChoose = new AdapterPrivayChoose(getApplicationContext(),
                R.layout.item_sichtbarkeitsmodus,
                list);

        spinner.setAdapter(arrayAdapterPrivacyChoose);

        adapterMitglieder = new AdapterBoardMitglieder(getApplicationContext());
        gridViewMitglieder.setAdapter(adapterMitglieder);

        editTextBoardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                board.setmBoardName(s.toString());
                handleDoneButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textViewAddTeilnehmer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UserPicker userPicker = new UserPicker(board, new UserPicker.UserSelectedListener()
                {
                    @Override
                    public void onUsersSelected(List<User> userProfiles)
                    {
                        board.setmListMitglieder(userProfiles);
                        updateUI();
                    }
                });

                userPicker.show(getSupportFragmentManager(), UserPicker.class.getName());
            }
        });

        buttonFertig.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (managmentMode)
                {
                    case CREATE:
                        new CreateNewBoard(BoardManagmentBearbeitenErstellen.this,
                                board,
                                new CreateNewBoard.CreateNewBoardListener()
                                {
                                    @Override
                                    public void onResult(Board board, boolean success)
                                    {
                                        if(isFinishing()) return;
                                        if(success)
                                        {
                                            BoardManagmentBearbeitenErstellen.this.board = board;
                                            Intent intent = new Intent();
                                            intent.putExtra(Board.extra_Board, board);
                                            setResult(BoardManagmentBearbeitenErstellen.RESULT_CODE_BOARD_DATA_CHANGED, intent);
                                            finish();
                                        }
                                        else
                                        {
                                            new AlertDialog.Builder(BoardManagmentBearbeitenErstellen.this)
                                                    .setTitle(R.string.error_board_erstellen_title)
                                                    .setMessage(R.string.error_board_erstellen)
                                                    .show();
                                        }
                                    }
                                }).transfer();
                        break;

                    case EDIT:
                        new UpdateBoard(BoardManagmentBearbeitenErstellen.this,
                                board,
                                new UpdateBoard.UpdateBoardListener()
                                {
                                    @Override
                                    public void onResult(Board board, boolean success)
                                    {
                                        if(isFinishing()) return;
                                        if(success)
                                        {
                                            BoardManagmentBearbeitenErstellen.this.board = board;
                                            Intent intent = new Intent();
                                            intent.putExtra(Board.extra_Board, board);
                                            setResult(BoardManagmentBearbeitenErstellen.RESULT_CODE_BOARD_DATA_CHANGED, intent);
                                            finish();
                                        }
                                        else
                                        {
                                            new AlertDialog.Builder(BoardManagmentBearbeitenErstellen.this)
                                                    .setTitle(R.string.error_board_bearbeiten_title)
                                                    .setMessage(R.string.error_board_bearbeiten)
                                                    .show();
                                        }
                                    }
                                }).transferSynch();
                        break;
                }
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

        gridViewMitglieder.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final User userProfile = adapterMitglieder.getItem(position);
                if(userProfile == null) return;

                final EsaphDialog esaphDialog = EsaphDialog.EsaphDialogBuilder.getInstance()
                        .setTitle(getResources().getString(R.string.mitgliedEntfernen))
                        .setDetail(getResources().getString(R.string.mitGliedEntfernenFrage, userProfile.getUsername()))
                        .setmButtonTextPositiv(getResources().getString(R.string.yes_remove))
                        .setmButtonTextNegative(getResources().getString(R.string.cancel)).request(BoardManagmentBearbeitenErstellen.this);

                esaphDialog.getButtonPositive().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(!isFinishing())
                        {
                            adapterMitglieder.removeUser(userProfile);
                            board.getmListMitglieder().remove(userProfile);
                            esaphDialog.dismiss();
                        }
                    }
                });
            }
        });

        updateUI();
    }

    private void updateUI()
    {
        switch (managmentMode)
        {
            case EDIT:
                textViewBack.setText(getResources().getString(R.string.board_aktualisieren));
                break;

            case CREATE:
                textViewBack.setText(getResources().getString(R.string.board_erstellen));
                break;
        }

        editTextBoardName.setText(board.getmBoardName());
        adapterMitglieder.setList(board.getmListMitglieder());
        updateSpinner();
        handleDoneButtonState();
    }

    private void updateSpinner()
    {
        if(board.getBoardPolicy() == BoardPolicy.POLICY_PUBLIC)
        {
            spinner.setSelection(0);
        }
        else
        {
            spinner.setSelection(1);
        }
    }


    private void handleDoneButtonState()
    {
        if(board.getmBoardName().isEmpty())
        {
            buttonFertig.setVisibility(View.GONE);
        }
        else
        {
            buttonFertig.setVisibility(View.VISIBLE);
        }
    }


    private static final int COLOR_PALETTE_CONFIGURATOR = 102;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == COLOR_PALETTE_CONFIGURATOR)
        {
            if(data != null)
            {
                List<ColorBinder> list = (List<ColorBinder>) data.getSerializableExtra(ColorBinder.extra);
                board.setColorBinders(list);
            }
        }
    }
}
