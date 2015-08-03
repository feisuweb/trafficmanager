package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.trafficmanager.view.MyScrollView;

public abstract class FragmentBodyPageScrollablePanel extends FragmentDashboardBodyPage {

    private View mTopShadow;
    private MyScrollView mScrollView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_page_scrollable_panel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(getPanelLayoutId(), (ViewGroup) view.findViewById(R.id.panel_page_content), true);
        return view;
    }

    protected abstract int getPanelLayoutId();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTopShadow = view(R.id.image_scroll_shadow);
        mScrollView = view(R.id.scroll_view, MyScrollView.class);

        updateShadow(mScrollView.getScrollY());
        mScrollView.mScrollListener = new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int left, int top, int oldl, int oldt) {
                updateShadow(top);
            }
        };
    }

    private void updateShadow(int top) {
        if (top > 0){
            mTopShadow.setVisibility(View.VISIBLE);
        }else {
            mTopShadow.setVisibility(View.INVISIBLE);
        }
    }
}
