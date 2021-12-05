package esaph.filing.TourenPlaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.Board.ShowBoardContent.Model.BoardListe;
import esaph.filing.CardsShowingFromList.ListenAnzeigen.ActivityListPicker;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.Filing;
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.KarteAnzeigen.KarteAnzeige;
import esaph.filing.LabelPicker.KatalogChooser;
import esaph.filing.R;
import esaph.filing.TourenPlaner.Adapter.AdapterTouren;
import esaph.filing.TourenPlaner.TMSDestinationAdress.TMSSetupDialog;
import esaph.filing.Utils.EndlessScrollListener;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.ArchiviereAuftrag;
import esaph.filing.workers.DeleteAuftrag;
import esaph.filing.workers.LoadMyTms;

public class TourenPlaner extends Fragment implements
        ListObserver,
        LoadMyTms.DataLoadListenerTMS
{
    private Context context;
    private AdapterTouren adapterTouren;
    private LoadingStateHandler loadingStateHandler;
    private TextView textViewStartCompleteTourTMSSystem;
    private ListView listViewTouren;
    private String destAdressFilingTMSystem;
    private TextView textViewCurrentList;
    private TextView textViewTMSHomeAdress;
    private TextView textViewKatagorie;
    private BoardListe boardListe;
    private Board boardShowing;
    private ColorBinder colorBinder;

    public TourenPlaner()
    {
        // Required empty public constructor
    }

    public static TourenPlaner getInstance()
    {
        return new TourenPlaner();
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
        boardListe = hPreferences.getWorkingList();
        boardShowing = hPreferences.getWorkingBench();
        colorBinder = hPreferences.getTourenColorFilter();

        try
        {
            destAdressFilingTMSystem = hPreferences.getTMSDestination();
            if(destAdressFilingTMSystem.isEmpty())
            {
                startActivityForResult(new Intent(context, TMSSetupDialog.class), TourenPlaner.REQUEST_CODE_TOUR_TMS_SETUP_DONE);
            }
        }
        catch (Exception e)
        {
            Log.i(getClass().getName(), "failed to onCreate Tourenplaner: " + e);
            startActivityForResult(new Intent(context, TMSSetupDialog.class), TourenPlaner.REQUEST_CODE_TOUR_TMS_SETUP_DONE);
        }

        adapterTouren = new AdapterTouren(context, this, destAdressFilingTMSystem);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        adapterTouren = null;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        listViewTouren = null;
        textViewStartCompleteTourTMSSystem = null;
        loadingStateHandler = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_touren_planer, container, false);

        loadingStateHandler = rootView.findViewById(R.id.fragment_touren_planer_loadingStateHandler);
        textViewStartCompleteTourTMSSystem = rootView.findViewById(R.id.fragment_touren_planer_TextViewStartCompleteTour);
        listViewTouren = rootView.findViewById(R.id.fragment_touren_planer_ListViewTouren);
        textViewTMSHomeAdress = rootView.findViewById(R.id.fragment_touren_planer_TextViewAdress);
        textViewCurrentList = rootView.findViewById(R.id.fragment_touren_planer_TextViewWorkList);
        textViewKatagorie = rootView.findViewById(R.id.fragment_touren_planer_TextViewKatagorie);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        registerForContextMenu(listViewTouren);

        textViewKatagorie.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, KatalogChooser.class);
                startActivityForResult(intent, TourenPlaner.REQUEST_CODE_SELECT_KATALOG);
            }
        });

        textViewStartCompleteTourTMSSystem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: 11.11.2019 problem only up to 10 waypoints.
                try
                {
                    EsaphGoogleTripsRequestBuilder esaphGoogleTripsRequestBuilder = EsaphGoogleTripsRequestBuilder.roadTrip(destAdressFilingTMSystem);

                    int count = 0;
                    List<Auftrag> list = adapterTouren.getmDisplayList();
                    for (Auftrag tour : list)
                    {
                        count++;
                        if(count >= 10)
                        {
                            break;
                        }

                        if(!tour.getContactDTO().getFormatedAdress().equals(esaphGoogleTripsRequestBuilder.getDestinitionAddress()))
                        {
                            esaphGoogleTripsRequestBuilder.addRoadTrip(new EsaphGoogleTripsRequestBuilder.RoadTrip(tour.getContactDTO().getFormatedAdress()));
                        }
                    }

                    startActivity(esaphGoogleTripsRequestBuilder.build());
                }
                catch (Exception ec)
                {
                    System.out.println("Starting road trip failed: " + ec);
                }
            }
        });

        listViewTouren.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Auftrag tour = adapterTouren.getItem(position);
                String addressList = tour.getContactDTO().getFormatedAdress();
                if(addressList != null && !addressList.isEmpty())
                {
                    startActivity(EsaphGoogleMapsRequestBuilder.obtain(addressList).build());
                }
                else
                {
                    Toast.makeText(context, getResources().getString(R.string.adressMissing), Toast.LENGTH_LONG).show();
                }
            }
        });

        listViewTouren.setOnScrollListener(new EndlessScrollListener()
        {
            @Override
            public void loadMore()
            {
                TourenPlaner.this.loadMore();
            }
        });

        textViewTMSHomeAdress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivityForResult(new Intent(context, TMSSetupDialog.class), TourenPlaner.REQUEST_CODE_TOUR_TMS_SETUP_DONE);
            }
        });

        textViewCurrentList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ActivityListPicker.class);
                intent.putExtra(Board.extra_Board, boardShowing);
                startActivityForResult(intent, TourenPlaner.REQUEST_CODE_LIST_CHOOSE);
            }
        });

        listViewTouren.setAdapter(adapterTouren);
        updateUI();
        loadMore();
    }

    private void updateUI()
    {
        if(boardListe != null)
        {
            textViewCurrentList.setText(boardListe.getmListenName());
        }
        else
        {
            textViewCurrentList.setText("");
        }

        if(colorBinder != null)
        {
            textViewKatagorie.setText(colorBinder.getmImportance());
        }
        else
        {
            textViewKatagorie.setText(getResources().getString(R.string.txt_katagorieSortieren));
        }

        textViewTMSHomeAdress.setText(destAdressFilingTMSystem);

        if(colorBinder != null)
        {
            textViewKatagorie.setText(colorBinder.getmImportance());
        }
        else
        {
            textViewKatagorie.setText("");
        }
    }

    private void loadMore()
    {
        if(!isAdded() || boardListe == null)
        {
            if(adapterTouren != null)
            {
                observDataChange(adapterTouren.getCountOriginal() <= 0);
            }
            return;
        }

        new LoadMyTms(context,
                loadingStateHandler,
                boardListe,
                colorBinder,
                this)
        {
            @Override
            public int getCount()
            {
                if(!isAdded())
                {
                    return -1;
                }
                return adapterTouren.getCountOriginal();
            }
        }.transferSynch();
    }

    @Override
    public void observDataChange(boolean isEmpty)
    {
        if(!isAdded()) return;
        if(isEmpty)
        {
            textViewStartCompleteTourTMSSystem.setVisibility(View.GONE);
            loadingStateHandler.showMessage(R.drawable.ic_destination_small, R.string.no_tour_data);
        }
        else
        {
            textViewStartCompleteTourTMSSystem.setVisibility(View.VISIBLE);
            loadingStateHandler.hideMessage();
        }
    }

    @Override
    public void onDataLoaded(List<Auftrag> data, boolean success)
    {
        if(!isAdded()) return;

        if(success)
        {
            adapterTouren.addItems(data);
        }
        else
        {
            loadingStateHandler.showMessageTryAgain(R.drawable.ic_warning, R.string.error_loading, new LoadingStateHandler.LoadingStateActionListener()
            {
                @Override
                public void onAction()
                {
                    loadMore();
                }
            });
        }
    }

    private static final int REQUEST_CODE_LIST_CHOOSE = 1005;
    private static final int REQUEST_CODE_TOUR_CREATOR = 66; //Route 66
    private static final int REQUEST_CODE_TOUR_TMS_SETUP_DONE = 625;
    private static final int REQUEST_CODE_SELECT_KATALOG = 241;
    private static final int REQUEST_CODE_CREATE_CARD = 526;
    private static final int REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_ANZEIGEN = 1001;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(!isAdded()) return;

        if(data != null)
        {
            if(requestCode == TourenPlaner.REQUEST_CODE_TOUR_CREATOR)
            {
            }
            else if(requestCode == TourenPlaner.REQUEST_CODE_TOUR_TMS_SETUP_DONE)
            {
                try
                {
                    destAdressFilingTMSystem = new HPreferences(context).getTMSDestination();
                    updateUI();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    startActivityForResult(new Intent(context, TMSSetupDialog.class), TourenPlaner.REQUEST_CODE_TOUR_TMS_SETUP_DONE);
                }
            }
            else if(requestCode == TourenPlaner.REQUEST_CODE_LIST_CHOOSE)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    BoardListe boardListe = (BoardListe) bundle.getSerializable(BoardListe.extra_BoardListe);
                    if(boardListe != null)
                    {
                        this.boardListe = boardListe;
                        adapterTouren.clear();
                        updateUI();
                        loadMore();
                    }
                }
            }
            else if(requestCode == TourenPlaner.REQUEST_CODE_SELECT_KATALOG)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    ColorBinder colorBinder = (ColorBinder) bundle.getSerializable(ColorBinder.extra);
                    if(colorBinder != null)
                    {
                        new HPreferences(context).setTourenColorFilter(colorBinder);
                        this.colorBinder = colorBinder;
                        adapterTouren.clear();
                        updateUI();
                        loadMore();
                    }
                }
            }
            else if(requestCode == TourenPlaner.REQUEST_CODE_CREATE_CARD)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    Auftrag auftrag = (Auftrag) bundle.getSerializable(Auftrag.SERIAZABLE_ID);
                    long LID = data.getLongExtra("LID", -1);
                    if(boardListe.getmBoardListeId() == LID && auftrag != null)
                    {
                        adapterTouren.addItem(auftrag);
                    }
                }
            }
            else if(requestCode == TourenPlaner.REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_ANZEIGEN)
            {
                Bundle bundle = data.getExtras();
                if(bundle != null)
                {
                    adapterTouren.updateItem((Auftrag) bundle.getSerializable(Auftrag.SERIAZABLE_ID));
                }
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
            if (v.getId() == R.id.fragment_touren_planer_ListViewTouren)
            {
                MenuInflater inflater = activity.getMenuInflater();
                inflater.inflate(R.menu.menu_card_options_touren, menu);
            }
        }
    }


    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId())
        {
            case R.id.menu_tour_Delete:
                removeCard(adapterTouren.getItem(info.position));
                return true;

            case R.id.menu_tour_karte_Bearbeiten:
                final Auftrag auftrag = adapterTouren.getItem(info.position);
                if(auftrag != null)
                {
                    Intent intent = new Intent(context, KarteAnzeige.class);
                    intent.putExtra(Auftrag.SERIAZABLE_ID, (Auftrag) auftrag);
                    intent.putExtra(BoardListe.extra_BoardListe, boardListe);
                    startActivityForResult(intent, TourenPlaner.REQUEST_CODE_CLICK_LISTVIEW_ITEM_KARTE_ANZEIGEN);
                }
                return true;

            case R.id.menu_tour_archivieren:

                new ArchiviereAuftrag(getActivity(),
                        adapterTouren.getItem(info.position),
                        new ArchiviereAuftrag.ArchiviereAuftragListener()
                        {
                            @Override
                            public void onResult(Auftrag auftragKarte, boolean success)
                            {
                                if(isAdded())
                                {
                                    if(success)
                                    {
                                        adapterTouren.removeItem(auftragKarte);
                                    }
                                    else
                                    {
                                        Activity activity = getActivity();
                                        if(activity != null && !activity.isFinishing())
                                        {
                                            new AlertDialog.Builder(activity).setTitle(R.string.error_archivieren_title)
                                                    .setMessage(R.string.error_archivieren)
                                                    .show();
                                        }
                                    }
                                }
                            }
                        })
                        .transferSynch();

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
                                adapterTouren.removeItem(auftragKarte);
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


}
