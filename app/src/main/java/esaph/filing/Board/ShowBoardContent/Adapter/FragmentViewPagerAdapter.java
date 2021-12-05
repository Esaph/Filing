package esaph.filing.Board.ShowBoardContent.Adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Adapter.Model.FragmentViewpagerItemBoardList;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.Utils.ListObserver;

public class FragmentViewPagerAdapter extends FragmentStatePagerAdapter implements
        ViewPager.OnPageChangeListener
{
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private List<BoardListe> boardListes = new ArrayList<>();
    private ListObserver listObserver;
    private ViewPager viewPager;
    private Board board;

    public FragmentViewPagerAdapter(@NonNull FragmentManager fm,
                                    int behavior,
                                    ViewPager viewPager,
                                    Board board,
                                    ListObserver listObserver)
    {
        super(fm, behavior);
        this.viewPager = viewPager;
        this.board = board;
        this.listObserver = listObserver;
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void notifyDataSetChanged()
    {
        if(listObserver != null)
        {
            listObserver.observDataChange(boardListes.isEmpty());
        }
        super.notifyDataSetChanged();
    }

    public void addItem(BoardListe boardListe)
    {
        if(canAdd(boardListe.getmBoardListeId()))
        {
            this.boardListes.add(boardListe);
        }
        notifyDataSetChanged();
    }

    private boolean canAdd(long ID_Check)
    {
        int size = this.boardListes.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(this.boardListes.get(counter).getmBoardListeId() == ID_Check)
            {
                return false;
            }
        }
        return true;
    }

    public void addItems(List<BoardListe> listes)
    {
        this.boardListes.addAll(this.boardListes.size(),listes);
        notifyDataSetChanged();
    }

    public BoardListe getBoardById(long boardId)
    {
        ListIterator<BoardListe> listIterator = boardListes.listIterator();
        while(listIterator.hasNext())
        {
            BoardListe boardListe = listIterator.next();
            if(boardListe.getmBoardListeId() == boardId)
            {
                return boardListe;
            }
        }
        return null;
    }

    public SparseArray<Fragment> getRegisteredFragments() {
        return registeredFragments;
    }

    public void removeList(long id)
    {
        ListIterator<BoardListe> listIterator = boardListes.listIterator();
        while(listIterator.hasNext())
        {
            BoardListe boardListe = listIterator.next();
            if(boardListe.getmBoardListeId() == id)
            {
                listIterator.remove();
                break;
            }
        }
        notifyDataSetChanged();
    }

    private String[] getTabTitles()
    {
        ArrayList<String> stringsNames = new ArrayList<>();
        for (BoardListe boardList:
                boardListes)
        {
            stringsNames.add(boardList.getmListenName());
        }

        return stringsNames.toArray(new String[]{});
    }

    @Override @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return boardListes.get(position).getmListenName();
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        return FragmentViewpagerItemBoardList.getInstance(boardListes.get(position));
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return boardListes.size();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
    }
}