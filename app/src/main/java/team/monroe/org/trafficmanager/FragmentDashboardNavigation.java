package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.util.HashMap;
import java.util.Map;


public class FragmentDashboardNavigation extends FragmentDashboardSupport {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_navigation;
    }

    private final Map<ActivityDashboard.BodyPageId, NavigationButtonController> controllersMap = new HashMap<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityDashboard.BodyPageId[] bodyPageIds = {ActivityDashboard.BodyPageId.BANDWIDTH_LIMITS, ActivityDashboard.BodyPageId.DEVICES, ActivityDashboard.BodyPageId.BANDWIDTH_PROFILES};
        for (ActivityDashboard.BodyPageId bodyPageId : bodyPageIds) {
            NavigationButtonController controller = NavigationButtonController.build(bodyPageId, view(R.id.panel_nav_btns, GridView.class), dashboard(), constructAction());
            controllersMap.put(bodyPageId, controller);
        }
    }

    private void onNavigationButtonClick(ActivityDashboard.BodyPageId id, NavigationButtonController arg) {

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
            return answer;
        }
    }
}
