package esaph.filing.LabelPicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import esaph.filing.Account.HPreferences;
import esaph.filing.Board.Model.Board;
import esaph.filing.FilingColorBinding.ColorBinder;
import esaph.filing.FilingColorBinding.FilingColorBind;
import esaph.filing.LabelPicker.Adapter.AdapterKatalog;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.ListObserver;
import esaph.filing.Utils.LoadingState.LoadingStateHandler;
import esaph.filing.workers.LoadMyColors;

public class KatalogChooser extends EsaphActivity implements ListObserver
{
    private AdapterKatalog adapterKatalog;
    private LoadingStateHandler loadingStateHandler;
    private ListView listView;
    private TextView textViewBack;
    private TextView textViewPaletteBearbeiten;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog_chooser);
        listView = findViewById(R.id.activity_katalog_chooser_ListView);
        loadingStateHandler = findViewById(R.id.activity_katalog_chooser_LoadingStateHandler);
        textViewBack = findViewById(R.id.activity_katalog_chooser_TextViewUberschrift);
        textViewPaletteBearbeiten = findViewById(R.id.activity_katalog_chooser_TextViewPaletteBearbeiten);

        textViewPaletteBearbeiten.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentFarb = new Intent(KatalogChooser.this, FilingColorBind.class);
                intentFarb.putExtra(ColorBinder.extra, (Serializable) adapterKatalog.getmDataBaseListDisplay());
                startActivityForResult(intentFarb, KatalogChooser.COLOR_PALETTE_CONFIGURATOR);
            }
        });

        textViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ColorBinder.extra, adapterKatalog.getItem(position));
                intent.putExtras(bundle);
                setResult(0, intent);
                finish();
            }
        });

        adapterKatalog = new AdapterKatalog(getApplicationContext(), this);
        listView.setAdapter(adapterKatalog);
        cool();
    }

    private void cool()
    {
        HPreferences hPreferences = new HPreferences(getApplicationContext());
        Board board = hPreferences.getWorkingBench();
        if(board != null)
        {
            new LoadMyColors(getApplicationContext(),
                    loadingStateHandler,
                    board.getmBoardId(),
                    new LoadMyColors.DataLoadListenerColorBinder()
                    {
                        @Override
                        public void onDataLoaded(List<ColorBinder> data, boolean success)
                        {
                            if(!isFinishing())
                            {
                                loadingStateHandler.hideMessage();
                                if(success)
                                {
                                    adapterKatalog.addItems(data);
                                }
                                else
                                {
                                    loadingStateHandler.showMessageTryAgain(R.drawable.ic_warning,
                                            R.string.error_loading,
                                            new LoadingStateHandler.LoadingStateActionListener() {
                                        @Override
                                        public void onAction()
                                        {
                                            cool();
                                        }
                                    });
                                }
                            }
                        }
                    }).transferSynch();
        }
    }

    private static final int COLOR_PALETTE_CONFIGURATOR = 102;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == COLOR_PALETTE_CONFIGURATOR)
        {
            if(data != null)
            {
                List<ColorBinder> list = (List<ColorBinder>) data.getSerializableExtra(ColorBinder.extra);
                adapterKatalog.clear();
                adapterKatalog.addItems(list);
            }
        }
    }

    @Override
    public void observDataChange(boolean isEmpty)
    {
        if(isFinishing()) return;
        if(isEmpty)
        {
            loadingStateHandler.showMessage(R.drawable.ic_break, R.string.ic_board_nicht_ausgewahlt);
        }
        else
        {
            loadingStateHandler.hideMessage();
        }
    }
}
