package esaph.filing.CardsShowingFromList.TransferWindow;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.CardsShowingFromList.TransferWindow.Adapter.AdapterListenPickerTransferAnzeigen;
import esaph.filing.R;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.LoadMyListFromBoard;
import esaph.filing.workers.TransferAuftrag;

public class DialogTransferAuftrag extends DialogFragment implements ListObserver,
        LoadMyListFromBoard.DataLoadListenerBoardListenGetter,
        LoadingStateHandler.LoadingStateActionListener
{
    private Auftrag auftrag;
    private BoardListe currentBoardListe;
    private Board board;

    private AdapterListenPickerTransferAnzeigen adapterListenPickerTransferAnzeigen;
    private LoadingStateHandler loadingStateHandler;
    private EditText editTextSearch;
    private ListView listView;
    private TransferAuftrag.TransferAutragListener transferAutragListener;


    public DialogTransferAuftrag(Auftrag auftrag,
                                 BoardListe currentBoardListe,
                                 Board board,
                                 TransferAuftrag.TransferAutragListener transferAutragListener)
    {
        this.auftrag = auftrag;
        this.currentBoardListe = currentBoardListe;
        this.board = board;
        this.transferAutragListener = transferAutragListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_dialog_transfer_auftrag, container, false);

        if (getDialog() != null && getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        editTextSearch = rootView.findViewById(R.id.dialog_transfer_auftragEditTextSearch);
        listView = rootView.findViewById(R.id.dialog_transfer_auftragListView);
        loadingStateHandler = rootView.findViewById(R.id.dialog_transfer_auftragLoadingStateHandler);

        loadingStateHandler.setLoadingStateActionListener(this);

        TextView textViewBack = rootView.findViewById(R.id.dialog_transfer_auftragTextViewBack);
        textViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        adapterListenPickerTransferAnzeigen = new AdapterListenPickerTransferAnzeigen(getContext(),
                currentBoardListe,
                this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BoardListe boardListe = adapterListenPickerTransferAnzeigen.getItem(position);
                if(boardListe.equals(currentBoardListe))
                {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.warning_same_list_selected),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                new TransferAuftrag(getActivity(),
                        auftrag,
                        currentBoardListe.getmBoardListeId(),
                        boardListe.getmBoardListeId(),
                        new TransferAuftrag.TransferAutragListener()
                        {
                            @Override
                            public void onResult(Auftrag auftrag, long boardListeCurrent, long boardListeTransfered, boolean success)
                            {
                                if(transferAutragListener != null) transferAutragListener.onResult(auftrag,
                                        boardListeCurrent,
                                        boardListeTransfered, success);

                                if(success)
                                {
                                    dismiss();
                                }
                                else
                                {
                                    new AlertDialog.Builder(getContext()).setTitle(R.string.error_transfer_card_title)
                                            .setMessage(R.string.error_transfer_card)
                                            .show();
                                }
                            }
                        }
                ).transferSynch();
            }
        });

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
                    adapterListenPickerTransferAnzeigen.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        registerForContextMenu(listView);

        listView.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public void loadMore()
            {
                DialogTransferAuftrag.this.loadMore();
            }
        });

        listView.setAdapter(adapterListenPickerTransferAnzeigen);

        loadMore();
        
        return rootView;
    }


    private void loadMore()
    {
        if(!isAdded()) return;

        new LoadMyListFromBoard(getContext(),
                loadingStateHandler, board, this)
        {
            @Override
            public int getCount()
            {
                if(!isAdded())
                {
                    return -1;
                }
                return adapterListenPickerTransferAnzeigen.getCountOriginal();
            }
        }.transferSynch();
    }


    @Override
    public void observDataChange(boolean isEmpty)
    {
        if(!isAdded()) return;

        if(isEmpty)
        {
            loadingStateHandler.showMessage(R.drawable.ic_break, R.string.no_card_data);
        }
        else
        {
            loadingStateHandler.hideMessage();
        }
    }

    private boolean removedFirstItem = false;
    @Override
    public void onDataLoaded(List<BoardListe> data, boolean success)
    {
        if(isAdded())
        {
            if(success)
            {
                adapterListenPickerTransferAnzeigen.addItems(data);
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
}