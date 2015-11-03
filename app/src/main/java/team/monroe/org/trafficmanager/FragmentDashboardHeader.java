package team.monroe.org.trafficmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.monroe.team.android.box.app.ApplicationSupport;

import java.io.File;

public class FragmentDashboardHeader extends FragmentDashboardSupport {

    private TextView mTextCaption;
    private View mButtonBack;
    private ImageView mIcon;
    private PopupWindow mMorePopup;

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
        view(R.id.action_more, ImageButton.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMoreMenuClick(v);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMorePopup.dismiss();
        mMorePopup = null;
    }

    private void onMoreMenuClick(View v) {
        if (mMorePopup == null) {
            View view = dashboard().getLayoutInflater().inflate(R.layout.panel_more, null);
            view.findViewById(R.id.action_export_devices).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMorePopup.dismiss();
                    onExportDevices();
                }
            });
            mMorePopup = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mMorePopup.setBackgroundDrawable(new BitmapDrawable());
            mMorePopup.setOutsideTouchable(true);
        }
        mMorePopup.showAsDropDown(v);
    }

    private void onExportDevices() {
        application().function_shareConfigurationDevice(new ApplicationSupport.ValueObserver<Uri>() {
            @Override
            public void onSuccess(Uri value) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, value);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share using"));
            }

            @Override
            public void onFail(Throwable exception) {
                throw new RuntimeException(exception);
            }
        });
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
