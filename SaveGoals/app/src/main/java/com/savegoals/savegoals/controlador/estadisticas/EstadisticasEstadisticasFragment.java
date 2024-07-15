package com.savegoals.savegoals.controlador.estadisticas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EstadisticasEstadisticasFragment extends Fragment {

    TextView tvUltimasEntradas, tvErrorEntradas;
    LinearLayout lyUltimasEntradas;
    PieChart pieChart;
    SharedPreferences settingssp;
    int id;

    public EstadisticasEstadisticasFragment(int id) {
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
        View view = inflater.inflate(R.layout.fragment_estadisticas_estadisticas, container, false);

        pieChart = view.findViewById(R.id.piechart);
        tvUltimasEntradas = view.findViewById(R.id.tvUltimasEntradas);
        tvErrorEntradas = view.findViewById(R.id.tvErrorEntradas);
        lyUltimasEntradas = view.findViewById(R.id.lyUltimasEntradas);


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        lyUltimasEntradas.removeAllViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        Objetivos objetivo = appDatabase.objetivosDao().findById(id);
        List<Entradas> entradas = appDatabase.entradasDao().findByIdObj(id);
        int z = 0;

        if (entradas.size() == 0) {
            pieChart.setVisibility(View.GONE);
            tvUltimasEntradas.setVisibility(View.GONE);
            tvErrorEntradas.setVisibility(View.VISIBLE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            tvUltimasEntradas.setVisibility(View.VISIBLE);
            tvErrorEntradas.setVisibility(View.GONE);

            Collections.sort(entradas, new Comparator<Entradas>() {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                @Override
                public int compare(Entradas entrada1, Entradas entrada2) {
                    try {
                        return dateFormat.parse(entrada2.getFecha()).compareTo(dateFormat.parse(entrada1.getFecha()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            ArrayList<PieEntry> pieEntries = new ArrayList<>();

            for (int i = 0; i < entradas.size(); i++) {
                boolean negativo = String.valueOf(entradas.get(i).getCantidad()).contains("-");
                if (!negativo) {
                    PieEntry pieEntry = new PieEntry(entradas.get(i).getCantidad(), entradas.get(i).getNombre() + " (" + obtieneDosDecimales(entradas.get(i).getCantidad()) + "€)");

                    pieEntries.add(pieEntry);
                    z++;
                }
            }

            if (z == 0) {
                pieChart.setVisibility(View.GONE);
                tvErrorEntradas.setText(getString(R.string.error_estadisticas_obj));
                tvErrorEntradas.setVisibility(View.VISIBLE);
            } else {
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
                pieChart.setCenterText(objetivo.getNombre());
                pieChart.animateXY(2000, 2000);
                pieChart.getDescription().setEnabled(false);
            }


            for (int i = 0; i < entradas.size(); i++) {
                if (i < 3) {
                    LinearLayout lyEntrada = new LinearLayout(getContext());
                    lyEntrada.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(20, 20, 20, 20);
                    lyEntrada.setLayoutParams(params);

                    LinearLayout linearLayoutPeq = new LinearLayout(getContext());
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.width = 0;
                    params1.weight = 4;
                    linearLayoutPeq.setLayoutParams(params1);
                    linearLayoutPeq.setOrientation(LinearLayout.VERTICAL);

                    ImageView icono = new ImageView(getContext());
                    LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsIcono.width = 0;
                    paramsIcono.weight = 1;
                    icono.setLayoutParams(paramsIcono);
                    switch (entradas.get(i).getCategoria()) {
                        case 1:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.cartera));
                            break;

                        case 2:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                            break;

                        case 3:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.martillo));
                            break;

                        case 4:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                            break;

                        case 5:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                            break;

                        case 6:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                            break;

                        case 7:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.otros));
                            break;

                    }
                    if (settingssp.getBoolean("oscuro", false)) {
                        icono.setColorFilter(Color.WHITE);
                    } else {
                        icono.setColorFilter(Color.BLACK);
                    }

                    TextView nombre = new TextView(getContext());
                    nombre.setText(entradas.get(i).getNombre());
                    nombre.setTextSize(18);
                    LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsNombre.width = 0;
                    paramsNombre.weight = 6;
                    nombre.setLayoutParams(paramsNombre);
                    nombre.setGravity(Gravity.CENTER);

                    TextView fecha = new TextView(getContext());
                    fecha.setText(getString(R.string.fecha) + entradas.get(i).getFecha());

                    TextView cantidad = new TextView(getContext());
                    cantidad.setText(obtieneDosDecimales(entradas.get(i).getCantidad()) + "€");

                    // Add views
                    linearLayoutPeq.addView(cantidad);
                    linearLayoutPeq.addView(fecha);

                    lyEntrada.addView(icono);
                    lyEntrada.addView(nombre);
                    lyEntrada.addView(linearLayoutPeq);
                    lyUltimasEntradas.addView(lyEntrada);
                }
            }
        }
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
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