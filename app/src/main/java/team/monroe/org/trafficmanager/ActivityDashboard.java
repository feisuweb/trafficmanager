package team.monroe.org.trafficmanager;

import android.app.Fragment;
import android.os.Bundle;

import org.monroe.team.android.box.app.ActivitySupport;


public class ActivityDashboard extends ActivitySupport<App> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (isFirstRun()){
            getFragmentManager()
                    .beginTransaction()
                       .add(R.id.frag_dash_header, new FragmentDashboardHeader())
                       .add(R.id.frag_dash_navigation, new FragmentDashboardNavigation())
                    .commit();
            if (application().function_hasRouterConfiguration()){
                getFragmentManager()
                        .beginTransaction()
                          .add(R.id.frag_dash_body, new FragmentDashboardMultiPage())
                        .commit();
            }else {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.frag_dash_body, new FragmentDashboardBodyPageRouterConfiguration())
                        .commit();
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

    public void setHeaderText(String headerText) {
        FragmentDashboardHeader dashboardHeader = (FragmentDashboardHeader) getFragmentManager().findFragmentById(R.id.frag_dash_header);
        dashboardHeader.setText(headerText);
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

    public static class FragmentTransitionSet{

        public final int inAnimation;
        public final int outAnimation;

        public FragmentTransitionSet(int inAnimation, int outAnimation) {
            this.inAnimation = inAnimation;
            this.outAnimation = outAnimation;
        }
    }
}
