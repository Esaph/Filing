package esaph.filing.FilingColorBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import esaph.filing.FilingColorBinding.Adapter.AdapterColorBinders;
import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;
import esaph.filing.Utils.ListObserver;

public class FilingColorBind extends EsaphActivity implements ListObserver
{
    private AdapterColorBinders adapterColorBinders;

    private List<ColorBinder> list = new ArrayList<>();
    private TextView textViewColorBind;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing_color_bind);

        Intent intent = getIntent();
        if(intent != null)
        {
            list = (List<ColorBinder>) intent.getSerializableExtra(ColorBinder.extra);
        }

        listView = findViewById(R.id.activity_filing_color_bind_ListView);

        adapterColorBinders = new AdapterColorBinders(getApplicationContext(), this);

        textViewColorBind = findViewById(R.id.activity_filing_color_bind_ButtonFertig);
        textViewColorBind.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra(ColorBinder.extra, (Serializable) list);
                setResult(0, intent);
                finish();
            }
        });

        if(list == null || list.isEmpty())
        {
            list = generateList();
        }

        listView.setAdapter(adapterColorBinders);
        adapterColorBinders.addItems(list);
    }


    private List<ColorBinder> generateList()
    {
        List<ColorBinder> list = new ArrayList<>();
        int[] rainbow = getResources().getIntArray(R.array.filingColors);

        for (int i = 0; i < rainbow.length; i++)
        {
            list.add(new ColorBinder("", rainbow[i]));
        }
        return list;
    }

    @Override
    public void observDataChange(boolean isEmpty)
    {

    }
}