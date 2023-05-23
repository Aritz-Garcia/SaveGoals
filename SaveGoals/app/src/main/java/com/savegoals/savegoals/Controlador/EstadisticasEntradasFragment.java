package com.savegoals.savegoals.Controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;

public class EstadisticasEntradasFragment extends Fragment {

    public EstadisticasEntradasFragment() {
        // Required empty public constructor
    }

    public static EstadisticasEntradasFragment newInstance(String param1, String param2) {
        EstadisticasEntradasFragment fragment = new EstadisticasEntradasFragment();
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
        return inflater.inflate(R.layout.fragment_estadisticas_entradas, container, false);
    }
}