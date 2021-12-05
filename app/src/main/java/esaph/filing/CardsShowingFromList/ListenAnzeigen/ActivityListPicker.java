package esaph.filing.CardsShowingFromList.ListenAnzeigen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import esaph.elib.esaphcommon.dialog.Builder.ESDialogRequestBuilder;
import esaph.elib.esaphcommon.dialog.ESDialog;
import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.Adapter.AdapterListenPickerAnzeigen;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.DeleteListe;
import esaph.filing.workers.LoadMyListFromBoard;

public class ActivityListPicker extends EsaphActivity implements ListObserver,
        LoadMyListFromBoard.DataLoadListenerBoardListenGetter,
        LoadingStateHandler.LoadingStateActionListener
{
    public static final int RESULT_CODE_LIST_SELECTED = 7;
    private AdapterListenPickerAnzeigen adapterListenPickerAnzeigen;
    private LoadingStateHandler loadingStateHandler;
    private EditText editTextSearch;
    private ListView listView;
    private Board board;

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        adapterListenPickerAnzeigen = null;
        loadingStateHandler = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_picker);

        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                board = (Board) bundle.getSerializable(Board.extra_Board);
            }
        }

        editTextSearch = findViewById(R.id.activity_list_picker_EditTextSearch);
        listView = findViewById(R.id.activity_list_picker_ListView);
        loadingStateHandler = findViewById(R.id.activity_list_picker_LoadingStateHandler);

        loadingStateHandler.setLoadingStateActionListener(this);

        TextView textViewCreateList = findViewById(R.id.activity_list_picker_TextViewListeErstellen);
        textViewCreateList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogListeErstellen dialogListeErstellen = new DialogListeErstellen(board, new CreateNewListListener()
                {
                    @Override
                    public void onResult(BoardListe boardListe, boolean success)
                    {
                        if(success)
                        {
                            loadMore();
                        }
                    }
                });

                dialogListeErstellen.show(getSupportFragmentManager(),
                    DialogListeErstellen.class.getName());
            }
        });

        TextView textViewBack = findViewById(R.id.activity_list_picker_TextViewBack);
        textViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        adapterListenPickerAnzeigen = new AdapterListenPickerAnzeigen(getApplicationContext(),
                this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BoardListe boardListe = adapterListenPickerAnzeigen.getItem(position);
                new HPreferences(getApplicationContext()).setWorkingList(boardListe);
                Intent intent = new Intent();
                intent.putExtra(BoardListe.extra_BoardListe, boardListe);
                setResult(ActivityListPicker.RESULT_CODE_LIST_SELECTED, intent);
                finish();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s != null)
                {
                    adapterListenPickerAnzeigen.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerForContextMenu(listView);

        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void loadMore() {
                ActivityListPicker.this.loadMore();
            }
        });

        listView.setAdapter(adapterListenPickerAnzeigen);

        loadMore();
    }


    private void loadMore()
    {
        if(isFinishing()) return;

        new LoadMyListFromBoard(getApplicationContext(),
                loadingStateHandler, board, this)
        {
            @Override
            public int getCount()
            {
                if(isFinishing())
                {
                    return -1;
                }
                return adapterListenPickerAnzeigen.getCountOriginal();
            }
        }.transferSynch();
    }


    @Override
    public void observDataChange(boolean isEmpty)
    {
        if(isFinishing()) return;
        if(isEmpty)
        {
            loadingStateHandler.showMessage(R.drawable.ic_break, R.string.no_card_data);
        }
        else
        {
            loadingStateHandler.hideMessage();
        }
    }

    @Override
    public void onDataLoaded(List<BoardListe> data, boolean success)
    {
        if(!isFinishing())
        {
            if(success)
            {
                adapterListenPickerAnzeigen.addItems(data);
            }
            else
            {
                loadingStateHandler.showMessageTryAgain(R.drawable.ic_warning, R.string.error_loading, new LoadingStateHandler.LoadingStateActionListener() {
                    @Override
                    public void onAction() {
                        loadMore();
                    }
                });
            }
        }
    }

    @Override
    public void onAction()
    {
        loadMore();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.activity_list_picker_ListView)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            case R.id.menuDeleteList:
                removeList(adapterListenPickerAnzeigen.getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void removeList(final BoardListe boardListe)
    {
        ESDialog esDialog = ESDialogRequestBuilder.obtain().setmTitleText(R.string.dialog_delete_board_liste, R.color.colorBlack)
        .setmDetailsText(R.string.bestaetigen, R.color.colorBlack)
        .setmNegativeButtonText(R.string.abbrechen, R.color.colorPrimaryDark, new ESDialog.EDClickListener()
        {
            @Override
            public void onClicked(View view, DialogFragment dialogFragment)
            {
                dialogFragment.dismiss();
            }
        }).setmPositiveButtonText(R.string.bestaetigen, R.color.colorAccent, new ESDialog.EDClickListener()
                {
                    @Override
                    public void onClicked(View view, DialogFragment dialogFragment)
                    {
                        dialogFragment.dismiss();
                        new DeleteListe(ActivityListPicker.this,
                                boardListe,
                                new DeleteListe.DeleteListeListener()
                                {
                                    @Override
                                    public void onResult(BoardListe boardListe, boolean success)
                                    {
                                        if(!isFinishing())
                                        {
                                            if(success)
                                            {
                                                adapterListenPickerAnzeigen.removeItem(boardListe);
                                            }
                                            else
                                            {
                                                new AlertDialog.Builder(ActivityListPicker.this)
                                                        .setTitle(R.string.error_list_delete_title)
                                                        .setMessage(R.string.error_list_delete)
                                                        .show();
                                            }
                                        }
                                    }
                                })
                                .transferSynch();
                    }
                }).build();

        esDialog.show(getSupportFragmentManager(), ESDialog.class.getName());
    }


}
