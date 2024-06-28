package com.savegoals.savegoals.controlador.estObjetivos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerControllerEstObjetivos extends FragmentPagerAdapter {

    int numftabs;

    public PagerControllerEstObjetivos(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numftabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new EstEvolucionFragment();
            case 1:
                return new EstObjetivosFragment();
            case 2:
                return new EstAhorrosFragment();
            case 3:
                return new EstGraficoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numftabs;
    }
}
