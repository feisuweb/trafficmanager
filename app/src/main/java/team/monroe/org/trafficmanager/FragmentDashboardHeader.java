package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.widget.TextView;

public class FragmentDashboardHeader extends FragmentDashboardSupport {
    private TextView mTextCaption;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_header;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextCaption = view(R.id.text_caption, TextView.class);
    }

    public void setText(String text) {
        mTextCaption.setText(text);
    }
}
