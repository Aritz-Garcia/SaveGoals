package com.savegoals.savegoals.controlador.estadisticas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.datos.ColoresHighLight;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.db.AppDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EstadisticasGraficoFragment extends Fragment {

    TextView tvTitle, tvError;
    LineChart lineChart;
    int id, numeroRandom;
    SharedPreferences settingssp;
    public EstadisticasGraficoFragment(int id) {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_estadisticas_grafico, container, false);

        tvTitle = view.findViewById(R.id.tvTitleGrafico);
        tvError = view.findViewById(R.id.tvErrorLineChartGrafico);
        lineChart = view.findViewById(R.id.linechartGrafico);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();

        AppDatabase db = AppDatabase.getDatabase(getContext());

        List<Entradas> entradas = db.entradasDao().findByIdObj(id);

        if (entradas.isEmpty()) {
            // No hay entradas
            tvError.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            lineChart.setVisibility(View.GONE);
        } else {
            // Hay entradas
            tvError.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.VISIBLE);

            List<Entry> entries = new ArrayList<>();
            Collections.sort(entradas, new Comparator<Entradas>() {
                @Override
                public int compare(Entradas e1, Entradas e2) {
                    return e1.getFechaAsDate().compareTo(e2.getFechaAsDate());
                }
            });

            LocalDate fecha1 = LocalDate.now();
            LocalDate fecha2 = fecha1.minusDays(15);
            LocalDate fecha3 = fecha2;

            int diffDays = 16;

            ArrayList<String> fechas = new ArrayList<>();
            for (int i = 0; i < diffDays; i++) {
                String fecha3String = fecha3.toString();
                String[] fecha3Array = fecha3String.split("-");
                String[] anoArray = fecha3Array[0].split("");
                String anoString = anoArray[2] + anoArray[3];
                fechas.add(fecha3Array[2] + "/" + fecha3Array[1] + "/" + anoString);
                fecha3 = fecha3.plusDays(1);
            }

            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(fechas));

            float total = 0;
            int dentro = 0;
            for (int i = 0; i < fechas.size(); i++) {
                for (int j = 0; j < entradas.size(); j++) {
                    String[] fecha = entradas.get(j).getFecha().split("/");
                    String[] anoArray = fecha[2].split("");
                    String anoString = anoArray[2] + anoArray[3];
                    if (fechas.get(i).equals(fecha[0] + "/" + fecha[1] + "/" + anoString)) {
                        dentro++;
                        if (dentro == 1 && 0 != j) {
                            for (int z = 0; z < j; z++) {
                                total += entradas.get(z).getCantidad();
                            }
                            for (int z = 0; z < i; z++) {
                                entries.set(z, new Entry(z, total));
                            }
                        }
                        total += entradas.get(j).getCantidad();
                    }
                }
                entries.add(new Entry(i, total));
            }

            if (total == 0) {
                for (int i = 0; i < entradas.size(); i++) {
                    String[] fecha = entradas.get(i).getFecha().split("/");
                    LocalDate fechaEntrada = LocalDate.of(Integer.parseInt(fecha[2]), Integer.parseInt(fecha[1]), Integer.parseInt(fecha[0]));
                    if (fechaEntrada.isBefore(fecha1)) {
                        total += entradas.get(i).getCantidad();
                    }
                }
                for (int i = 0; i < fechas.size(); i++) {
                    entries.set(i, new Entry(i, total));
                }
            }

            LineDataSet lineDataSet = new LineDataSet(entries, db.objetivosDao().findById(id).getNombre());
            numeroRandom = (int) (Math.random() * 5);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillColor(ColorTemplate.VORDIPLOM_COLORS[numeroRandom]);
            lineDataSet.setColor(ColoresHighLight.COLORESGET_VORDIPLOM_COLORS[numeroRandom]);
            lineDataSet.setCircleColor(Color.DKGRAY);
            lineDataSet.setHighLightColor(ColoresHighLight.COLORESGET_VORDIPLOM_COLORS[numeroRandom]);
            lineDataSet.setValueTextSize(12);
            if (settingssp.getBoolean("oscuro", false)) {
                lineDataSet.setValueTextColor(Color.WHITE);
                lineChart.getXAxis().setTextColor(Color.WHITE);
                lineChart.getAxisLeft().setTextColor(Color.WHITE);
                lineChart.getAxisRight().setTextColor(Color.WHITE);
                lineChart.getLegend().setTextColor(Color.WHITE);
            } else {
                lineDataSet.setValueTextColor(Color.BLACK);
                lineChart.getXAxis().setTextColor(Color.BLACK);
                lineChart.getAxisLeft().setTextColor(Color.BLACK);
                lineChart.getAxisRight().setTextColor(Color.BLACK);
                lineChart.getLegend().setTextColor(Color.BLACK);
            }
            lineChart.setData(new LineData(lineDataSet));
            lineChart.animateXY(2000, 2000);
            lineChart.getDescription().setEnabled(false);
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
}