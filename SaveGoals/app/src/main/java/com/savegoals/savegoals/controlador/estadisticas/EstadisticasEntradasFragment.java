package com.savegoals.savegoals.controlador.estadisticas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savegoals.savegoals.AddEntradasActivity;
import com.savegoals.savegoals.EditEntradasActivity;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EstadisticasEntradasFragment extends Fragment implements View.OnClickListener {

    int id;
    LinearLayout lyEntradas;
    FloatingActionButton btnAddEntrada;
    SharedPreferences settingssp;

    public EstadisticasEntradasFragment(int id) {
        // Required empty public constructor
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas_entradas, container, false);

        lyEntradas = view.findViewById(R.id.lyEntradas);
        btnAddEntrada = view.findViewById(R.id.btnAddEntrada);

        btnAddEntrada.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        lyEntradas.removeAllViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        Objetivos objetivo = appDatabase.objetivosDao().findById(id);
        List<Entradas> entradas = appDatabase.entradasDao().findByIdObj(id);

        Collections.sort(entradas, new Comparator<Entradas>() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(Entradas entrada1, Entradas entrada2) {
                try {
                    return dateFormat.parse(entrada1.getFecha()).compareTo(dateFormat.parse(entrada2.getFecha()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        for (int i = 0; i < entradas.size(); i++) {

            LinearLayout lyGeneral = new LinearLayout(getContext());
            LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsGeneral.setMargins(20, 20, 20, 20);
            lyGeneral.setLayoutParams(paramsGeneral);
            lyGeneral.setOrientation(LinearLayout.HORIZONTAL);
            lyGeneral.setId(entradas.get(i).getIdEntrada());

            ImageView icono = new ImageView(getContext());
            LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsIcono.width = 0;
            paramsIcono.weight = 1;
            icono.setLayoutParams(paramsIcono);
            switch (entradas.get(i).getCategoria()) {
                case "Cartera":
                    icono.setImageDrawable(getResources().getDrawable(R.drawable.cartera));
                    break;

                case "Hucha":
                    icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                    break;

                case "Trabajo":
                    icono.setImageDrawable(getResources().getDrawable(R.drawable.martillo));
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

                case "Otros":
                    icono.setImageDrawable(getResources().getDrawable(R.drawable.otros));
                    break;

            }
            if (settingssp.getBoolean("oscuro", false)) {
                icono.setColorFilter(Color.WHITE);
            } else {
                icono.setColorFilter(Color.BLACK);
            }

            TextView fecha = new TextView(getContext());
            LinearLayout.LayoutParams paramsfecha = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsfecha.width = 0;
            paramsfecha.weight = 4;
            fecha.setLayoutParams(paramsfecha);
            fecha.setText(entradas.get(i).getFecha());
            fecha.setGravity(Gravity.CENTER);

            TextView nombre = new TextView(getContext());
            LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsNombre.width = 0;
            paramsNombre.weight = 5;
            nombre.setLayoutParams(paramsNombre);
            nombre.setText(entradas.get(i).getNombre());
            nombre.setGravity(Gravity.CENTER);

            TextView cantidad = new TextView(getContext());
            LinearLayout.LayoutParams paramsCantidad = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsCantidad.width = 0;
            paramsCantidad.weight = 3;
            cantidad.setLayoutParams(paramsCantidad);
            cantidad.setText(obtieneDosDecimales(entradas.get(i).getCantidad()) + "€");
            cantidad.setGravity(Gravity.CENTER);

            lyGeneral.addView(icono);
            lyGeneral.addView(fecha);
            lyGeneral.addView(nombre);
            lyGeneral.addView(cantidad);

            lyEntradas.addView(lyGeneral);

            lyGeneral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EditEntradasActivity.class);
                    intent.putExtra("idO", id);
                    intent.putExtra("idE", lyGeneral.getId());
                    startActivity(intent);
                }
            });


            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaMetida = null;
            Date fechaFin = null;

            try {
                fechaMetida = formato.parse(entradas.get(i).getFecha());
                fechaFin = formato.parse(objetivo.getFecha());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!getFueraDias(fechaMetida, fechaFin)) {
                nombre.setTextColor(Color.RED);
                fecha.setTextColor(Color.RED);
                cantidad.setTextColor(Color.RED);
            }

        }

        if (objetivo.getCompletado()) {
            btnAddEntrada.setVisibility(View.GONE);
        } else {
            btnAddEntrada.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAddEntrada.getId()) {
            Intent intent = new Intent(getContext(), AddEntradasActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("restar", false);
            startActivity(intent);
        }
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    private boolean getFueraDias(Date fechaMetida, Date fechaFin) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate fechaMetidaLocal = fechaMetida.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (fechaMetidaLocal.getYear() == fechaFinLocal.getYear() && fechaMetidaLocal.getMonth() == fechaFinLocal.getMonth() && fechaMetidaLocal.getDayOfMonth() == fechaFinLocal.getDayOfMonth()) {
                // Mismo dia
                return true;
            } else if (fechaMetidaLocal.isAfter(fechaFinLocal)) {
                // Despues
                return false;
            } else {
                // antes
                return true;
            }

        }
        return true;
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}