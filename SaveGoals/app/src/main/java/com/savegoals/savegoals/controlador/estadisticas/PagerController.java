package com.savegoals.savegoals.controlador.estadisticas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerController extends FragmentPagerAdapter {

    int numftabs;
    int id;

    public PagerController(@NonNull FragmentManager fm, int behavior, int id) {
        super(fm, behavior);
        this.numftabs = behavior;
        this.id = id;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new EstadisticasResumenFragment(id);
            case 1:
                return new EstadisticasEstadisticasFragment(id);
            case 2:
                return new EstadisticasEntradasFragment(id);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numftabs;
    }
}
