package com.example.administrator.fabulousrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by zhjh on 2017/6/14.
 */

public class FabulousLayout extends ViewGroup {
    public FabulousLayout(Context context) {
        super(context);
    }

    public FabulousLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FabulousLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
