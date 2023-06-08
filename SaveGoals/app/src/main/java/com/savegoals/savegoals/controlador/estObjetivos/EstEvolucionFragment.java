package com.savegoals.savegoals.controlador.estObjetivos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class EstEvolucionFragment extends Fragment {

    TextView tvPorcentaje, tvAhorrado, tvPendiente, tvTotal;
    ProgressBar progressBar;

    public EstEvolucionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_est_evolucion, container, false);
        tvPorcentaje = view.findViewById(R.id.tvPorcentajeEst);
        tvAhorrado = view.findViewById(R.id.tvAhorradoEst);
        tvPendiente = view.findViewById(R.id.tvPendienteEst);
        tvTotal = view.findViewById(R.id.tvTotalEst);
        progressBar = view.findViewById(R.id.progressBarEst);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        progressBar.setProgress(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        List<Objetivos> objetivos = appDatabase.objetivosDao().getAll();
        float total = 0;
        float ahorrado = 0;
        float pendiente = 0;
        int porcentaje = 0;

        if (objetivos.size() != 0) {
            for (int i = 0; i < objetivos.size(); i++) {
                total = total + objetivos.get(i).getCantidad();
                ahorrado = ahorrado + objetivos.get(i).getAhorrado();
            }

            pendiente = total - ahorrado;

            if (ahorrado == 0) {
                porcentaje = 1;
            } else {
                porcentaje = (int) ((ahorrado * 100) / total);
            }

            if (porcentaje < 50) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_rojo));
            } else if (porcentaje < 75) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_amarillo));
            } else if (porcentaje < 100) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_verde));
            } else {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_completado));
            }
            progressBar.setProgress(porcentaje);

            tvPorcentaje.setText(porcentaje + "%");

            if (ahorrado == 0) {
                porcentaje = 0;
                progressBar.setProgress(porcentaje);
                tvPorcentaje.setText(porcentaje + "%");
            }

            tvAhorrado.setText(obtieneDosDecimales(ahorrado) + "€");
            tvPendiente.setText(obtieneDosDecimales(pendiente) + "€");
            tvTotal.setText(obtieneDosDecimales(total) + "€");

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        List<Objetivos> objetivos = appDatabase.objetivosDao().getAll();
        float total = 0;
        float ahorrado = 0;
        float pendiente = 0;
        int porcentaje = 0;

        if (objetivos.size() != 0) {
            for (int i = 0; i < objetivos.size(); i++) {
                total = total + objetivos.get(i).getCantidad();
                ahorrado = ahorrado + objetivos.get(i).getAhorrado();
            }

            pendiente = total - ahorrado;

            if (ahorrado == 0) {
                porcentaje = 1;
            } else {
                porcentaje = (int) ((ahorrado * 100) / total);
            }

            if (porcentaje < 50) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_rojo));
            } else if (porcentaje < 75) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_amarillo));
            } else if (porcentaje < 100) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_verde));
            } else {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_completado));
            }
            progressBar.setProgress(porcentaje);

            tvPorcentaje.setText(porcentaje + "%");

            if (ahorrado == 0) {
                porcentaje = 0;
                progressBar.setProgress(porcentaje);
                tvPorcentaje.setText(porcentaje + "%");
            }

            tvAhorrado.setText(obtieneDosDecimales(ahorrado) + "€");
            tvPendiente.setText(obtieneDosDecimales(pendiente) + "€");
            tvTotal.setText(obtieneDosDecimales(total) + "€");

        }

    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

}