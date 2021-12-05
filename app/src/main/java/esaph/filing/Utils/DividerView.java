package esaph.filing.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.util.AttributeSet;
import android.view.View;

import esaph.filing.R;

public class DividerView extends View
{
    static public int ORIENTATION_HORIZONTAL = 0;
    static public int ORIENTATION_VERTICAL = 1;
    private Paint mPaint;
    private int orientation;




    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int dashGap, dashLength, dashThickness;
        int color;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DividerView, 0, 0);

        try
        {
            dashGap = a.getDimensionPixelSize(R.styleable.DividerView_dashGap, 5);
            dashLength = a.getDimensionPixelSize(R.styleable.DividerView_dashLength, 5);
            dashThickness = a.getDimensionPixelSize(R.styleable.DividerView_dashThickness, 3);
            color = a.getColor(R.styleable.DividerView_color, 0xff000000);
            orientation = a.getInt(R.styleable.DividerView_orientation, ORIENTATION_HORIZONTAL);
        }
        finally
        {
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dashThickness);
        mPaint.setPathEffect(new DashPathEffect(new float[]{10,10}, 10));
    }

    public DividerView(Context context)
    {
        this(context, null);
    }

    public void setColor(int color)
    {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (orientation == ORIENTATION_HORIZONTAL)
        {
            float center = getHeight() * .5f;
            canvas.drawLine(0, center, getWidth(), center, mPaint);
        }
        else {
            float center = getWidth() * .5f;
            canvas.drawLine(center, 0, center, getHeight(), mPaint);
        }
    }
}