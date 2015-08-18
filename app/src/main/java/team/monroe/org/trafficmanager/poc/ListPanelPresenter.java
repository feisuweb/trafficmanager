package team.monroe.org.trafficmanager.poc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ListPanelPresenter<DataType> {

    private final LayoutInflater mInflater;
    private final ViewGroup mPanel;
    private final DataViewResolver<DataType> mViewResolver;
    private final DataViewResolver<Object> mFallbackViewResolver;

    public ListPanelPresenter(LayoutInflater inflater, ViewGroup parent,
                              DataViewResolver<DataType> viewResolver,
                              DataViewResolver<Object> noFallbackViewResolver) {
        this.mInflater = inflater;
        this.mPanel = parent;
        this.mViewResolver = viewResolver;
        this.mFallbackViewResolver = noFallbackViewResolver;
    }

    public void updateUI(List<DataType> dataList) {
        mPanel.removeAllViews();
        if (dataList == null || dataList.isEmpty()){
            View view = mFallbackViewResolver.build(null, mPanel, mInflater);
            mPanel.addView(view);
        }else {
            for (DataType dataType : dataList) {
                View view = mViewResolver.build(dataType, mPanel, mInflater);
                mPanel.addView(view);
            }
        }
        mPanel.requestLayout();
        mPanel.invalidate();
    }


    public void updateUI(Object object) {
        mPanel.removeAllViews();
        View view = mFallbackViewResolver.build(object, mPanel, mInflater);
        mPanel.addView(view);
        mPanel.requestLayout();
        mPanel.invalidate();
    }

    public static interface DataViewResolver<DataType>{
        public View build(DataType type, ViewGroup parent, LayoutInflater inflater);
    }

}
