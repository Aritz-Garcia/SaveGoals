package com.savegoals.savegoals;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.savegoals.savegoals.controlador.menu.PagerControllerMenu;

public class ObjetivosActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1, tab2, tab3;
    PagerControllerMenu pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);



        tabLayout = findViewById(R.id.tabLayoutMenu);
        viewPager = findViewById(R.id.viewPagerMenu);

        tab1 = findViewById(R.id.tabObjetivosMenu);
        tab2 = findViewById(R.id.tabEstadisticasMenu);
        tab3 = findViewById(R.id.tabConfiguracionMenu);

        pagerAdapter = new PagerControllerMenu(getSupportFragmentManager(), tabLayout.getTabCount());
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

    }
}