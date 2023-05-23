package com.savegoals.savegoals.Controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;

public class EstadisticasEstadisticasFragment extends Fragment {

    public EstadisticasEstadisticasFragment() {
        // Required empty public constructor
    }

    public static EstadisticasEstadisticasFragment newInstance() {
        EstadisticasEstadisticasFragment fragment = new EstadisticasEstadisticasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estadisticas_estadisticas, container, false);
    }
}