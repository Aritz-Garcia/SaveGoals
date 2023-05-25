package com.savegoals.savegoals.Controlador.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.util.List;

public class ObjetivosFragment extends Fragment {

    LinearLayout linearLayoutObjetivos;

    public ObjetivosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objetivos, container, false);

        linearLayoutObjetivos = view.findViewById(R.id.objetivos);

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();
        List<Objetivos> objetivosCompletados = appDatabase.objetivosDao().getAllCompleted();

        if (objetivosSinCompletar != null) {
            for (int i = 0; i < objetivosSinCompletar.size(); i++) {

                LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsGeneral.setMargins(30, 50, 30, 0);
                linearLayoutGeneral.setLayoutParams(paramsGeneral);
                linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);

                LinearLayout linearLayoutMedio = new LinearLayout(getContext());
                linearLayoutMedio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayoutMedio.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout linearLayoutPeq = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.width = 0;
                params.weight = 4;
                linearLayoutPeq.setLayoutParams(params);
                linearLayoutPeq.setOrientation(LinearLayout.VERTICAL);

                LinearLayout linearLayoutProgressBar = new LinearLayout(getContext());
                LinearLayout.LayoutParams paramsProgressBar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsProgressBar.setMargins(30, 20, 30, 0);
                linearLayoutProgressBar.setLayoutParams(paramsProgressBar);
                linearLayoutProgressBar.setOrientation(LinearLayout.HORIZONTAL);

                TextView nombre = new TextView(getContext());
                nombre.setText(objetivosSinCompletar.get(i).getNombre());
                nombre.setTextSize(18);
                LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsNombre.weight = 5;
                nombre.setLayoutParams(paramsNombre);

                TextView icono = new TextView(getContext());
                icono.setText("prueba");
                icono.setTextSize(18);
                LinearLayout.LayoutParams paramsIcono1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsIcono1.weight = 1;
                icono.setLayoutParams(paramsIcono1);

                TextView fecha = new TextView(getContext());
                fecha.setText("Fecha: " + objetivosSinCompletar.get(i).getFecha());

                TextView cantidad = new TextView(getContext());
                cantidad.setText(objetivosSinCompletar.get(i).getAhorrado() + "€ / " + objetivosSinCompletar.get(i).getCantidad() + "€");

                ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsProgress.weight = 9;
                paramsProgress.setMargins(0, 0, 50, 0);
                progressBar.setLayoutParams(paramsProgress);

                TextView porcentaje = new TextView(getContext());
                porcentaje.setText("0%");
                LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsPorcentaje.weight = 1;
                porcentaje.setLayoutParams(paramsPorcentaje);


                linearLayoutPeq.addView(cantidad);
                linearLayoutPeq.addView(fecha);
                linearLayoutMedio.addView(icono);
                linearLayoutMedio.addView(nombre);
                linearLayoutMedio.addView(linearLayoutPeq);
                linearLayoutGeneral.addView(linearLayoutMedio);
                linearLayoutProgressBar.addView(progressBar);
                linearLayoutProgressBar.addView(porcentaje);
                linearLayoutGeneral.addView(linearLayoutProgressBar);
                linearLayoutObjetivos.addView(linearLayoutGeneral);
            }
        }

        return view;
    }
}