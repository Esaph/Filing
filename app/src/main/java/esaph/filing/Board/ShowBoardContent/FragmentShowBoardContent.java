package esaph.filing.Board.ShowBoardContent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import esaph.filing.Board.BoardManager.BoardManagmentBearbeitenErstellen;
import esaph.filing.Board.Model.Board;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.Adapter.FragmentViewPagerAdapter;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.DeleteListe;
import esaph.filing.workers.LoadMyListFromBoard;
import esaph.filing.workers.TransferAuftrag;

public class FragmentShowBoardContent extends Fragment implements ListObserver,
        LoadMyListFromBoard.DataLoadListenerBoardListenGetter,
        DeleteListe.DeleteListeListener,
        TransferAuftrag.TransferAutragListener
{
    private Context context;
    private ViewPager viewPager;
    private LoadingStateHandler loadingStateHandler;
    private Board board;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;

    public FragmentShowBoardContent()
    {
        // Required empty public constructor
    }

    public static FragmentShowBoardContent getInstance(Board board)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Board.extra_Board, board);

        FragmentShowBoardContent fragmentShowBoardContent = new FragmentShowBoardContent();
        fragmentShowBoardContent.setArguments(bundle);
        return fragmentShowBoardContent;
    }

    public void addSingleListToViewPager(BoardListe boardListe)
    {
        fragmentViewPagerAdapter.addItem(boardListe);
        viewPager.setCurrentItem(fragmentViewPagerAdapter.getCount());
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
        context = null;
        viewPager = null;
        loadingStateHandler = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        fragmentViewPagerAdapter = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fragment_show_board_content, container, false);

        viewPager = rootView.findViewById(R.id.fragment_fragment_show_board_content_ViewPager);
        loadingStateHandler = rootView.findViewById(R.id.fragment_fragment_show_board_content_anzeige_LoadingStateHandler);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                viewPager,
                board,
                this);

        viewPager.setAdapter(fragmentViewPagerAdapter);
        loadMore();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            board = (Board) bundle.getSerializable(Board.extra_Board);
        }
    }

    public void loadMore()
    {
        if(!isAdded()) return;

        new LoadMyListFromBoard(context,
                loadingStateHandler,
                board,
                this)
        {
            @Override
            public int getCount()
            {
                if(!isAdded())
                {
                    return -1;
                }
                return fragmentViewPagerAdapter.getCount();
            }

        }.transferSynch();
    }

    @Override
    public void observDataChange(boolean isEmpty)
    {
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
        if(isAdded())
        {
            loadingStateHandler.hideMessage();
            if(success)
            {
                fragmentViewPagerAdapter.addItems(data);
            }
            else
            {
                loadingStateHandler.showMessageTryAgain(R.drawable.ic_warning, R.string.error_loading, new LoadingStateHandler.LoadingStateActionListener()
                {
                    @Override
                    public void onAction()
                    {
                        if(isAdded())
                        {
                            loadMore();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(!isAdded()) return;

        SparseArray<Fragment> sparseArray = fragmentViewPagerAdapter.getRegisteredFragments();
        for (int i = 0; i < sparseArray.size(); i++)
        {
            sparseArray.get(i).onActivityResult(requestCode, resultCode, data);
        }

        if(data != null)
        {
            if(resultCode == BoardManagmentBearbeitenErstellen.RESULT_CODE_BOARD_DATA_CHANGED)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    Board board = (Board) bundle.getSerializable(Board.extra_Board);
                    if(board != null)
                    {
                        this.board = board;
                    }
                }
            }
        }
    }

    @Override
    public void onResult(BoardListe boardListe, boolean success)
    {
        if(!isAdded()) return;

        if(success)
        {
            fragmentViewPagerAdapter.removeList(boardListe.getmBoardListeId());
        }
        else
        {
            new AlertDialog.Builder(context).setTitle(R.string.error_list_delete_title)
                    .setMessage(R.string.error_list_delete)
                    .show();
        }
    }

    @Override
    public void onResult(Auftrag auftrag, long boardListeCurrent, long boardListeTransfered, boolean success)
    {
        if(success)
        {
            SparseArray<Fragment> fragmentSparseArray = fragmentViewPagerAdapter.getRegisteredFragments();
            for (int pos = 0; pos < fragmentSparseArray.size(); pos++)
            {
                Fragment fragment = fragmentSparseArray.get(pos);
                if(fragment != null)
                {
                    if(fragment instanceof TransferAuftrag.TransferAutragListener)
                    {
                        ((TransferAuftrag.TransferAutragListener)fragment).onResult(auftrag,
                                boardListeCurrent,
                                boardListeTransfered,
                                success);
                    }
                }
            }
        }
    }
}