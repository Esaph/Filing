package esaph.filing.Board.ShowBoardContent.Adapter.Model;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import esaph.elib.esaphcommon.dialog.Builder.ESDialogRequestBuilder;
import esaph.elib.esaphcommon.dialog.ESDialog;
import esaph.filing.CardsShowingFromList.FragmentShowListContent;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.CardsShowingFromList.Sorting.KartenSortMethods;
import esaph.filing.Filing;
import esaph.filing.KarteErstellen.KarteErstellen;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.workers.DeleteListe;
import esaph.filing.workers.TransferAuftrag;

public class FragmentViewpagerItemBoardList extends Fragment implements TransferAuftrag.TransferAutragListener
{
    private Context context;
    private TextView textViewListenName;
    private ImageView imageViewAdd;
    private ImageView imageViewKarteErstellen;
    private ImageView imageViewListeDelete;
    private FragmentShowListContent fragmentShowListContent;
    private BoardListe boardListe;

    public FragmentViewpagerItemBoardList()
    {
        // Required empty public constructor
    }

    public static FragmentViewpagerItemBoardList getInstance(BoardListe boardListe)
    {
        FragmentViewpagerItemBoardList fragmentViewpagerItemBoardList = new FragmentViewpagerItemBoardList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BoardListe.extra_BoardListe, boardListe);
        fragmentViewpagerItemBoardList.setArguments(bundle);
        return fragmentViewpagerItemBoardList;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        this.imageViewAdd = null;
        this.fragmentShowListContent = null;
        this.textViewListenName = null;
        this.imageViewKarteErstellen = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.context = null;
        this.boardListe = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            boardListe = (BoardListe) bundle.getSerializable(BoardListe.extra_BoardListe);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fragment_viewpager_item_board_list, container, false);
        textViewListenName = rootView.findViewById(R.id.fragment_fragment_viewpager_item_board_list_TextView_ListenName);
        imageViewKarteErstellen = rootView.findViewById(R.id.fragment_fragment_viewpager_item_board_list_list_ImageViewKarteErstellen);
        imageViewListeDelete = rootView.findViewById(R.id.fragment_fragment_viewpager_item_board_list_ImageViewDeleteList);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        imageViewKarteErstellen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, KarteErstellen.class);
                intent.putExtra(BoardListe.extra_BoardListe, boardListe);
                startActivityForResult(intent,
                        FragmentViewpagerItemBoardList.requestCodeCreateKarte);
            }
        });

        imageViewListeDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
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
                                new DeleteListe(getActivity(),
                                        boardListe,
                                        new DeleteListe.DeleteListeListener()
                                        {
                                            @Override
                                            public void onResult(BoardListe boardListe, boolean success)
                                            {
                                                Activity activity = getActivity();
                                                if(activity instanceof Filing)
                                                {
                                                    Filing filing = (Filing) activity;
                                                    filing.onResult(boardListe, success);
                                                }
                                            }
                                        })
                                        .transferSynch();
                            }
                        }).build();

                esDialog.show(getChildFragmentManager(), ESDialog.class.getName());
            }
        });

        textViewListenName.setText(boardListe.getmListenName());
        updateBoardFragment();
    }


    private static final int requestCodeCreateKarte = 526;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(fragmentShowListContent != null)
        {
            fragmentShowListContent.onActivityResult(requestCode, resultCode, data);
        }

        if(data != null)
        {
            if(requestCode == FragmentViewpagerItemBoardList.requestCodeCreateKarte)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    Auftrag auftrag = (Auftrag) bundle.getSerializable(Auftrag.SERIAZABLE_ID);
                    fragmentShowListContent.addAuftrag(auftrag);
                }
            }
        }
    }

    private void updateBoardFragment()
    {
        fragmentShowListContent = FragmentShowListContent.getInstance(KartenSortMethods.BY_PRIORITY, boardListe);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_fragment_viewpager_item_board_list_FrameLayout, fragmentShowListContent)
                .commit();
    }

    @Override
    public void onResult(Auftrag auftrag, long boardListeCurrent, long boardListeTransfered, boolean success)
    {
        if(success && fragmentShowListContent != null)
        {
            if(boardListe.getmBoardListeId() == boardListeCurrent)
            {
                fragmentShowListContent.removeAuftrag(auftrag);
            }
            else if(boardListe.getmBoardListeId() == boardListeTransfered)
            {
                fragmentShowListContent.addAuftrag(auftrag);
            }
        }
    }
}