package esaph.filing.Utils.LoadingState;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import esaph.filing.R;

public class LoadingStateHandler extends RelativeLayout
{
    private LoadingStateActionListener loadingStateActionListener;
    private ImageView imageViewIcon;
    private TextView textViewText;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private Button buttonTryAgain;

    public LoadingStateHandler(Context context) {
        super(context);
        init(context);
    }

    public LoadingStateHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingStateHandler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public LoadingStateHandler(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void showMessage(int drawable, int stringId)
    {
        reset();
        this.textViewText.setVisibility(VISIBLE);
        this.imageViewIcon.setVisibility(VISIBLE);
        this.textViewText.setText(getResources().getString(stringId));
        this.imageViewIcon.setImageResource(drawable);
    }

    public void showMessageTryAgain(int drawable,
                                    int stringId,
                                    LoadingStateActionListener loadingStateActionListener)
    {
        reset();
        this.loadingStateActionListener = loadingStateActionListener;
        this.textViewText.setVisibility(VISIBLE);
        this.imageViewIcon.setVisibility(VISIBLE);
        this.buttonTryAgain.setVisibility(VISIBLE);

        this.textViewText.setText(getResources().getString(stringId));
        this.imageViewIcon.setImageResource(drawable);
    }

    public void hideMessage()
    {
        reset();
    }

    public void showLoading()
    {
        reset();
        this.avLoadingIndicatorView.smoothToShow();
    }

    private void reset()
    {
        this.textViewText.setVisibility(GONE);
        this.imageViewIcon.setVisibility(GONE);
        this.buttonTryAgain.setVisibility(GONE);
        this.avLoadingIndicatorView.smoothToHide();
    }

    public void hideLoading()
    {
        this.avLoadingIndicatorView.smoothToHide();
    }

    public interface LoadingStateActionListener
    {
        void onAction();
    }

    private void init(Context context)
    {
        Log.i(getClass().getName(), "init() called");

        inflate(getContext(), R.layout.layout_loading_state_handler, this);
        this.imageViewIcon = (ImageView) findViewById(R.id.layout_loading_state_handler_ImageViewIcon);
        this.textViewText = (TextView) findViewById(R.id.layout_loading_state_handler_TextViewText);
        this.avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.layout_loading_state_handler_AVLoadingIndicatorView);
        this.buttonTryAgain = (Button) findViewById(R.id.layout_loading_state_handler_TextViewTryAgain);

        this.buttonTryAgain.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(loadingStateActionListener == null) return;
                reset();
                loadingStateActionListener.onAction();
            }
        });
        reset();
    }

    public void setLoadingStateActionListener(LoadingStateActionListener loadingStateActionListener) {
        this.loadingStateActionListener = loadingStateActionListener;
    }
}