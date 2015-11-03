package team.monroe.org.trafficmanager.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by mrjbee on 8/2/15.
 */
public class MyScrollView extends ScrollView {

    public OnScrollListener mScrollListener;
    public OnOverScrollListener mOverScrollListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldl, int oldt) {
        super.onScrollChanged(left, top, oldl, oldt);
        if (mScrollListener != null){
            mScrollListener.onScrollChanged(left, top, oldl, oldt);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (mOverScrollListener != null){
            mOverScrollListener.onOverScroll(scrollX, scrollY, clampedX, clampedY);
        }
    }

    public static interface OnScrollListener{
        void onScrollChanged(int left, int top, int oldl, int oldt);
    }

    public static interface OnOverScrollListener{
        void onOverScroll(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }
}
