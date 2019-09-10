package com.mets.rassdasshboard.app;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;


    String myvalue1,myvalue2,myvalue3;


    public static HomeFragment newInstance(String Selv1, String Selv2, String Selv3){
        HomeFragment plansFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SelectValue_OrgUnit",Selv1);
        bundle.putString("SelectValue_Entity",Selv2);
        bundle.putString("SelectValue_Period",Selv3);
        plansFragment.setArguments(bundle);

        return plansFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.home_tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        if(getArguments() != null){
             myvalue1 = getArguments().getString("SelectValue_OrgUnit");
             myvalue2 = getArguments().getString("SelectValue_Entity");
             myvalue3 = getArguments().getString("SelectValue_Period");
            Log.e("here data",""+myvalue1);

        }

        return x;
    }
    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        /**
         * Return fragment with respect to Position .
         */
        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 :
                    LifeStyleTabFragment Adults = new LifeStyleTabFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("SelectValue_OrgUnit",myvalue1);
                    bundle.putString("SelectValue_Entity",myvalue2);
                    bundle.putString("SelectValue_Period",myvalue3);
                    Adults.setArguments(bundle);
                    return Adults;
                case 1 :
                    AutoTabFragment STKC = new AutoTabFragment();
                    Bundle bundleSTKC = new Bundle();
                    bundleSTKC.putString("SelectValue_OrgUnit",myvalue1);
                    bundleSTKC.putString("SelectValue_Entity",myvalue2);
                    bundleSTKC.putString("SelectValue_Period",myvalue3);
                    STKC.setArguments(bundleSTKC);
                    return STKC;
                case 2 :
                    AutoTabFragment RTK = new AutoTabFragment();
                    Bundle bundleRTK = new Bundle();
                    bundleRTK.putString("SelectValue_OrgUnit",myvalue1);
                    bundleRTK.putString("SelectValue_Entity",myvalue2);
                    bundleRTK.putString("SelectValue_Period",myvalue3);
                    RTK.setArguments(bundleRTK);
                    return RTK;
            }
            return null;
        }
        @Override
        public int getCount() {
            return int_items;
        }
        /**
         * This method returns the title of the tab according to the position.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "Adults";
                case 1 :
                    return "Paediatrics";
                case 2 :
                    return "RTKS";
            }
            return null;
        }
    }
}
