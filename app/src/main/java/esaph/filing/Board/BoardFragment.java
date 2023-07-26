package esaph.filing.Board;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.BoardsAnzeigen.ActivityBoardPicker;
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.CreateNewListListener;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.DialogListeErstellen;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.FragmentShowBoardContent;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.DeleteListe;
import esaph.filing.workers.TransferAuftrag;

public class BoardFragment extends Fragment implements
        DeleteListe.DeleteListeListener,
        TransferAuftrag.TransferAutragListener
{
    private static final int REQUEST_CODE_BOARD_CHOOSE = 1004;
    private Context context;
    private LoadingStateHandler loadingStateHandler;
    private EditText editTextSearchBoardContent;
    private TextView textViewBoardWechseln;
    private TextView textViewListeErstellen;
    private TextView textViewBoardName;
    private Board boardShowing;

    public BoardFragment()
    {
    }

    public static BoardFragment getInstance()
    {
        return new BoardFragment();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        context = null;
        loadingStateHandler = null;
        editTextSearchBoardContent = null;
        textViewListeErstellen = null;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        HPreferences hPreferences = new HPreferences(context);
        this.boardShowing = hPreferences.getWorkingBench();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        textViewBoardWechseln = rootView.findViewById(R.id.fragment_board_TextViewBoardWechseln);
        loadingStateHandler = rootView.findViewById(R.id.fragment_board_loadingStateHandler);
        editTextSearchBoardContent = rootView.findViewById(R.id.fragment_board_EditTextSearch);
        textViewListeErstellen = rootView.findViewById(R.id.fragment_board_TextView_ListeErstellen);
        textViewBoardName = rootView.findViewById(R.id.fragment_board_TextViewFiling);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        textViewBoardWechseln.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ActivityBoardPicker.class);
                startActivityForResult(intent, BoardFragment.REQUEST_CODE_BOARD_CHOOSE);
            }
        });

        textViewListeErstellen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogListeErstellen dialogListeErstellen = new DialogListeErstellen(boardShowing, new CreateNewListListener()
                {
                    @Override
                    public void onResult(BoardListe boardListe, boolean success)
                    {
                        if(success)
                        {
                            currentFragmentShowBoardContent.addSingleListToViewPager(boardListe);
                        }
                        else
                        {
                            new AlertDialog.Builder(context).setTitle(R.string.error_list_creating_title)
                                    .setMessage(R.string.error_list_creating)
                                    .show();
                        }
                    }
                });

                dialogListeErstellen.show(getChildFragmentManager(), DialogListeErstellen.class.getName());
            }
        });
        updateBoardFragment();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        HPreferences hPreferences = new HPreferences(context);
        if(hPreferences.getWorkingBench() == null
                || !hPreferences.getWorkingBench().equals(boardShowing))
        {
            boardShowing = null;
        }

        boardShowing = hPreferences.getWorkingBench();
        updateBoardFragment();
    }

    private FragmentShowBoardContent currentFragmentShowBoardContent;
    private void updateBoardFragment()
    {
        if(boardShowing != null)
        {
            textViewBoardName.setText(boardShowing.getmBoardName());
            currentFragmentShowBoardContent = FragmentShowBoardContent.getInstance(boardShowing);
            textViewListeErstellen.setVisibility(View.VISIBLE);
            loadingStateHandler.hideMessage();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_board_container, currentFragmentShowBoardContent)
                    .commit();
        }
        else
        {
            textViewBoardName.setText(getResources().getString(R.string.app_name));
            textViewListeErstellen.setVisibility(View.GONE);
            if(currentFragmentShowBoardContent != null)
            {
                getChildFragmentManager().beginTransaction()
                        .remove(currentFragmentShowBoardContent)
                        .commit();
            }
            loadingStateHandler.showMessage(R.drawable.ic_break, R.string.boardAuswaehlen);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(!isAdded()) return;

        if(currentFragmentShowBoardContent != null)
        {
            currentFragmentShowBoardContent.onActivityResult(requestCode, resultCode, data);
        }

        if(data != null)
        {
            if(requestCode == BoardFragment.REQUEST_CODE_BOARD_CHOOSE)
            {
                if(resultCode == ActivityBoardPicker.RESULT_CODE_BOARD_SELECTED)
                {
                    Bundle bundle = data.getExtras();
                    if(bundle != null)
                    {
                        Board board = (Board) bundle.getSerializable(Board.extra_Board);
                        if(board != null)
                        {
                            this.boardShowing = board;
                            updateBoardFragment();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResult(BoardListe boardListe, boolean success)
    {
        if(currentFragmentShowBoardContent != null)
        {
            currentFragmentShowBoardContent.onResult(boardListe, success);
        }
    }

    @Override
    public void onResult(Auftrag auftrag, long boardListeCurrent, long boardListeTransfered, boolean success)
    {
        if(currentFragmentShowBoardContent != null)
        {
            currentFragmentShowBoardContent.onResult(auftrag,
                    boardListeCurrent,
                    boardListeTransfered,
                    success);
        }
    }
}