package com.savegoals.savegoals.controlador.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savegoals.savegoals.AddObjetivosActivity;
import com.savegoals.savegoals.MainActivity;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class ObjetivosFragment extends Fragment implements View.OnClickListener {

    LinearLayout linearLayoutObjetivos, linearLayoutCumplidos;
    TextView tv_objetivos, tv_cumplidos;
    FloatingActionButton btn_add_objetivo;

    public ObjetivosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objetivos, container, false);

        linearLayoutObjetivos = view.findViewById(R.id.objetivos);
        linearLayoutCumplidos = view.findViewById(R.id.cumplidos);
        tv_objetivos = view.findViewById(R.id.tv_objetivos);
        tv_cumplidos = view.findViewById(R.id.tv_cumplidos);
        btn_add_objetivo = view.findViewById(R.id.btnAdd);

        btn_add_objetivo.setOnClickListener(this);
        linearLayoutObjetivos.setOnClickListener(this);
        linearLayoutCumplidos.setOnClickListener(this);

        return view;
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn_add_objetivo.getId()) {
            Intent intent = new Intent(getContext(), AddObjetivosActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        linearLayoutObjetivos.removeAllViews();
        linearLayoutCumplidos.removeAllViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();
        List<Objetivos> objetivosCompletados = appDatabase.objetivosDao().getAllCompleted();

        if (objetivosSinCompletar.size() != 0) {
            tv_objetivos.setVisibility(View.VISIBLE);
            for (int i = 0; i < objetivosSinCompletar.size(); i++) {

                int porcentajeInt = (int) ((objetivosSinCompletar.get(i).getAhorrado() * 100) / objetivosSinCompletar.get(i).getCantidad());

                LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsGeneral.setMargins(30, 50, 30, 0);
                linearLayoutGeneral.setLayoutParams(paramsGeneral);
                linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);
                linearLayoutGeneral.setId(objetivosSinCompletar.get(i).getId());

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
                paramsNombre.width = 0;
                paramsNombre.weight = 6;
                nombre.setLayoutParams(paramsNombre);
                nombre.setGravity(Gravity.CENTER);
                nombre.setTextColor(Color.BLACK);

                ImageView icono = new ImageView(getContext());
                LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsIcono.width = 0;
                paramsIcono.weight = 1;
                icono.setLayoutParams(paramsIcono);
                switch (objetivosSinCompletar.get(i).getCategoria()) {
                    case "Viaje":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                        break;

                    case "Ahorrar":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                        break;

                    case "Regalo":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                        break;

                    case "Compras":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                        break;

                    case "Clase":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                        break;

                    case "Juego":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.mando));
                        break;

                    case "Otros":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.otros));
                        break;

                }


                TextView fecha = new TextView(getContext());
                fecha.setText("Fecha: " + objetivosSinCompletar.get(i).getFecha());
                fecha.setTextColor(Color.BLACK);

                TextView cantidad = new TextView(getContext());
                cantidad.setText(obtieneDosDecimales(objetivosSinCompletar.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivosSinCompletar.get(i).getCantidad()) + "€");
                cantidad.setTextColor(Color.BLACK);

                ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsProgress.width = 0;
                paramsProgress.weight = 10;
                paramsProgress.setMargins(0, 0, 50, 0);
                progressBar.setLayoutParams(paramsProgress);
                progressBar.setMax(100);
                progressBar.setProgress(porcentajeInt);
                if (porcentajeInt < 25) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_rojo));
                } else if (porcentajeInt < 75) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_amarillo));
                } else if (porcentajeInt < 100) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_verde));
                } else {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_completado));
                }

                TextView porcentaje = new TextView(getContext());
                porcentaje.setText(porcentajeInt + "%");
                LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsPorcentaje.width = 0;
                paramsPorcentaje.weight = 2;
                porcentaje.setLayoutParams(paramsPorcentaje);
                porcentaje.setGravity(Gravity.CENTER);
                porcentaje.setTextColor(Color.BLACK);

                // Add views
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

                linearLayoutGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("id", linearLayoutGeneral.getId());
                        startActivity(intent);
                    }
                });
            }
        } else {
            tv_objetivos.setVisibility(View.GONE);
        }

        if (objetivosCompletados.size() != 0) {
            tv_cumplidos.setVisibility(View.VISIBLE);
            for (int i = 0; i < objetivosCompletados.size(); i++) {
                int porcentajeInt = (int) ((objetivosCompletados.get(i).getAhorrado() * 100) / objetivosCompletados.get(i).getCantidad());

                LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsGeneral.setMargins(30, 50, 30, 0);
                linearLayoutGeneral.setLayoutParams(paramsGeneral);
                linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);
                linearLayoutGeneral.setId(objetivosCompletados.get(i).getId());

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
                nombre.setText(objetivosCompletados.get(i).getNombre());
                nombre.setTextSize(18);
                LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsNombre.width = 0;
                paramsNombre.weight = 6;
                nombre.setLayoutParams(paramsNombre);
                nombre.setTextColor(Color.BLACK);
                nombre.setGravity(Gravity.CENTER);

                ImageView icono = new ImageView(getContext());
                LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsIcono.width = 0;
                paramsIcono.weight = 1;
                icono.setLayoutParams(paramsIcono);
                switch (objetivosCompletados.get(i).getCategoria()) {
                    case "Viaje":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                        break;

                    case "Ahorrar":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                        break;

                    case "Regalo":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                        break;

                    case "Compras":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                        break;

                    case "Clase":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                        break;

                    case "Juego":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.mando));
                        break;

                    case "Otros":
                        icono.setImageDrawable(getResources().getDrawable(R.drawable.otros));
                        break;

                }

                TextView fecha = new TextView(getContext());
                fecha.setText("Fecha: " + objetivosCompletados.get(i).getFecha());
                fecha.setTextColor(Color.BLACK);

                TextView cantidad = new TextView(getContext());
                cantidad.setText(obtieneDosDecimales(objetivosCompletados.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivosCompletados.get(i).getCantidad()) + "€");
                cantidad.setTextColor(Color.BLACK);

                ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsProgress.width = 0;
                paramsProgress.weight = 10;
                paramsProgress.setMargins(0, 0, 50, 0);
                progressBar.setLayoutParams(paramsProgress);
                progressBar.setMax(100);
                progressBar.setProgress(porcentajeInt);
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_completado));

                TextView porcentaje = new TextView(getContext());
                porcentaje.setText(porcentajeInt + "%");
                LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsPorcentaje.width = 0;
                paramsPorcentaje.weight = 2;
                porcentaje.setLayoutParams(paramsPorcentaje);
                porcentaje.setGravity(Gravity.CENTER);
                porcentaje.setTextColor(Color.BLACK);

                // Add views
                linearLayoutPeq.addView(cantidad);
                linearLayoutPeq.addView(fecha);

                linearLayoutMedio.addView(icono);
                linearLayoutMedio.addView(nombre);
                linearLayoutMedio.addView(linearLayoutPeq);

                linearLayoutGeneral.addView(linearLayoutMedio);

                linearLayoutProgressBar.addView(progressBar);
                linearLayoutProgressBar.addView(porcentaje);

                linearLayoutGeneral.addView(linearLayoutProgressBar);
                linearLayoutCumplidos.addView(linearLayoutGeneral);

                linearLayoutGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("id", linearLayoutGeneral.getId());
                        startActivity(intent);
                    }
                });
            }
        } else {
            tv_cumplidos.setVisibility(View.GONE);
        }
    }
}