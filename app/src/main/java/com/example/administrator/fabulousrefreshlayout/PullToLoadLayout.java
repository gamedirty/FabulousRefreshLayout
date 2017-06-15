package com.example.administrator.fabulousrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 *
 */
public class PullToLoadLayout extends RelativeLayout {

    private static final CharSequence NEXT_LOAD = "松手加载下一条";
    private static final CharSequence NEXT_LOAD_TOPULL = "上拉加载下一条房源信息";
    private PullUpLoadLayout.IUpSlidable scrollView;
    private TextView headView;
    private int headH;//整个控件的高度
    private float dY;
    private float ratio = 2.0f;//下拉时候的滑动系数

    public PullToLoadLayout(Context context) {
        super(context);

        init();
    }

    public void setExtraTextSize(float textSize) {
        if (headView != null) {
            headView.setTextSize(textSize);
        }
    }

    void init() {
        headView = new TextView(getContext());
        headView.setGravity(Gravity.CENTER);
        int textSize = sp2px(getContext(), 5);
        headH = textSize * 2;
        headView.setTextSize(textSize);
        addView(headView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public PullToLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 根据dy进行布局
     * dy大于0 是下拉 head布局位于上方
     * dy小于0 是上拉 head布局位于下方
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (shouldInterupt) {//只有需要阻隔手势的时候处理
            float d = Math.abs(dY);
            d /= ratio;
            d = Math.min(d, headH * 2);//限制下拉的距离

            headView.layout(l, (int) (t + getMeasuredHeight() - d) - t, r, (int) (b + headH - d) - t);
            ((View) scrollView).layout(l, (int) (t - d) - t, r, (int) (b - d) - t);

        } else {
            headView.layout(l, -headH - t, r, 0 - t);
            ((View) scrollView).layout(l, t - t, r, b - t);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildAt(0) instanceof ScrollView) {
            scrollView = (PullUpLoadLayout.IUpSlidable) getChildAt(0);
        } else {
            scrollView = (PullUpLoadLayout.IUpSlidable) getChildAt(1);
        }

    }

    /**
     * 设置scrollview 的高度为占满整个控件
     * <p/>
     * 设置headview的高度为其原先高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild((View) scrollView, widthMeasureSpec, resolveSize(getMeasuredHeight(), heightMeasureSpec));
        headH = headView.getMeasuredHeight();
    }


    /**
     * @return 当前状态是不是可以上拉加载
     */
    private boolean canPullUp() {
        return scrollView.canPullUp();
    }

    private boolean canUp;//可以上拉
    private float downY;//手指落下时候记录的坐标

    private boolean shouldInterupt;

    /**
     * 手指落下的时候记录是不是可以下拉或者上拉
     * <p/>
     * 如果满足了条件  则本类的onTouchEvent方法拦截处理之后的move事件
     * 如果move事件满足条件 就对之后的事件进行拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float eY = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                canUp = canPullUp();
                downY = eY;
                break;
            case MotionEvent.ACTION_MOVE:
                dY = eY - downY;
                if (canUp && dY < 0) {
                    shouldInterupt = true;
                    //处理dy  根据dy确定是向上滑动还是向下滑动 处理布局的规格
                    requestLayout();
                    setHeadViewText();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL://手势抬起  回复工具变量
                if (shouldInterupt) {

                }
                shouldInterupt = false;
                canUp = false;
                dY = 0;
                shouldLoadNext = false;
                requestLayout();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean shouldLoadNext;//可不可以加载下一个

    /**
     *
     */
    private void setHeadViewText() {
        float absh = Math.abs(dY);
        absh /= ratio;
        if (absh < headH) {//不够长
            headView.setText(NEXT_LOAD_TOPULL);
            shouldLoadNext = false;
        } else {//松手加载
            headView.setText(NEXT_LOAD);
            if (dY < 0) {
                shouldLoadNext = true;
            }
        }
    }


    /**
     * 手势被拦截的条件：
     * 满足任一可下拉可上拉的条件  且同时上拉和下拉的一段距离>{@slop}
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (shouldInterupt) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }


}
