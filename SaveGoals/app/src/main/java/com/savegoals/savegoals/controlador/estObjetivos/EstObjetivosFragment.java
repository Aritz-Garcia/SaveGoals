package com.savegoals.savegoals.controlador.estObjetivos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class EstObjetivosFragment extends Fragment implements View.OnClickListener {

    LinearLayout lyEdit;
    TextView tvError, tvTitle, tvTotal;
    PieChart pieChart;
    RadioGroup rgObjetivos;
    RadioButton rbNombre, rbCategoria;
    boolean edit = false;
    boolean nombre = true;
    SharedPreferences settingssp;

    public EstObjetivosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_est_objetivos, container, false);

        lyEdit = view.findViewById(R.id.lyEditEstObj);
        pieChart = view.findViewById(R.id.piechartEstObjetivos);
        tvError = view.findViewById(R.id.tvErrorPieChartEstObjetivos);
        tvTitle = view.findViewById(R.id.tvTitleEstObjetivos);
        tvTotal = view.findViewById(R.id.tvTotalEstObjetivos);
        rgObjetivos = view.findViewById(R.id.rgEstObjetivos);
        rbNombre = view.findViewById(R.id.rbNomEstObj);
        rbCategoria = view.findViewById(R.id.rbCatEstObj);

        rbNombre.setOnClickListener(this);
        rbCategoria.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (nombre) {
            rbNombre.setChecked(true);
        } else {
            rbCategoria.setChecked(true);
        }

        if (edit) {
            lyEdit.setVisibility(View.VISIBLE);
        } else {
            lyEdit.setVisibility(View.GONE);
        }


        AppDatabase db = AppDatabase.getDatabase(getContext());

        if (nombre) {
            // Datos por nombre
            List<Objetivos> objetivos = db.objetivosDao().getAllSinArchivados();
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
                if (settingssp.getBoolean("oscuro", false)) {
                    pieChart.setEntryLabelColor(Color.WHITE);
                    pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
                    pieChart.getLegend().setTextColor(Color.WHITE);
                } else {
                    pieChart.setEntryLabelColor(Color.BLACK);
                    pieChart.getLegend().setTextColor(Color.BLACK);
                }
                pieChart.setData(new PieData(pieDataSet));
                pieChart.setDrawEntryLabels(true);
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(false);
                pieChart.animateXY(2000, 2000);
                pieChart.getDescription().setEnabled(false);
            }
        } else {
            // Datos por categoría
            List<Objetivos> objetivos = db.objetivosDao().getAllSinArchivados();
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

                // Viaje
                float oc1 = 0;
                // Ahorrar
                float oc2 = 0;
                // Regalo
                float oc3 = 0;
                // Compras
                float oc4 = 0;
                // Clase
                float oc5 = 0;
                // Juego
                float oc6 = 0;
                // Otros
                float oc7 = 0;

                for (int i = 0; i < objetivos.size(); i++) {
                    switch (objetivos.get(i).getCategoria()) {
                        case 1:
                            oc1 += objetivos.get(i).getCantidad();
                            break;
                        case 2:
                            oc2 += objetivos.get(i).getCantidad();
                            break;
                        case 3:
                            oc3 += objetivos.get(i).getCantidad();
                            break;
                        case 4:
                            oc4 += objetivos.get(i).getCantidad();
                            break;
                        case 5:
                            oc5 += objetivos.get(i).getCantidad();
                            break;
                        case 6:
                            oc6 += objetivos.get(i).getCantidad();
                            break;
                        case 7:
                            oc7 += objetivos.get(i).getCantidad();
                            break;
                    }
                }

                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                if (oc1 > 0) {
                    PieEntry pieEntry = new PieEntry(oc1, getString(R.string.categoria_viaje) + " (" + obtieneDosDecimales(oc1) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (oc2 > 0) {
                    PieEntry pieEntry = new PieEntry(oc2, getString(R.string.categoria_ahorrar) + " (" + obtieneDosDecimales(oc2) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (oc3 > 0) {
                    PieEntry pieEntry = new PieEntry(oc3, getString(R.string.categoria_regalo) + " (" + obtieneDosDecimales(oc3) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (oc4 > 0) {
                    PieEntry pieEntry = new PieEntry(oc4, getString(R.string.categoria_compras) + " (" + obtieneDosDecimales(oc4) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (oc5 > 0) {
                    PieEntry pieEntry = new PieEntry(oc5, getString(R.string.categoria_clase) + " (" + obtieneDosDecimales(oc5) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (oc6 > 0) {
                    PieEntry pieEntry = new PieEntry(oc6, getString(R.string.categoria_juego) + " (" + obtieneDosDecimales(oc6) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (oc7 > 0) {
                    PieEntry pieEntry = new PieEntry(oc7, getString(R.string.categoria_otros) + " (" + obtieneDosDecimales(oc7) + "€)");
                    pieEntries.add(pieEntry);
                }
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet.setSliceSpace(2);
                pieDataSet.setValueTextSize(12);
                if (settingssp.getBoolean("oscuro", false)) {
                    pieChart.setEntryLabelColor(Color.WHITE);
                    pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
                    pieChart.getLegend().setTextColor(Color.WHITE);
                } else {
                    pieChart.setEntryLabelColor(Color.BLACK);
                    pieChart.getLegend().setTextColor(Color.BLACK);
                }
                pieChart.setData(new PieData(pieDataSet));
                pieChart.setDrawEntryLabels(true);
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(false);
                pieChart.animateXY(2000, 2000);
                pieChart.getDescription().setEnabled(false);
            }
        }

    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.editChartBtnEdit) {
            edit = !edit;
            if (edit) {
                lyEdit.setVisibility(View.VISIBLE);
            } else {
                lyEdit.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == rbNombre.getId()) {
            if (rgObjetivos.getCheckedRadioButtonId() == rbNombre.getId()) {
                nombre = true;
                onResume();
            }

        } else if (v.getId() == rbCategoria.getId()) {
            if (rgObjetivos.getCheckedRadioButtonId() == rbCategoria.getId()) {
                nombre = false;
                onResume();
            }
        }
    }
}