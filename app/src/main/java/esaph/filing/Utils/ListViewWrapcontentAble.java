package esaph.filing.Utils;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewWrapcontentAble extends ListView {

    public ListViewWrapcontentAble(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewWrapcontentAble(Context context) {
        super(context);
    }

    public ListViewWrapcontentAble(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}