package com.savegoals.savegoals.Controlador.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerControllerMenu extends FragmentPagerAdapter {

    int numftabs;

    public PagerControllerMenu(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numftabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ObjetivosFragment();
            case 1:
                return new EstadisticasFragment();
            case 2:
                return new ConfiguracionFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numftabs;
    }
}
