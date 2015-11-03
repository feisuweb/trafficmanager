package team.monroe.org.trafficmanager;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.util.HashMap;
import java.util.Map;

import team.monroe.org.trafficmanager.view.MyScrollView;


public class FragmentDashboardNavigation extends FragmentDashboardSupport {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_navigation;
    }

    private final Map<ActivityDashboard.BodyPageId, NavigationButtonController> controllersMap = new HashMap<>();
    private NavigationButtonController activatedPageController;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityDashboard.BodyPageId[] bodyPageIds = {ActivityDashboard.BodyPageId.BANDWIDTH_LIMITS, ActivityDashboard.BodyPageId.DEVICES, ActivityDashboard.BodyPageId.BANDWIDTH_PROFILES};
        for (ActivityDashboard.BodyPageId bodyPageId : bodyPageIds) {
            NavigationButtonController controller = NavigationButtonController.build(bodyPageId, view(R.id.panel_nav_btns, GridView.class), dashboard(), constructAction());
            controllersMap.put(bodyPageId, controller);
        }

        final ImageView cloudImage = view(R.id.image_cloud, ImageView.class);
        view(R.id.scroll, MyScrollView.class).mScrollListener = new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int left, int top, int oldl, int oldt) {
                cloudImage.setTranslationY(top / 2);
            }
        };

    }

    private void onNavigationButtonClick(ActivityDashboard.BodyPageId id, NavigationButtonController arg) {
        if (activatedPageController == arg) return;
        dashboard().open_page(arg.id);
    }

    private Closure<NavigationButtonController, Void> constructAction() {
        return new Closure<NavigationButtonController, Void>() {
            @Override
            public Void execute(NavigationButtonController arg) {
                onNavigationButtonClick(arg.id, arg);
                return null;
            }
        };
    }

    public void update(ActivityDashboard.BodyPageId pageId) {
        if (activatedPageController != null){
            if (activatedPageController.id == pageId) return;
            activatedPageController.clearSelection(getResources());
            activatedPageController = null;
        }

        if (!controllersMap.containsKey(pageId)) return;
        activatedPageController = controllersMap.get(pageId);
        activatedPageController.captureSelection(getResources());
    }


    public static class NavigationButtonController{

        private final ActivityDashboard.BodyPageId id;
        private final TextView title;
        private final ImageView image;
        private final Button button;

        private NavigationButtonController(ActivityDashboard.BodyPageId id, TextView title, ImageView image, Button button) {
            this.id = id;
            this.title = title;
            this.image = image;
            this.button = button;
        }

        public static NavigationButtonController build(ActivityDashboard.BodyPageId id, ViewGroup parent, ActivityDashboard dashboard, final Closure<NavigationButtonController, Void> action){

            View view = dashboard.getLayoutInflater().inflate(R.layout.navigation_button, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.text);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            Button actionView = (Button) view.findViewById(R.id.action);

            ActivityDashboard.BodyPageInfo info = dashboard.resolvePageInfo(id);
            textView.setText(info.title);
            imageView.setImageResource(info.icon);
            parent.addView(view);
            final NavigationButtonController answer = new NavigationButtonController(id, textView, imageView, actionView);
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.execute(answer);
                }
            });
            answer.clearSelection(dashboard.getResources());
            return answer;
        }

        public void clearSelection(Resources resources) {
            title.setTextColor(resources.getColor(R.color.text_dark));
            image.setAlpha(0.3f);
            title.setAlpha(0.5f);
        }

        public void captureSelection(Resources resources) {
            title.setTextColor(resources.getColor(R.color.text_highlight));
            image.setAlpha(1f);
            title.setAlpha(1f);
        }
    }
}
