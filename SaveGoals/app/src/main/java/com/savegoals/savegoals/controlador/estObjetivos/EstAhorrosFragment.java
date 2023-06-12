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
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EstAhorrosFragment extends Fragment implements View.OnClickListener {

    LinearLayout lyEdit;
    TextView tvError, tvTitle, tvTotal;
    PieChart pieChart;
    RadioGroup rgEstAhorros;
    RadioButton rbObjetivos, rbEntradas;
    boolean edit = false;
    boolean objetivos = true;
    SharedPreferences settingssp;

    public EstAhorrosFragment() {
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
        View view = inflater.inflate(R.layout.fragment_est_ahorros, container, false);

        lyEdit = view.findViewById(R.id.lyEditEstAho);
        tvError = view.findViewById(R.id.tvErrorPieChartEstAhorros);
        tvTitle = view.findViewById(R.id.tvTitleEstAhorros);
        tvTotal = view.findViewById(R.id.tvTotalEstAhorros);
        pieChart = view.findViewById(R.id.piechartEstAhorrros);
        rgEstAhorros = view.findViewById(R.id.rgEstAhorros);
        rbObjetivos = view.findViewById(R.id.rbObjEstAho);
        rbEntradas = view.findViewById(R.id.rbEntEstAho);

        rbObjetivos.setOnClickListener(this);
        rbEntradas.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (objetivos) {
            rbObjetivos.setChecked(true);
        } else {
            rbEntradas.setChecked(true);
        }

        if (edit) {
            lyEdit.setVisibility(View.VISIBLE);
        } else {
            lyEdit.setVisibility(View.GONE);
        }

        AppDatabase db = AppDatabase.getDatabase(getContext());
        if (objetivos) {
            // Datos de Objetivos
            List<Objetivos> objetivos = db.objetivosDao().getAll();

            if (objetivos.size() == 0) {
                tvError.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.GONE);
                tvTotal.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                tvError.setText(getString(R.string.error_pieChart_est_obj));
            } else {
                int z = 0;
                int y = 0;
                for (int i = 0; i < objetivos.size(); i++) {
                    if (objetivos.get(i).getAhorrado() == 0) {
                        z++;
                    } else {
                        y++;
                    }
                }

                if (y != 0) {
                    float ahorrado = 0;
                    for (int i = 0; i < objetivos.size(); i++) {
                        ahorrado += objetivos.get(i).getAhorrado();
                    }
                    tvTitle.setText(getString(R.string.txt_est_aho));
                    tvTotal.setText(obtieneDosDecimales(ahorrado) + "€");

                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    for (int i = 0; i < objetivos.size(); i++) {
                        if (objetivos.get(i).getAhorrado() > 0) {
                            PieEntry pieEntry = new PieEntry(objetivos.get(i).getAhorrado(), objetivos.get(i).getNombre() + " ("  + obtieneDosDecimales(objetivos.get(i).getAhorrado()) + "€)");
                            pieEntries.add(pieEntry);
                        }
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
                    pieChart.animateXY(2000, 2000);
                    pieChart.getDescription().setEnabled(false);

                    tvError.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.VISIBLE);
                    tvTotal.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.VISIBLE);

                } else if (z != 0) {
                    tvError.setText(getString(R.string.error_est_aho));
                    tvError.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.GONE);
                    tvTotal.setVisibility(View.GONE);
                    pieChart.setVisibility(View.GONE);
                }
            }
        } else {
            // Datos de Entradas
            List<Entradas> entradas = db.entradasDao().getAll();

            if (entradas.size() == 0) {
                tvError.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.GONE);
                tvTotal.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                tvError.setText(getString(R.string.error_pieChartEn_est_obj));

            } else {
                float total = 0;
                // Cartera
                float en1 = 0;
                // Hucha
                float en2 = 0;
                // Trabajo
                float en3 = 0;
                // Regalo
                float en4 = 0;
                // Compras
                float en5 = 0;
                // Clase
                float en6 = 0;
                // Otros
                float en7 = 0;

                for (int i = 0; i < entradas.size(); i++) {
                    total += entradas.get(i).getCantidad();
                }
                tvTitle.setText(getString(R.string.txt_en_est_aho));
                tvTotal.setText(obtieneDosDecimales(total) + "€");

                for (int i = 0; i < entradas.size(); i++) {
                    switch (entradas.get(i).getCategoria()) {
                        case 1:
                            en1 += entradas.get(i).getCantidad();
                            break;
                        case 2:
                            en2 += entradas.get(i).getCantidad();
                            break;
                        case 3:
                            en3 += entradas.get(i).getCantidad();
                            break;
                        case 4:
                            en4 += entradas.get(i).getCantidad();
                            break;
                        case 5:
                            en5 += entradas.get(i).getCantidad();
                            break;
                        case 6:
                            en6 += entradas.get(i).getCantidad();
                            break;
                        case 7:
                            en7 += entradas.get(i).getCantidad();
                            break;
                    }
                }

                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                if (en1 > 0) {
                    PieEntry pieEntry = new PieEntry(en1, getString(R.string.categoria_cartera) + " (" + obtieneDosDecimales(en1) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (en2 > 0) {
                    PieEntry pieEntry = new PieEntry(en2, getString(R.string.categoria_hucha) + " (" + obtieneDosDecimales(en2) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (en3 > 0) {
                    PieEntry pieEntry = new PieEntry(en3, getString(R.string.categoria_trabajo) + " (" + obtieneDosDecimales(en3) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (en4 > 0) {
                    PieEntry pieEntry = new PieEntry(en4, getString(R.string.categoria_regalo) + " (" + obtieneDosDecimales(en4) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (en5 > 0) {
                    PieEntry pieEntry = new PieEntry(en5, getString(R.string.categoria_compras) + " (" + obtieneDosDecimales(en5) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (en6 > 0) {
                    PieEntry pieEntry = new PieEntry(en6, getString(R.string.categoria_clase) + " (" + obtieneDosDecimales(en6) + "€)");
                    pieEntries.add(pieEntry);
                }
                if (en7 > 0) {
                    PieEntry pieEntry = new PieEntry(en7, getString(R.string.categoria_otros) + " (" + obtieneDosDecimales(en7) + "€)");
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
                pieChart.animateXY(2000, 2000);
                pieChart.getDescription().setEnabled(false);

                tvError.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                tvTotal.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.VISIBLE);
            }
        }
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == rbObjetivos.getId()) {
            if (rgEstAhorros.getCheckedRadioButtonId() == rbObjetivos.getId()) {
                objetivos = true;
                onResume();
            }
        } else if (v.getId() == rbEntradas.getId()) {
            if (rgEstAhorros.getCheckedRadioButtonId() == rbEntradas.getId()) {
                objetivos = false;
                onResume();
            }
        }
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
}