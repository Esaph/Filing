package esaph.filing.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import esaph.filing.R;

public class EsaphDialog extends Dialog
{
    private TextView textViewTitle;
    private TextView textViewDetails;
    private Button buttonPositive;
    private Button buttonNegative;

    private String Title;
    private String Detail;
    private String mButtonTextPositiv;
    private String mButtonTextNegative;

    public static class EsaphDialogBuilder
    {
        private String Title;
        private String Detail;
        private String mButtonTextPositiv;
        private String mButtonTextNegative;

        private EsaphDialogBuilder()
        {
            this.Title = "";
            this.Detail = "";
            this.mButtonTextPositiv = "";
            this.mButtonTextNegative = "";
        }

        public static EsaphDialogBuilder getInstance()
        {
            return new EsaphDialogBuilder();
        }

        public EsaphDialog request(AppCompatActivity appCompatActivity)
        {
            EsaphDialog esaphDialog = new EsaphDialog(
                    appCompatActivity,
                    Title,
                    Detail,
                    mButtonTextPositiv,
                    mButtonTextNegative);

            esaphDialog.show();
            return esaphDialog;
        }

        public EsaphDialogBuilder setTitle(String Title) {
            this.Title = Title;
            return this;
        }

        public EsaphDialogBuilder setDetail(String Detail) {
            this.Detail = Detail;
            return this;
        }

        public EsaphDialogBuilder setmButtonTextNegative(String mButtonTextNegative) {
            this.mButtonTextNegative = mButtonTextNegative;
            return this;
        }

        public EsaphDialogBuilder setmButtonTextPositiv(String mButtonTextPositiv) {
            this.mButtonTextPositiv = mButtonTextPositiv;
            return this;
        }
    }

    private EsaphDialog(@NonNull Context context,
                       String Title,
                       String Details,
                       String mButtonTextPositiv,
                       String mButtonTextNegative)
    {
        super(context);

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        this.Title = Title;
        this.Detail = Details;
        this.mButtonTextPositiv = mButtonTextPositiv;
        this.mButtonTextNegative = mButtonTextNegative;
    }

    private EsaphDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EsaphDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_esaph_dialog);
        textViewTitle = (TextView) findViewById(R.id.layout_esaph_dialog_TextViewTitle);
        textViewDetails = (TextView) findViewById(R.id.layout_esaph_dialog_TextViewDetails);
        buttonPositive = (Button) findViewById(R.id.layout_esaph_dialog_ButtonPositiv);
        buttonNegative = (Button) findViewById(R.id.layout_esaph_dialog_ButtonNegative);

        textViewTitle.setText(Title);
        textViewDetails.setText(Detail);


        buttonPositive.setVisibility(!TextUtils.isEmpty(mButtonTextPositiv) ? View.VISIBLE : View.GONE);
        buttonNegative.setVisibility(!TextUtils.isEmpty(mButtonTextNegative) ? View.VISIBLE : View.GONE);


        buttonNegative.setText(mButtonTextNegative);
        buttonPositive.setText(mButtonTextPositiv);
    }


    public Button getButtonPositive() {
        return buttonPositive;
    }

}
