package esaph.filing.CardsShowingFromList.ListenAnzeigen;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import esaph.filing.Board.Model.Board;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.workers.CreateNewList;

public class DialogListeErstellen extends DialogFragment
{
    private Board board;
    private CreateNewListListener createNewListListener;


    public DialogListeErstellen(Board board, CreateNewListListener createNewListListener)
    {
        this.board = board;
        this.createNewListListener = createNewListListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_dialog_liste_erstellen, container, false);

        if (getDialog() != null && getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        final EditText editText = (EditText) rootView.findViewById(R.id.layout_dialog_liste_erstellen_EditTextName);
        final Button button = (Button) rootView.findViewById(R.id.layout_dialog_liste_erstellen_ButtonFertig);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BoardListe boardListe = new BoardListe(0, editText.getText().toString(), 0);
                new CreateNewList(getActivity(),
                        board,
                        boardListe,
                        createNewListListener).transfer();
                dismiss();
            }
        });

        return rootView;
    }

}