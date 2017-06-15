package com.example.administrator.fabulousrefreshlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 目标：可插拔、低侵入
 * Created by zhjh on 2017/6/15.
 */

public class PullUpLoadLayout extends RelativeLayout {
    private static final CharSequence NEXT_LOAD = "松手展示商品列表";
    private static final CharSequence NEXT_LOAD_TOPULL = "上拉查看推荐商品列表";
    private IUpSlidable upper;
    private TextView headView;
    private int headH;//整个控件的高度
    private boolean shouldInterupt;
    private float dY;
    private float ratio = 2.0f;//下拉时候的滑动系数
    private View child;
    private boolean canUp;//可以上拉
    private float downY;//手指落下时候记录的坐标

    public PullUpLoadLayout(Context context) {
        super(context);
        init();
    }

    public PullUpLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullUpLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        headView = new TextView(getContext());
        headView.setGravity(Gravity.CENTER);
        int textSize = sp2px(getContext(), 15);
        headH = textSize * 2;
        headView.setTextSize(textSize);
        addView(headView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setUpper(IUpSlidable upper) {
        this.upper = upper;
    }

    public static PullUpLoadLayout wrap(IUpSlidable upper, Context context) {
        PullUpLoadLayout pullUpLoadLayout = new PullUpLoadLayout(context);

        View view = (View) upper;
        return pullUpLoadLayout;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (shouldInterupt) {//只有需要阻隔手势的时候处理
            float d = Math.abs(dY);
            d /= ratio;
//            d = Math.min(d, headH * 2);//限制下拉的距离
            L.i("布局");
            headView.layout(l, (int) (t + getMeasuredHeight() - d) - t, r, (int) (b + headH - d) - t);
            child.layout(l, (int) (t - d) - t, r, (int) (b - d) - t);
        } else {
            headView.layout(l, -headH - t, r, 0 - t);
            child.layout(l, t - t, r, b - t);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child = getChildAt(1);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        L.i("高度："+child.getMeasuredHeight()+","+headView.getMeasuredHeight()+","+headH);
        headH = headView.getMeasuredHeight();

    }

    /*
    手指移动过程中，达到临界点，记录此时临界点y坐标位置。
    手指继续移动，与临界点做判断，如果是增大，表示向下滑动，得出移动距离dy，根据dy计算textview位置和显示内容
     */

    private boolean hasRecordDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float eY = ev.getY();
        canUp = upper != null && upper.canPullUp();
        L.i("canUp：" + canUp);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (canUp && !hasRecordDownY) {//达到临界点，并且没有记录临界坐标
                    downY = eY;
                    hasRecordDownY = true;
                    Log.i("zhjh", "记录了临界位置:" + downY);
                }
                dY = eY - downY;
                L.i("dY:" + dY);
                if (canUp || hasRecordDownY) {//处于上拉状态
                    if (dY < 0) {
                        shouldInterupt = true;
                        //处理dy  根据dy确定是向上滑动还是向下滑动 处理布局的规格
                        requestLayout();
                        setHeadViewText();
                    } else {//处于正常下拉状态
                        L.i("下拉状态");
                        shouldInterupt = false;
                        requestLayout();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL://手势抬起  回复工具变量
                boolean s = shouldInterupt;
                shouldInterupt = false;
                canUp = false;
                hasRecordDownY = false;
                dY = 0;
                requestLayout();
                if (onReleaseListener!=null&&state==1){
                    onReleaseListener.onRelease();
                }
                if (s) return true;
                break;
        }
        if (shouldInterupt) return true;
        else
            return super.dispatchTouchEvent(ev);
    }

    private int state;//1松手刷新、2上拉中
    private void setHeadViewText() {
        float absh = Math.abs(dY);
        absh /= ratio;
        if (absh < headH) {//不够长
            headView.setText(NEXT_LOAD_TOPULL);
            state = 2;
        } else {//松手加载
            state = 1;
            headView.setText(NEXT_LOAD);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (shouldInterupt) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface IUpSlidable {
        boolean canPullUp();
    }

    public interface OnReleaseListener{
        void onRelease();
    }

    private OnReleaseListener onReleaseListener;
    public void setOnReleaseListener(OnReleaseListener onReleaseListener){
        this.onReleaseListener = onReleaseListener;
    }
}
