package esaph.filing.logging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.Einstellungen.ActivityEinstellungen;
import esaph.filing.R;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.logging.Adapter.AdapterLogging;
import esaph.filing.logging.Adapter.model.EsaphLog;
import esaph.filing.workers.LoadLogFromBoard;

public class LogFragment extends Fragment implements
        ListObserver,
        LoadLogFromBoard.DataLoadListenerLog
{
    private Context context;
    private AdapterLogging adapterLogging;
    private EditText editTextSearching;
    private ListView listView;
    private LoadingStateHandler loadingStateHandler;
    private ImageView imageViewEinstellungen;
    private Board board;

    public LogFragment()
    {
        // Required empty public constructor
    }

    public static LogFragment getInstance()
    {
        return new LogFragment();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        editTextSearching = null;
        listView = null;
        loadingStateHandler = null;
        imageViewEinstellungen = null;
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
        adapterLogging = new AdapterLogging(context,
                this);

        board = new HPreferences(context).getWorkingBench();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_logging, container, false);
        editTextSearching = rootView.findViewById(R.id.fragment_logging_EditTextSearch);
        listView = rootView.findViewById(R.id.fragment_logging_ListViewLog);
        loadingStateHandler = rootView.findViewById(R.id.fragment_logging_loadingStateHandler);
        imageViewEinstellungen = rootView.findViewById(R.id.fragment_logging_ImageViewOptions);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        imageViewEinstellungen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ActivityEinstellungen.class);
                startActivityForResult(intent, LogFragment.REQUEST_CODE_EINSTELLUNGEN);
            }
        });

        listView.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public void loadMore()
            {
                LogFragment.this.loadMore();
            }
        });

        editTextSearching.addTextChangedListener(new TextWatcher()
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
                    adapterLogging.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        listView.setAdapter(adapterLogging);
        loadMore();
    }

    private void loadMore()
    {
        if(!isAdded()) return;

        new LoadLogFromBoard(context,
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
                return adapterLogging.getCountOriginal();
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

    @Override
    public void onDataLoaded(List<EsaphLog> data, boolean success)
    {
        if(!isAdded()) return;

        if(success)
        {
            adapterLogging.addItems(data);
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

    private static final int REQUEST_CODE_EINSTELLUNGEN = 1223;
    private static final int RESTUL_CODE_LOGGED_OUT = 100;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LogFragment.REQUEST_CODE_EINSTELLUNGEN)
        {
            if(resultCode == LogFragment.RESTUL_CODE_LOGGED_OUT)
            {
                Activity activity = getActivity();
                if(activity != null)
                {
                    activity.finish();
                }
            }
        }
    }
}
