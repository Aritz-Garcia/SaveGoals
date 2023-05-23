package com.savegoals.savegoals.Controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;

public class EstadisticasResumenFragment extends Fragment {

    ProgressBar progressBar;
    TextView porcentaje;

    public EstadisticasResumenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas_resumen, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        porcentaje = view.findViewById(R.id.porcentaje);


        return view;
    }
}