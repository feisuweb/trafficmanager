package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team.monroe.org.trafficmanager.view.MyScrollView;

public abstract class FragmentDashboardDialog extends FragmentDashboardContentSupport implements ContractBackButton {

    private View mTopShadow;
    private MyScrollView mScrollView;
    private View mActionClose;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(getDialogContent(), (ViewGroup) view.findViewById(R.id.panel_page_content), true);
        return view;
    }

    protected abstract int getDialogContent();
    protected abstract String getDialogCaption();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTopShadow = view(R.id.image_scroll_shadow);
        mScrollView = view(R.id.scroll_view, MyScrollView.class);
        view(R.id.text_dialog_caption, TextView.class).setText(getDialogCaption());
        updateShadow(mScrollView.getScrollY());
        mScrollView.mScrollListener = new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int left, int top, int oldl, int oldt) {
                updateShadow(top);
            }
        };
       mActionClose = view(R.id.action_close);
       mActionClose.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onDialogClose();
           }
       });
    }

    protected void visibility_actionClose(boolean visible){
       mActionClose.setVisibility(visible?View.VISIBLE:View.GONE);
    }

    protected abstract void onDialogClose();


    private void updateShadow(int top) {
        if (top > 0){
            mTopShadow.setVisibility(View.VISIBLE);
        }else {
            mTopShadow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onBackPressed() {
        onDialogClose();
        return true;
    }
}
