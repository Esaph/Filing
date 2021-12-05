package esaph.filing.Board.BoardManager;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import esaph.filing.Account.User;
import esaph.filing.Board.BoardManager.Adapter.UserPickerAdapter;
import esaph.filing.Board.Model.Board;
import esaph.filing.R;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.LoadUsersFromFilingServer;

public class UserPicker extends DialogFragment implements LoadUsersFromFilingServer.DataLoadListenerFilingServerUsers
{
    private UserSelectedListener userSelectedListener;
    private EditText editTextSearch;
    private UserPickerAdapter userPickerAdapter;
    private LoadingStateHandler loadingStateHandler;
    private ListView listView;
    private Button buttonDone;
    private Board board;


    public UserPicker(Board board, UserSelectedListener userSelectedListener)
    {
        this.userSelectedListener = userSelectedListener;
        this.board = board;
    }

    public interface UserSelectedListener
    {
        void onUsersSelected(List<User> mitgliedList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userPickerAdapter = new UserPickerAdapter(getContext());
        userPickerAdapter.getListSelectedUser().addAll(board.getmListMitglieder());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        listView = null;
        buttonDone = null;
        loadingStateHandler = null;
        editTextSearch = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        userSelectedListener = null;
        userPickerAdapter = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dialog_user_picker_user, container, false);

        if (getDialog() != null && getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        editTextSearch = (EditText) rootView.findViewById(R.id.dialog_choose_user_EditTextSearch);
        buttonDone = (Button) rootView.findViewById(R.id.dialog_choose_user_ButtonDone);
        listView = (ListView) rootView.findViewById(R.id.dialog_choose_user_ListViewTeilnehmer);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        buttonDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isAdded() && userSelectedListener != null)
                {
                    userSelectedListener.onUsersSelected(userPickerAdapter.getListSelectedUser());
                    dismiss();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                User user = userPickerAdapter.getItem(position);
                if(userPickerAdapter.getListSelectedUser().contains(user))
                {
                    userPickerAdapter.getListSelectedUser().remove(user);
                }
                else
                {
                    userPickerAdapter.getListSelectedUser().add(user);
                }
                userPickerAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public void loadMore()
            {
                UserPicker.this.loadMore();
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
                // TODO: 12.11.2019 test filter
                if(s != null)
                {
                    userPickerAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        listView.setAdapter(userPickerAdapter);

        loadMore();
    }



    private void loadMore()
    {
        if(!isAdded()) return;

        new LoadUsersFromFilingServer(getContext(),
                loadingStateHandler,
                this)
        {
            @Override
            public int getCount()
            {
                if(!isAdded())
                {
                    return -1;
                }
                return userPickerAdapter.getCount();
            }
        }.transferSynch();
    }

    @Override
    public void onDataLoaded(List<User> data, boolean success)
    {
        if(!isAdded()) return;

        if(success)
        {
            userPickerAdapter.addData(data);
        }
        else
        {
            loadingStateHandler.showMessageTryAgain(R.drawable.ic_warning, R.string.error_loading,
                    new LoadingStateHandler.LoadingStateActionListener()
            {
                @Override
                public void onAction()
                {
                    loadMore();
                }
            });
        }
    }
}