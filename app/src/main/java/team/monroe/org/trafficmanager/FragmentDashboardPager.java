package team.monroe.org.trafficmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class FragmentDashboardPager extends AbstractFragmentDashboard implements ContractBackButton{

    private ViewPager mViewPager;
    private FragmentPageAdapter mFragmentPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dashboard_pager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = view(R.id.view_pager,ViewPager.class);
        mFragmentPagerAdapter = new FragmentPageAdapter(activity().getFragmentManager()) {

            private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

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
                    case 0: return new FragmentPageBandwidthLimits();
                    case 1: return new FragmentPageClients();
                    default:
                        throw new IllegalStateException();
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                mFragmentPagerAdapter.getItem(position);
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
            mFragmentPagerAdapter = null;
        }
    }

    @Override
    public boolean onBackPressed() {
        FragmentDashboardPage dashboardSlide = getCurrentSlide();
        if (dashboardSlide instanceof ContractBackButton){
            if (((ContractBackButton) dashboardSlide).onBackPressed()){
                return true;
            }
        }
        return true;
    }

    public void updateScreen(int screenPosition) {
        mViewPager.setCurrentItem(screenPosition, true);
    }

    private FragmentDashboardPage getPage(int pageIndex) {
        FragmentDashboardPage page = getFragmentDashboardPageByFragmentManager(pageIndex);
        if (page == null){
           // page = (FragmentDashboardPage) mFragmentPagerAdapter.getItem(pageIndex);
        }
        return page;
    }

    private FragmentDashboardPage getFragmentDashboardPageByFragmentManager(int pageIndex) {
        String pageTag = "android:switcher:" + mViewPager.getId() + ":" + pageIndex;
        return (FragmentDashboardPage) getFragmentManager().findFragmentByTag(pageTag);
    }

    public FragmentDashboardPage getCurrentSlide() {
        int curItem = mViewPager.getCurrentItem();
        Fragment fragment = getPage(curItem);
        return (FragmentDashboardPage) fragment;
    }

    public void viewPagerGesture(boolean enabled) {
        mViewPager.requestDisallowInterceptTouchEvent(!enabled);
    }
}
