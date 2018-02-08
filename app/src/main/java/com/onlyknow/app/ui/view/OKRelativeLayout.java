package com.onlyknow.app.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2018/1/17.
 */

public class OKRelativeLayout extends RelativeLayout {
    public OKRelativeLayout(Context context) {
        super(context);
    }

    public OKRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OKRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OKRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
