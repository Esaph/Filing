package esaph.filing.CardsShowingFromList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.CardsShowingFromList.Sorting.KartenSortMethods;
import esaph.filing.KarteAnzeigen.KarteAnzeige;
import esaph.filing.CardsShowingFromList.Adapter.AdapterKartenAnzeigen;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.CardsShowingFromList.Sorting.KartenComperator;
import esaph.filing.R;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.Utils.GlobalBroadCasts;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.DeleteAuftrag;
import esaph.filing.workers.LoadMyKartenFromList;

public class FragmentShowListContent extends Fragment implements
        LoadMyKartenFromList.DataLoadListenerKartenFromList,
        ListObserver
{
    private Context context;
    private ListView listView;
    private LoadingStateHandler loadingStateHandler;
    private Spinner spinnerSortMethod;
    private AdapterKartenAnzeigen adapterHome;
    private Board boardWorking;
    private BoardListe boardListe;
    private KartenSortMethods kartenSortMethods;
    private boolean shouldSavePreferences = false;

    public static FragmentShowListContent getInstance(KartenSortMethods kartenSortMethods,
                                                      BoardListe boardListe)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KartenComperator.extra_KartenSortMethods, kartenSortMethods);
        bundle.putSerializable(BoardListe.extra_BoardListe, boardListe);

        FragmentShowListContent fragmentKartenAnzeigen = new FragmentShowListContent();
        fragmentKartenAnzeigen.setArguments(bundle);
        return fragmentKartenAnzeigen;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Activity activity = getActivity();
        if(activity != null)
        {
            activity.registerReceiver(broadcastReceiver, new IntentFilter(GlobalBroadCasts.action_NewCardCreated));
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Activity activity = getActivity();
        if(activity != null)
        {
            activity.unregisterReceiver(broadcastReceiver);
        }
    }

    public FragmentShowListContent()
    {
        // Required empty public constructor
    }

    public void addAuftrag(Auftrag auftrag)
    {
        adapterHome.addItem(auftrag);
    }

    public void removeAuftrag(Auftrag auftrag)
    {
        adapterHome.removeItem(auftrag);
    }

    public FragmentShowListContent setShouldSavePreferences(boolean shouldSavePreferences)
    {
        this.shouldSavePreferences = shouldSavePreferences;
        return this;
    }

    public void postSearch(String toSearch)
    {
        adapterHome.getFilter().filter(toSearch);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    public synchronized void postSetBoardListe(BoardListe boardListe)
    {
        if(!isAdded()) return;

        this.boardListe = boardListe;
        adapterHome.clearData();
        loadMore();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        listView = null;
        loadingStateHandler = null;
        spinnerSortMethod = null;
        context = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        adapterHome = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            boardListe = (BoardListe) bundle.getSerializable(BoardListe.extra_BoardListe);
            boardWorking = new HPreferences(context).getWorkingBench();
            kartenSortMethods = (KartenSortMethods) bundle.getSerializable(KartenComperator.extra_KartenSortMethods);
        }

        adapterHome = new AdapterKartenAnzeigen(getActivity(),
                boardListe,
                boardWorking,
                getChildFragmentManager(),
                this,
                kartenSortMethods);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_show_list_content, container, false);
        listView = rootView.findViewById(R.id.fragment_show_list_content_ListView_Main);
        loadingStateHandler = rootView.findViewById(R.id.fragment_show_list_content_LoadingStateHandler);
        spinnerSortMethod = rootView.findViewById(R.id.fragment_show_list_content_Spinner_SortMethod);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.layout_filing_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.arraySortMethods)));

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        registerForContextMenu(listView);

        loadingStateHandler.setLoadingStateActionListener(new LoadingStateHandler.LoadingStateActionListener()
        {
            @Override
            public void onAction()
            {
                loadMore();
            }
        });

        spinnerSortMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        kartenSortMethods = KartenSortMethods.BY_PRIORITY;
                        break;

                    case 1:
                        kartenSortMethods = KartenSortMethods.BY_TIME;
                        break;

                    case 2:
                        kartenSortMethods = KartenSortMethods.BY_COLOR;
                        break;
                }

                if(shouldSavePreferences)
                {
                    new HPreferences(context).setHomeSortingMethod(kartenSortMethods);
                }

                adapterHome.setKartenSortMethods(kartenSortMethods);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        spinnerSortMethod.setAdapter(dataAdapter);

        listView.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public void loadMore()
            {
                FragmentShowListContent.this.loadMore();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Auftrag auftrag = adapterHome.getItem(position);
                if(auftrag != null)
                {
                    Intent intent = new Intent(context, KarteAnzeige.class);
                    intent.putExtra(Auftrag.SERIAZABLE_ID, (Auftrag) auftrag);
                    intent.putExtra(BoardListe.extra_BoardListe, boardListe);
                    startActivityForResult(intent, FragmentShowListContent.REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_ANZEIGEN);
                }
            }
        });

        listView.setAdapter(adapterHome);

        updateUI();
        loadMore();
    }


    private void updateUI()
    {
        if(!isAdded()) return;

        switch (kartenSortMethods)
        {
            case BY_PRIORITY:
                spinnerSortMethod.setSelection(0);
                break;

            case BY_TIME:
                spinnerSortMethod.setSelection(1);
                break;

            case BY_COLOR:
                spinnerSortMethod.setSelection(2);
                break;
        }
    }


    private void loadMore()
    {
        if(boardListe == null) return;
        if(!isAdded()) return;

        new LoadMyKartenFromList(context,
                loadingStateHandler,
                boardListe, this)

        {
            @Override
            public int getCount()
            {
                if(!isAdded())
                {
                    return -1;
                }
                return adapterHome.getItemCountOriginal();
            }
        }.transferSynch();
    }


    private static final int REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_ANZEIGEN = 1001;
    private static final int REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_LISTE_ANZEIGEN = 1002;
    private static final int REQUEST_CODE_CREATE_CARD = 526;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) //Must use this, because we dont know if the activity will be killed or not.
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null)
        {
            if(requestCode == FragmentShowListContent.REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_ANZEIGEN)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    adapterHome.updateItem((Auftrag) bundle.getSerializable(Auftrag.SERIAZABLE_ID));
                }
            }
            else if(requestCode == FragmentShowListContent.REQUEST_CODE_CREATE_CARD)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    Auftrag auftrag = (Auftrag) bundle.getSerializable(Auftrag.SERIAZABLE_ID);
                    long LID = data.getLongExtra("LID", -1);
                    if(boardListe.getmBoardListeId() == LID && auftrag != null)
                    {
                        adapterHome.addItem(auftrag);
                    }
                }
            }
        }
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
    public void onDataLoaded(List<Auftrag> data, boolean success)
    {
        if(isAdded())
        {
            if(success)
            {
                adapterHome.addItems(data);
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
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            if (v.getId() == R.id.fragment_show_list_content_ListView_Main)
            {
                MenuInflater inflater = activity.getMenuInflater();
                inflater.inflate(R.menu.menu_card_options, menu);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId())
        {
            case R.id.menu_tour_Delete:
                removeCard(adapterHome.getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void removeCard(Auftrag auftrag)
    {
        new DeleteAuftrag(getActivity(),
                auftrag,
                boardListe,
                new DeleteAuftrag.DeleteAuftragListener()
                {
                    @Override
                    public void onResult(Auftrag auftragKarte, boolean success)
                    {
                        if(isAdded())
                        {
                            if(success)
                            {
                                adapterHome.removeItem(auftragKarte);
                            }
                            else
                            {
                                new AlertDialog.Builder(context).setTitle(R.string.error_karte_delete_title)
                                        .setMessage(R.string.error_karte_delete)
                                        .show();
                            }
                        }
                    }
                })
                .transferSynch();
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null
                    && intent.getAction() != null
                    && intent.getExtras() != null &&
                    intent.getAction().equals(GlobalBroadCasts.action_NewCardCreated))
            {
                if(boardListe.getmBoardListeId() == intent.getExtras().getLong(GlobalBroadCasts.extraNewCardCreatedAuftragList))
                {
                    Auftrag auftrag = (Auftrag) intent.getExtras().getSerializable(Auftrag.SERIAZABLE_ID);
                    adapterHome.addItem(auftrag);
                }
            }
        }
    };

}
