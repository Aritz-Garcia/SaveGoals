package com.savegoals.savegoals.controlador.estObjetivos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
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
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstGraficoFragment extends Fragment {

    int numeroRandom;
    LineChart lineChart;
    SharedPreferences settingssp;

    public EstGraficoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_est_grafico, container, false);

        lineChart = view.findViewById(R.id.linechartEstGrafico);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();

        AppDatabase db = AppDatabase.getDatabase(getContext());

        List<Objetivos> objetivos = db.objetivosDao().getAll();

        if (objetivos.isEmpty()) {
            // No hay objetivos
            lineChart.setVisibility(View.GONE);
        } else {
            // Hay objetivos
            lineChart.setVisibility(View.VISIBLE);

            List<Entry> entries = new ArrayList<Entry>();
            List<Entradas> entradasAll = db.entradasDao().getAll();
            // ordenar entradas por fechas
            entradasAll.sort((o1, o2) -> o1.getFecha().compareTo(o2.getFecha()));
            String primeraFecha = entradasAll.get(0).getFecha();
            String ultimaFecha = entradasAll.get(entradasAll.size() - 1).getFecha();

            // Poner fecha de primero al ultimo aunque no tenga entrada
            String[] pfecha = primeraFecha.split("/");
            String[] ufecha = ultimaFecha.split("/");
            primeraFecha = pfecha[2] + "-" + pfecha[1] + "-" + pfecha[0];
            ultimaFecha = ufecha[2] + "-" + ufecha[1] + "-" + ufecha[0];

            LocalDate fecha1 = LocalDate.parse(primeraFecha);
            LocalDate fecha2 = LocalDate.parse(ultimaFecha);
            LocalDate fecha3 = fecha1;

            // Cuantos DIAS hay entra fecha1 y fecha2
            int diffDays = fecha1.until(fecha2).getDays();
            diffDays++;

            ArrayList<String> fechas = new ArrayList<String>();
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
            for (int i = 0; i < fechas.size(); i++) {
                for (int j = 0; j < entradasAll.size(); j++) {
                    String[] fecha = entradasAll.get(j).getFecha().split("/");
                    String[] anoArray = fecha[2].split("");
                    String anoString = anoArray[2] + anoArray[3];
                    if (fechas.get(i).equals(fecha[0] + "/" + fecha[1] + "/" + anoString)) {
                        total += entradasAll.get(j).getCantidad();
                    }
                }
                entries.add(new Entry(i, total));
            }

            LineDataSet lineDataSet = new LineDataSet(entries, "Total Ahorrado");
            numeroRandom = (int) (Math.random() * 5);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillColor(ColorTemplate.VORDIPLOM_COLORS[numeroRandom]);
            lineDataSet.setColor(ColoresHighLight.coloresGet_VORDIPLOM_COLORS().get(numeroRandom));
            lineDataSet.setCircleColor(Color.DKGRAY);
            lineDataSet.setHighLightColor(ColoresHighLight.coloresGet_VORDIPLOM_COLORS().get(numeroRandom));
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
}