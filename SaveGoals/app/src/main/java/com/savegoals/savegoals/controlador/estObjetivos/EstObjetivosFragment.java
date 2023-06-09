package com.savegoals.savegoals.controlador.estObjetivos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EstObjetivosFragment extends Fragment {

    TextView tvError, tvTitle, tvTotal;
    PieChart pieChart;
    SharedPreferences settingssp;

    public EstObjetivosFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_est_objetivos, container, false);

        pieChart = view.findViewById(R.id.piechartEstObjetivos);
        tvError = view.findViewById(R.id.tvErrorPieChartEstObjetivos);
        tvTitle = view.findViewById(R.id.tvTitleEstObjetivos);
        tvTotal = view.findViewById(R.id.tvTotalEstObjetivos);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase db = AppDatabase.getDatabase(getContext());

        List<Objetivos> objetivos = db.objetivosDao().getAll();
        if (objetivos.size() == 0) {
            pieChart.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTotal.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);

            float total = 0;
            for (int i = 0; i < objetivos.size(); i++) {
                total += objetivos.get(i).getCantidad();
            }

            tvTotal.setText(obtieneDosDecimales(total) + "€");

            ArrayList<PieEntry> pieEntries = new ArrayList<>();

            for (int i = 0; i < objetivos.size(); i++) {
                PieEntry pieEntry = new PieEntry(objetivos.get(i).getCantidad(), objetivos.get(i).getNombre() + " ("  + obtieneDosDecimales(objetivos.get(i).getCantidad()) + "€)");
                pieEntries.add(pieEntry);
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);
            pieChart.setData(new PieData(pieDataSet));
            pieChart.setDrawEntryLabels(true);
            pieChart.setUsePercentValues(true);
            pieChart.setDrawHoleEnabled(false);
            if (settingssp.getBoolean("oscuro", false)) {
                pieChart.setEntryLabelColor(Color.WHITE);
                pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
            } else {
                pieChart.setEntryLabelColor(Color.BLACK);
            }
            pieChart.animateXY(3000, 3000);
            pieChart.getDescription().setEnabled(false);
        }
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

}