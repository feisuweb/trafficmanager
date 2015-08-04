package team.monroe.org.trafficmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class FragmentDashboardMultiPage extends FragmentDashboardSupport implements ContractBackButton{

    private ViewPager mViewPager;
    private FragmentPageAdapter mFragmentPagerAdapter;
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dashboard_pager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = view(R.id.view_pager,ViewPager.class);
        mFragmentPagerAdapter = new FragmentPageAdapter(activity().getFragmentManager()) {


            @Override
            public Fragment getItem(int position) {
                Fragment answer = fragmentMap.get(position);
                if (answer == null){
                    answer = createFragment(position);
                    fragmentMap.put(position, answer);
                }
                return answer;
            }

            private Fragment createFragment(int position) {
                switch (position){
                    case 0: return new FragmentBodyPageBandwidthLimits();
                    case 1: return new FragmentBodyPageClients();
                    default:
                        throw new IllegalStateException();
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Fragment fragment = fragmentMap.remove(position);
                super.destroyItem(container, position, object);
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mFragmentPagerAdapter.notifyDataSetChanged();
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dashboard().onScreenChanged(position);
                if (getPage(position) ==null){
                    return;
                }
                getPage(position).onPageSelect();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFragmentPagerAdapter != null) {
            try {
                mViewPager.setAdapter(null);
            }catch (Exception e){}
            mFragmentPagerAdapter = null;
        }
    }

    @Override
    public boolean onBackPressed() {
        FragmentDashboardBodyPage dashboardSlide = getCurrentPage();
        if (dashboardSlide instanceof ContractBackButton){
            if (((ContractBackButton) dashboardSlide).onBackPressed()){
                return true;
            }
        }
        return true;
    }

    public void slideToPage(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    private FragmentDashboardBodyPage getPage(int pageIndex) {
        FragmentDashboardBodyPage page = getFragmentDashboardPageByFragmentManager(pageIndex);
        if (page == null){
           // page = (FragmentDashboardPage) mFragmentPagerAdapter.getItem(pageIndex);
        }
        return page;
    }

    private FragmentDashboardBodyPage getFragmentDashboardPageByFragmentManager(int pageIndex) {
        String pageTag = "android:switcher:" + mViewPager.getId() + ":" + pageIndex;
        return (FragmentDashboardBodyPage) getFragmentManager().findFragmentByTag(pageTag);
    }

    public FragmentDashboardBodyPage getCurrentPage() {
        int curItem = mViewPager.getCurrentItem();
        Fragment fragment = getPage(curItem);
        return (FragmentDashboardBodyPage) fragment;
    }

    public void viewPagerGesture(boolean enabled) {
        mViewPager.requestDisallowInterceptTouchEvent(!enabled);
    }
}
