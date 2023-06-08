package com.savegoals.savegoals.controlador.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.controlador.estObjetivos.PagerControllerEstObjetivos;

public class EstadisticasFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1, tab2, tab3;
    PagerControllerEstObjetivos pagerAdapter;
    SharedPreferences settingssp;

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        tabLayout = view.findViewById(R.id.tabLayoutEst);
        viewPager = view.findViewById(R.id.viewPagerEst);
        tab1 = view.findViewById(R.id.tabEstEvolucion);
        tab2 = view.findViewById(R.id.tabEstObjetivos);
        tab3 = view.findViewById(R.id.tabEstAhorros);

        pagerAdapter = new PagerControllerEstObjetivos(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            // @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            // @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}