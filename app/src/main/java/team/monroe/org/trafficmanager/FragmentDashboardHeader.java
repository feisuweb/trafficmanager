package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentDashboardHeader extends FragmentDashboardSupport {

    private TextView mTextCaption;
    private View mButtonBack;
    private ImageView mIcon;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_header;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextCaption = view(R.id.text_caption, TextView.class);
        mButtonBack = view(R.id.button_back);
        mIcon = view(R.id.image_icon, ImageView.class);
    }

    public void update(ActivityDashboard.BodyPageId pageId, boolean stepBackSupport) {
        mButtonBack.setVisibility(stepBackSupport ? View.VISIBLE : View.INVISIBLE);
        ActivityDashboard.BodyPageInfo pageInfo = dashboard().resolvePageInfo(pageId);
        if (stepBackSupport){
            mIcon.setImageResource(R.drawable.android_left_arrow);
        }else {
            mIcon.setImageResource(pageInfo.icon);
        }
        mTextCaption.setText(pageInfo.title);
    }
}
