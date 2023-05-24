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
    TextView porcentaje, tvAhorrado, tvPendiente, tvTotal, tvDia, tvSemana, tvMes;

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
        tvAhorrado = view.findViewById(R.id.tvAhorrado);
        tvPendiente = view.findViewById(R.id.tvPendiente);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvDia = view.findViewById(R.id.tvDia);
        tvSemana = view.findViewById(R.id.tvSemana);
        tvMes = view.findViewById(R.id.tvMes);


        return view;
    }
}