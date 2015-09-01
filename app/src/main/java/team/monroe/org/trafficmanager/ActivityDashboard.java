package team.monroe.org.trafficmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import org.monroe.team.android.box.app.ActivitySupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceInfo;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;


public class ActivityDashboard extends ActivitySupport<App> {

    private View mLayerShadow;
    private AppearanceController ac_layerShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
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
            visibility_shadow(false, false);
        }else {
            visibility_shadow(true, false);
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
                title = "Devices";
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
        getFragmentManager().beginTransaction()
                .add(R.id.frag_popup, fragment)
                .commit();
    }

    public void dialog_editBandwidthProfile(BandwidthProfile profile) {
        visibility_shadow(true, true);
        FragmentDialogBandwidthProfileEdit fragment = new FragmentDialogBandwidthProfileEdit();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BandwidthProfile.class.getName(), profile);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.frag_popup, fragment)
                .commit();
    }

    public void dialog_close() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frag_popup);
        getFragmentManager().beginTransaction().remove(fragment).commit();
        visibility_shadow(false, true);
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
