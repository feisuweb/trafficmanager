package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.monroe.team.android.box.app.ApplicationSupport;

import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;

public class FragmentDashboardBodyPageRouterConfiguration extends FragmentBodyPageDefault {

    @Override
    protected int getPanelLayoutId() {
        return R.layout.fragment_dashboard_router_configuration;
    }

    @Override
    protected ActivityDashboard.BodyPageId getPageId() {
        return ActivityDashboard.BodyPageId.ROUTER_CONFIGURATION;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.action_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cation_saveConfiguration();
            }
        });
    }

    @Override
    protected String customizeIssueActionText(int issueCode) {
        return "Change settings";
    }

    @Override
    protected boolean onIssueAction(int issueCode) {
        showContent();
        return true;
    }

    private void cation_saveConfiguration() {
        showLoading();
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(
                getFieldText(R.id.text_user, "admin"),
                getFieldText(R.id.text_password, "admin"),
                getFieldText(R.id.text_host, "192.168.0.1"),
                getFieldText(R.id.text_port, "80")
        );

        application().function_routerConfigurationSave(connectionConfiguration, new ApplicationSupport.ValueObserver<Void>() {
            @Override
            public void onSuccess(Void value) {
                showContent();
                dashboard().open_mainSlider();
            }

            @Override
            public void onFail(Throwable exception) {
                handleException(exception);
            }
        });
    }

    private String getFieldText(int editId, String defaultValue) {
        String text = view(editId, TextView.class).getText().toString().trim();
        if (text.isEmpty()){
            text = defaultValue;
        }
        return text;
    }
}
