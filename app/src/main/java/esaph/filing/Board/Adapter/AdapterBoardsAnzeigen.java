package esaph.filing.Board.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import esaph.filing.Account.User;
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.Model.BoardPolicy;
import esaph.filing.Board.Sorting.BoardComperator;
import esaph.filing.R;
import esaph.filing.Utils.ListObserver;
import esaph.filing.logging.Adapter.model.EsaphLog;

public class AdapterBoardsAnzeigen extends BaseAdapter implements Filterable
{
    private List<Board> mDisplayList = new ArrayList<>();
    private List<Board> mDataBaseListOriginal = new ArrayList<>();
    private LayoutInflater inflater;
    private WeakReference<ListObserver> adapterDataEmptyListenerWeakReference;

    public AdapterBoardsAnzeigen(Context context, ListObserver adapterDataEmptyListener)
    {
        this.inflater = LayoutInflater.from(context);
        this.adapterDataEmptyListenerWeakReference = new WeakReference<>(adapterDataEmptyListener);
    }


    private void recyclerViewNotify()
    {
        Collections.sort(this.mDataBaseListOriginal, BoardComperator.getComperator());

        ListObserver adapterDataEmptyListener = adapterDataEmptyListenerWeakReference.get();
        if(adapterDataEmptyListener != null)
        {
            adapterDataEmptyListener.observDataChange(mDataBaseListOriginal.isEmpty());
        }
    }


    public void clearData()
    {
        mDataBaseListOriginal.clear();
        getFilter().filter("");
    }

    public void removeItem(Board board)
    {
        int size = mDataBaseListOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(mDataBaseListOriginal.get(counter).getmBoardId() == board.getmBoardId())
            {
                mDataBaseListOriginal.remove(counter);
                getFilter().filter("");
                break;
            }
        }
    }

    public void updateItem(Board board)
    {
        int size = mDataBaseListOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(mDataBaseListOriginal.get(counter).getmBoardId() == board.getmBoardId())
            {
                mDataBaseListOriginal.set(counter, board);
                getFilter().filter("");
                break;
            }
        }
    }

    public void addItems(List<Board> list)
    {
        this.mDataBaseListOriginal.addAll(list);
        getFilter().filter("");
    }

    public void addItem(Board board)
    {
        this.mDataBaseListOriginal.add(board);
        getFilter().filter("");
    }


    private class ViewHolderBoard
    {
        private View rootView;
        private TextView textViewBoardName;
        private TextView textViewTeilnehmer;
        private ImageView imageViewPolicy;
    }

    public int getCountOriginal()
    {
        return mDataBaseListOriginal.size();
    }

    @Override
    public int getCount()
    {
        return mDisplayList.size();
    }

    public Board getItem(int position)
    {
        return mDisplayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Board board = mDisplayList.get(position);
        ViewHolderBoard viewHolderBoard;
        if(convertView == null)
        {
            viewHolderBoard = new ViewHolderBoard();
            convertView = inflater.inflate(R.layout.item_board, parent, false);
            viewHolderBoard.textViewBoardName = convertView.findViewById(R.id.item_board_TextViewBoardName);
            viewHolderBoard.textViewTeilnehmer = convertView.findViewById(R.id.item_board_TextViewBoardTeilnehmer);
            viewHolderBoard.imageViewPolicy = convertView.findViewById(R.id.item_board_ImageViewLock);
            convertView.setTag(viewHolderBoard);
        }
        else
        {
            viewHolderBoard = (ViewHolderBoard) convertView.getTag();
        }

        viewHolderBoard.textViewBoardName.setText(board.getmBoardName());
        viewHolderBoard.textViewTeilnehmer.setText(getMitglieder(board));

        if(board.getBoardPolicy() == BoardPolicy.POLICY_PRIVATE)
        {
            Glide.with(viewHolderBoard.imageViewPolicy.getContext())
                    .load(R.drawable.ic_locked)
                    .into(viewHolderBoard.imageViewPolicy);
        }
        else if(board.getBoardPolicy() == BoardPolicy.POLICY_PUBLIC)
        {
            Glide.with(viewHolderBoard.imageViewPolicy.getContext())
                    .load(R.drawable.ic_unlocked)
                    .into(viewHolderBoard.imageViewPolicy);
        }

        return convertView;
    }

    private String getMitglieder(Board board)
    {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        Iterator<User> iterator = board.getmListMitglieder().iterator();
        while(iterator.hasNext() && count < 3)
        {
            count++;
            stringBuilder.append(iterator.next().getUsername());
            if(iterator.hasNext())
            {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                mDisplayList = (List<Board>) results.values;
                recyclerViewNotify();
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();
                if(constraint.toString().isEmpty())
                {
                    results.values = mDataBaseListOriginal;
                    return results;
                }

                ArrayList<Board> FilteredArrayNames = new ArrayList<Board>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mDataBaseListOriginal.size(); i++)
                {
                    Board dataNames = mDataBaseListOriginal.get(i);
                    if (dataNames.getmBoardName().toLowerCase().startsWith(constraint.toString()))
                    {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }


}