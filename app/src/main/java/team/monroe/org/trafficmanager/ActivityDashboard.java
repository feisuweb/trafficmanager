package team.monroe.org.trafficmanager;

import android.app.Fragment;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.monroe.team.android.box.app.ActivitySupport;
import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.corebox.utils.Closure;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceInfo;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;


public class ActivityDashboard extends ActivitySupport<App> {

    private View mLayerShadow;
    private AppearanceController ac_layerShadow;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mDrawerLayout = view(R.id.drawer_layout, DrawerLayout.class);
        if (isFirstRun()){
            getFragmentManager()
                    .beginTransaction()
                       .add(R.id.frag_dash_header, new FragmentDashboardHeader())
                       .add(R.id.frag_dash_navigation, new FragmentDashboardNavigation())
                       .add(R.id.frag_dash_body, new FragmentDashboardMultiPage())
                    .commit();
        }

        mLayerShadow = view(R.id.layer_shadow);
        mLayerShadow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ac_layerShadow = animateAppearance(mLayerShadow, alpha(1f,0f))
                .hideAndGone()
                .showAnimation(duration_constant(300),interpreter_decelerate(0.5f))
                .hideAnimation(duration_constant(300),interpreter_accelerate(0.5f))
                .build();

        if (getFragmentManager().findFragmentById(R.id.frag_popup) == null){
            enableNavigationDrawer(true);
            visibility_shadow(false, false);
        }else {
            enableNavigationDrawer(false);
            visibility_shadow(true, false);
        }
        if (getIntent() != null && isFirstRun()) {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            if (uri != null) {
                if (uri.getPath().endsWith(".tm")) {
                    try {
                        ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                        application().function_loadConfiguration(fileDescriptor.getFileDescriptor(), new ApplicationSupport.ValueObserver<Void>() {
                            @Override
                            public void onSuccess(Void value) {
                                Toast.makeText(ActivityDashboard.this, "Configuration added", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFail(Throwable exception) {
                                handle_Error(exception);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        ContractBackButton backButtonContractAware = (ContractBackButton) getFragmentManager().findFragmentById(R.id.frag_popup);
        if (backButtonContractAware != null){
            if (backButtonContractAware.onBackPressed()){
                return;
            }
        }
        super.onBackPressed();
    }

    private void visibility_shadow(boolean visible, boolean animate) {
        if (animate){
            if (visible){
                ac_layerShadow.show();
            }else {
                ac_layerShadow.hide();
            }
        }else {
            if (visible){
                ac_layerShadow.showWithoutAnimation();
            }else {
                ac_layerShadow.hideWithoutAnimation();
            }
        }
    }

    public void onScreenChanged(int position) {}

    private <Type extends Fragment> Type getBody(Class<Type> aClass) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frag_dash_body);
        if (!aClass.isInstance(fragment)){
            return null;
        }
        return (Type) fragment;
    }

    public void updateHeader(BodyPageId pageId, boolean stepBackSupport) {
        FragmentDashboardHeader dashboardHeader = (FragmentDashboardHeader) getFragmentManager().findFragmentById(R.id.frag_dash_header);
        dashboardHeader.update(pageId, stepBackSupport);

        FragmentDashboardNavigation fragmentDashboardNavigation = (FragmentDashboardNavigation) getFragmentManager().findFragmentById(R.id.frag_dash_navigation);
        fragmentDashboardNavigation.update(pageId);
    }

    public FragmentDashboardBodyPage getCurrentPage() {
        Fragment fragment = getBody(Fragment.class);
        if (fragment == null)return null;
        FragmentDashboardBodyPage page;
        if (fragment instanceof FragmentDashboardMultiPage){
            page = ((FragmentDashboardMultiPage) fragment).getCurrentPage();
        }else {
            page = (FragmentDashboardBodyPage) fragment;
        }
        return page;
    }

    final protected FragmentTransitionSet animation_slide_from_left() {
        return new FragmentTransitionSet(R.animator.slide_in_from_right, R.animator.slide_out_to_left);
    }

    final protected FragmentTransitionSet animation_slide_from_right() {
        return new FragmentTransitionSet(R.animator.slide_in_from_left, R.animator.slide_out_to_right);
    }

    final protected FragmentTransitionSet animation_slide_out_from_right() {
        return new FragmentTransitionSet(R.animator.slide_in_from_left, R.animator.scale_out_to_right);
    }

    final protected FragmentTransitionSet animation_down_up() {
        return new FragmentTransitionSet(R.animator.gone_up, R.animator.gone_down);
    }

    final protected void replaceBody(Fragment fragment, FragmentTransitionSet transition) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(transition.inAnimation, transition.outAnimation)
                .replace(R.id.frag_dash_body, fragment)
                .commit();
    }

    public BodyPageInfo resolvePageInfo(BodyPageId pageId) {
        String title ="";
        int icon = 0;
        switch (pageId){
            case ROUTER_CONFIGURATION:
                title = "Router Configuration";
                icon = R.drawable.android_router;
                break;
            case BANDWIDTH_LIMITS:
                title = "Bandwidth Limits";
                icon = R.drawable.android_components;
                break;
            case DEVICES:
                title = "Registered Devices";
                icon = R.drawable.android_devices;
                break;
            case  BANDWIDTH_PROFILES:
                title = "Bandwidth Profiles";
                icon = R.drawable.android_tune;
                break;
            default:
                throw new IllegalStateException("Unsupported");
        }
        return new BodyPageInfo(title, icon, pageId);
    }

    public void open_routerConfiguration() {
        replaceBody(new FragmentDashboardBodyPageRouterConfiguration(), animation_slide_from_left());
    }

    public void open_mainSlider() {
        replaceBody(new FragmentDashboardMultiPage(), animation_slide_from_right());
    }

    public void dialog_editDeviceAlias(DeviceInfo deviceInfo) {
        visibility_shadow(true, true);
        FragmentDialogAliasEdit fragment = new FragmentDialogAliasEdit();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DeviceInfo.class.getName(), deviceInfo);
        fragment.setArguments(bundle);
        applyPopupFragment(fragment);
    }

    private void applyPopupFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.frag_popup, fragment)
                .commit();
        enableNavigationDrawer(false);
    }

    public void dialog_editBandwidthProfile(BandwidthProfile profile) {
        visibility_shadow(true, true);
        FragmentDialogBandwidthProfileEdit fragment = new FragmentDialogBandwidthProfileEdit();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BandwidthProfile.class.getName(), profile);
        fragment.setArguments(bundle);
        applyPopupFragment(fragment);
    }

    public void dialog_execute(Closure<Void,Boolean> execution) {
        application().createPendingExecution(execution);
        visibility_shadow(true, true);
        FragmentDialogExecute fragment = new FragmentDialogExecute();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        applyPopupFragment(fragment);
    }

    public void dialog_close() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frag_popup);
        getFragmentManager().beginTransaction().remove(fragment).commit();
        visibility_shadow(false, true);
        enableNavigationDrawer(true);
    }

    public void open_page(BodyPageId id) {
        Fragment fragment = getBody(Fragment.class);
        if (fragment == null) return;
        if (fragment instanceof FragmentDashboardMultiPage) {
            ((FragmentDashboardMultiPage)fragment).slideToPage(id);
        } else {
            throw new IllegalStateException("Not supported");
        }
        //Close navigation drawer
        closeNavigationDrawer();
    }

    private void closeNavigationDrawer() {
        if (mDrawerLayout == null) return;
        mDrawerLayout.closeDrawer(view(R.id.frag_dash_navigation));
    }

    private void enableNavigationDrawer(boolean enable) {
        if (mDrawerLayout == null) return;
        if (enable){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }


    public static class FragmentTransitionSet{

        public final int inAnimation;
        public final int outAnimation;

        public FragmentTransitionSet(int inAnimation, int outAnimation) {
            this.inAnimation = inAnimation;
            this.outAnimation = outAnimation;
        }
    }

    public static enum BodyPageId{
        ROUTER_CONFIGURATION, BANDWIDTH_LIMITS, BANDWIDTH_PROFILES, DEVICES
    }

    public static class BodyPageInfo{

        public final String title;
        public final int icon;
        public final BodyPageId id;

        public BodyPageInfo(String title, int icon, BodyPageId id) {
            this.title = title;
            this.icon = icon;
            this.id = id;
        }
    }
}
