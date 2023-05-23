package com.savegoals.savegoals.Controlador;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerController extends FragmentPagerAdapter {

    int numftabs;

    public PagerController(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numftabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new EstadisticasResumenFragment();
            case 1:
                return new EstadisticasEstadisticasFragment();
            case 2:
                return new EstadisticasEntradasFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numftabs;
    }
}
