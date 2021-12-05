package esaph.filing.Board.BoardsAnzeigen;

import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Adapter.AdapterBoardsAnzeigen;
import esaph.filing.Board.BoardManager.BoardManagmentBearbeitenErstellen;
import esaph.filing.Board.Model.Board;
import esaph.filing.R;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.DeleteBoard;
import esaph.filing.workers.LoadMyBoards;

public class ActivityBoardPicker extends EsaphActivity implements
        ListObserver,
        LoadMyBoards.DataLoadListenerBoardList,
        LoadingStateHandler.LoadingStateActionListener
{
    public static final int RESULT_CODE_BOARD_SELECTED = 6;
    private AdapterBoardsAnzeigen adapterBoardsAnzeigen;
    private LoadingStateHandler loadingStateHandler;
    private TextView textViewBack;
    private EditText editTextSearch;
    private ListView listViewBoards;
    private TextView textViewBoardErstellen;

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        adapterBoardsAnzeigen = null;
        loadingStateHandler = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_picker);

        textViewBack = findViewById(R.id.activity_board_picker_textViewUeberschrift);
        textViewBoardErstellen = findViewById(R.id.activity_board_picker_TextViewBoardErstellen);
        editTextSearch = findViewById(R.id.activity_board_picker_EditTextSearch);
        listViewBoards = findViewById(R.id.activity_board_picker_ListViewBoards);
        loadingStateHandler = findViewById(R.id.activity_board_picker_LoadingStateHandler);
        loadingStateHandler.setLoadingStateActionListener(this);

        textViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        textViewBoardErstellen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ActivityBoardPicker.this, BoardManagmentBearbeitenErstellen.class);
                startActivityForResult(intent, ActivityBoardPicker.REQUEST_CODE_SHOW_BOARD_ERSTELLEN);
            }
        });

        adapterBoardsAnzeigen = new AdapterBoardsAnzeigen(getApplicationContext(),
                this);

        editTextSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s != null)
                {
                    adapterBoardsAnzeigen.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        listViewBoards.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Board board = adapterBoardsAnzeigen.getItem(position);
                new HPreferences(getApplicationContext()).setWorkingBench(board);
                Intent intent = new Intent();
                intent.putExtra(Board.extra_Board, board);
                setResult(ActivityBoardPicker.RESULT_CODE_BOARD_SELECTED, intent);
                finish();
            }
        });

        registerForContextMenu(listViewBoards);

        listViewBoards.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public void loadMore()
            {
                ActivityBoardPicker.this.loadMore();
            }
        });

        listViewBoards.setAdapter(adapterBoardsAnzeigen);

        loadMore();
    }

    private void loadMore()
    {
        if(isFinishing()) return;
        new LoadMyBoards(getApplicationContext(),
                loadingStateHandler, this)
        {
            @Override
            public int getCount()
            {
                if(isFinishing())
                {
                    return -1;
                }
                return adapterBoardsAnzeigen.getCountOriginal();
            }
        }.transferSynch();
    }

    @Override
    public void onDataLoaded(List<Board> data, boolean success)
    {
        if(!isFinishing())
        {
            if(success)
            {
                adapterBoardsAnzeigen.addItems(data);
            }
            else
            {
                loadingStateHandler.showMessageTryAgain(R.drawable.ic_warning, R.string.error_loading,
                        new LoadingStateHandler.LoadingStateActionListener() {
                    @Override
                    public void onAction()
                    {
                        loadMore();
                    }
                });
            }
        }
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

    private static final int REQUEST_CODE_SHOW_BOARD_ERSTELLEN = 1003;
    private static final int REQUEST_CODE_BOARD_BEARBEITEN = 1004;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(isFinishing()) return;

        if(data != null)
        {
            if(requestCode == ActivityBoardPicker.REQUEST_CODE_BOARD_BEARBEITEN)
            {
                if(resultCode == BoardManagmentBearbeitenErstellen.RESULT_CODE_BOARD_DATA_CHANGED)
                {
                    Bundle bundle = data.getExtras();
                    if(bundle != null)
                    {
                        Board board = (Board) bundle.getSerializable(Board.extra_Board);
                        if(board != null)
                        {
                            adapterBoardsAnzeigen.updateItem(board);
                        }
                    }
                }
            }
            else if(requestCode == ActivityBoardPicker.REQUEST_CODE_SHOW_BOARD_ERSTELLEN)
            {
                if(resultCode == BoardManagmentBearbeitenErstellen.RESULT_CODE_BOARD_DATA_CHANGED)
                {
                    Bundle bundle = data.getExtras();
                    if(bundle != null)
                    {
                        Board board = (Board) bundle.getSerializable(Board.extra_Board);
                        if(board != null)
                        {
                            adapterBoardsAnzeigen.addItem(board);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAction()
    {
        if(isFinishing()) return;
        loadMore();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.activity_board_picker_ListViewBoards)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_board, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            case R.id.menuDeleteBoard:
                removeList(adapterBoardsAnzeigen.getItem(info.position));
                return true;

            case R.id.menuBoardBearbeiten: // TODO: 24.11.2019 what if position change because after clicked some items get manipulated?

                Intent intent = new Intent(ActivityBoardPicker.this, BoardManagmentBearbeitenErstellen.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Board.extra_Board, adapterBoardsAnzeigen.getItem(info.position));
                intent.putExtras(bundle);
                startActivityForResult(intent, ActivityBoardPicker.REQUEST_CODE_BOARD_BEARBEITEN);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    private void removeList(Board board)
    {
        new DeleteBoard(ActivityBoardPicker.this,
                board,
                new DeleteBoard.DeleteBoardListener()
                {
                    @Override
                    public void onResult(Board board, boolean success)
                    {
                        if(!isFinishing())
                        {
                            if(success)
                            {
                                adapterBoardsAnzeigen.removeItem(board);
                            }
                            else
                            {
                                new AlertDialog.Builder(ActivityBoardPicker.this)
                                        .setTitle(R.string.error_board_delete_title)
                                        .setMessage(R.string.error_board_delete)
                                        .show();
                            }
                        }
                    }
                })
                .transferSynch();
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            HPreferences hPreferences = new HPreferences(getApplicationContext());
            if(hPreferences.getWorkingBench() != null)
            {
                finish();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "onBackPressed(activityBoardPicker): " + ec);
        }
    }
}
