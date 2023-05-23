package com.savegoals.savegoals.Controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.savegoals.savegoals.R;

import java.util.ArrayList;

public class EstadisticasEstadisticasFragment extends Fragment {

    PieChart pieChart;

    public EstadisticasEstadisticasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas_estadisticas, container, false);

        pieChart = view.findViewById(R.id.piechart);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        PieEntry pieEntry = new PieEntry(1, "Enero");
        PieEntry pieEntry2 = new PieEntry(2, "Febrero");
        PieEntry pieEntry3 = new PieEntry(50, "Prueba");
        PieEntry pieEntry4 = new PieEntry(10, "Abril");

        pieEntries.add(pieEntry);
        pieEntries.add(pieEntry2);
        pieEntries.add(pieEntry3);
        pieEntries.add(pieEntry4);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(false);
        pieChart.setCenterText("Resumen");
        pieChart.animateXY(3000, 3000);
        pieChart.getDescription().setEnabled(false);

        return view;
    }
}