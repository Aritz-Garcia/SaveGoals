package com.savegoals.savegoals.controlador.estadisticas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savegoals.savegoals.AddEntradasActivity;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class EstadisticasResumenFragment extends Fragment implements View.OnClickListener {

    ProgressBar progressBar;
    TextView tvPorcentaje, tvAhorradotxt, tvPendientetxt, tvTotaltxt, tvAhorrado, tvPendiente, tvTotal, tvText,
            tvDiatxt, tvSemanatxt, tvMestxt, tvDia, tvSemana, tvMes, tvFecha, tvCategoria;
    int id;
    FloatingActionButton btnAñadirEntrada;
    LinearLayout lyFecha, lyCategoria;
    ImageView ivCategoria;
    SharedPreferences settingssp;


    public EstadisticasResumenFragment(int id) {
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
        View view = inflater.inflate(R.layout.fragment_estadisticas_resumen, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        tvPorcentaje = view.findViewById(R.id.tvPorcentaje);

        tvAhorradotxt = view.findViewById(R.id.tvAhorradotxt);
        tvPendientetxt = view.findViewById(R.id.tvPendientetxt);
        tvTotaltxt = view.findViewById(R.id.tvTotaltxt);

        tvAhorrado = view.findViewById(R.id.tvAhorrado);
        tvPendiente = view.findViewById(R.id.tvPendiente);
        tvTotal = view.findViewById(R.id.tvTotal);

        tvText = view.findViewById(R.id.tvText);

        tvDiatxt = view.findViewById(R.id.tvDiatxt);
        tvSemanatxt = view.findViewById(R.id.tvSemanatxt);
        tvMestxt = view.findViewById(R.id.tvMestxt);

        tvDia = view.findViewById(R.id.tvDia);
        tvSemana = view.findViewById(R.id.tvSemana);
        tvMes = view.findViewById(R.id.tvMes);

        lyFecha = view.findViewById(R.id.lyFecha);
        lyCategoria = view.findViewById(R.id.lyCategoria);
        tvFecha = view.findViewById(R.id.tvFecha);
        tvCategoria = view.findViewById(R.id.tvCategoria);
        ivCategoria = view.findViewById(R.id.ivCategoria);

        btnAñadirEntrada = view.findViewById(R.id.btnAñadirEntardaResumen);
        btnAñadirEntrada.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        progressBar.setProgress(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        Objetivos objetivo = appDatabase.objetivosDao().findById(id);

        if (objetivo != null) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.width = 0;
            params.weight = 1;

            tvAhorradotxt.setLayoutParams(params);
            tvPendientetxt.setLayoutParams(params);
            tvTotaltxt.setLayoutParams(params);

            tvAhorrado.setLayoutParams(params);
            tvPendiente.setLayoutParams(params);
            tvTotal.setLayoutParams(params);

            tvDiatxt.setLayoutParams(params);
            tvSemanatxt.setLayoutParams(params);
            tvMestxt.setLayoutParams(params);

            tvDia.setLayoutParams(params);
            tvSemana.setLayoutParams(params);
            tvMes.setLayoutParams(params);

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Date today = new Date();
            today.setHours(0);
            today.setMinutes(0);
            today.setSeconds(0);
            Date fechaFin = null;
            try {
                fechaFin = formato.parse(objetivo.getFecha());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            float pendiente = objetivo.getCantidad() - objetivo.getAhorrado();

            if (leerFecha(today, fechaFin)) {
                if (!objetivo.getCompletado()) {
                    int dias = getDias(today, fechaFin);
                    int semanas = getSemanas(today, fechaFin);
                    int meses = getMeses(today, fechaFin);

                    float dia = pendiente / dias;
                    float semana = pendiente / semanas;
                    float mes = pendiente / meses;

                    tvDia.setText(obtieneDosDecimales(dia) + "€");
                    tvSemana.setText(obtieneDosDecimales(semana) + "€");
                    tvMes.setText(obtieneDosDecimales(mes) + "€");

                    tvText.setVisibility(View.VISIBLE);
                    tvDiatxt.setVisibility(View.VISIBLE);
                    tvSemanatxt.setVisibility(View.VISIBLE);
                    tvMestxt.setVisibility(View.VISIBLE);
                    tvDia.setVisibility(View.VISIBLE);
                    tvSemana.setVisibility(View.VISIBLE);
                    tvMes.setVisibility(View.VISIBLE);
                } else {
                    tvText.setVisibility(View.GONE);
                    tvDiatxt.setVisibility(View.GONE);
                    tvSemanatxt.setVisibility(View.GONE);
                    tvMestxt.setVisibility(View.GONE);
                    tvDia.setVisibility(View.GONE);
                    tvSemana.setVisibility(View.GONE);
                    tvMes.setVisibility(View.GONE);
                }
            } else {
                tvText.setVisibility(View.GONE);
                tvDiatxt.setVisibility(View.GONE);
                tvSemanatxt.setVisibility(View.GONE);
                tvMestxt.setVisibility(View.GONE);
                tvDia.setVisibility(View.GONE);
                tvSemana.setVisibility(View.GONE);
                tvMes.setVisibility(View.GONE);
            }

            int porcentajeCalc;

            if (objetivo.getAhorrado() == 0) {
                porcentajeCalc = 1;
            } else {
                porcentajeCalc = (int) ((objetivo.getAhorrado() * 100) / objetivo.getCantidad());
            }

            if (porcentajeCalc < 50) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_rojo));
            } else if (porcentajeCalc < 75) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_amarillo));
            } else if (porcentajeCalc < 100) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_verde));
            } else {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_completado));
            }
            progressBar.setProgress(porcentajeCalc);

            tvPorcentaje.setText(porcentajeCalc + "%");

            if (objetivo.getAhorrado() == 0) {
                porcentajeCalc = 0;
                progressBar.setProgress(porcentajeCalc);
                tvPorcentaje.setText(porcentajeCalc + "%");
            }

            tvAhorrado.setText(obtieneDosDecimales(objetivo.getAhorrado()) + "€");
            tvPendiente.setText(obtieneDosDecimales(pendiente) + "€");
            tvTotal.setText(obtieneDosDecimales(objetivo.getCantidad()) + "€");

            if (objetivo.getCompletado()) {
                tvPendientetxt.setVisibility(View.GONE);
                tvPendiente.setVisibility(View.GONE);
                btnAñadirEntrada.setVisibility(View.GONE);
            }

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params2.width = 0;
            params2.weight = 1;
            lyFecha.setLayoutParams(params2);
            lyCategoria.setLayoutParams(params2);
            lyFecha.setGravity(Gravity.CENTER);
            lyCategoria.setGravity(Gravity.CENTER);

            tvFecha.setText(objetivo.getFecha());
            tvCategoria.setText(getCategoria(objetivo.getCategoria()));
            switch (objetivo.getCategoria()) {
                case 1:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                    break;

                case 2:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                    break;

                case 3:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                    break;

                case 4:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                    break;

                case 5:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                    break;

                case 6:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.mando));
                    break;

                case 7:
                    ivCategoria.setImageDrawable(getResources().getDrawable(R.drawable.otros));
                    break;

            }


            if (!leerFecha(today, fechaFin)) {
                tvFecha.setTextColor(Color.RED);
            }

        }
    }

    private int getDias(Date hoy, Date fechaFin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate hoyLocal = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return (int) ChronoUnit.DAYS.between(hoyLocal, fechaFinLocal);
        }
        return 1;
    }

    private int getSemanas(Date hoy, Date fechaFin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate hoyLocal = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long dias = ChronoUnit.DAYS.between(hoyLocal, fechaFinLocal);
            if (dias > 7) {
                return (int) (dias / 7);
            }
        }
        return 1;
    }

    private int getMeses(Date hoy, Date fechaFin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate hoyLocal = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long dias = ChronoUnit.DAYS.between(hoyLocal, fechaFinLocal);
            if (dias > 30) {
                return (int) (dias / 30);
            }
        }
        return 1;
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    private boolean leerFecha(Date today, Date fechaFin){

        if (fechaFin.getDay() == today.getDay() && fechaFin.getMonth() == today.getMonth() && fechaFin.getYear() == today.getYear()) {
            return true;
        } else if (fechaFin.before(today)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAñadirEntrada.getId()) {
            Intent intent = new Intent(getContext(), AddEntradasActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("restar", false);
            startActivity(intent);
        }
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private String getCategoria(int posicion) {
        switch (posicion) {
            case 1:
                return getString(R.string.categoria_viaje);
            case 2:
                return getString(R.string.categoria_ahorrar);
            case 3:
                return getString(R.string.categoria_regalo);
            case 4:
                return getString(R.string.categoria_compras);
            case 5:
                return getString(R.string.categoria_clase);
            case 6:
                return getString(R.string.categoria_juego);
            case 7:
                return getString(R.string.categoria_otros);
            default:
                return getString(R.string.categoria_seleccionar);
        }
    }
}