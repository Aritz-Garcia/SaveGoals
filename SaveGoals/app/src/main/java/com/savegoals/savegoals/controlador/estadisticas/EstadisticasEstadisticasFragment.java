package com.savegoals.savegoals.controlador.estadisticas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EstadisticasEstadisticasFragment extends Fragment {

    TextView tvNombre, tvUltimasEntradas, tvErrorEntradas;
    LinearLayout lyUltimasEntradas;
    PieChart pieChart;
    int id;

    public EstadisticasEstadisticasFragment(int id) {
        // Required empty public constructor
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas_estadisticas, container, false);

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        Objetivos objetivo = appDatabase.objetivosDao().findById(id);
        List<Entradas> entradas = appDatabase.entradasDao().findByIdObj(id);

        pieChart = view.findViewById(R.id.piechart);
        tvNombre = view.findViewById(R.id.tvNombreEst);
        tvUltimasEntradas = view.findViewById(R.id.tvUltimasEntradas);
        tvErrorEntradas = view.findViewById(R.id.tvErrorEntradas);
        lyUltimasEntradas = view.findViewById(R.id.lyUltimasEntradas);

        if (objetivo != null) {
            tvNombre.setText(objetivo.getNombre());
        }

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
                PieEntry pieEntry = new PieEntry(entradas.get(i).getCantidad(), entradas.get(i).getNombre());

                pieEntries.add(pieEntry);
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);
            pieChart.setData(new PieData(pieDataSet));
            pieChart.setEntryLabelColor(R.color.black);
            pieChart.setDrawEntryLabels(true);
            pieChart.setUsePercentValues(false);
            pieChart.setCenterText(objetivo.getNombre());
            pieChart.animateXY(3000, 3000);
            pieChart.getDescription().setEnabled(false);

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

                    TextView nombre = new TextView(getContext());
                    nombre.setText(entradas.get(i).getNombre());
                    nombre.setTextSize(18);
                    LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsNombre.width = 0;
                    paramsNombre.weight = 6;
                    nombre.setLayoutParams(paramsNombre);
                    nombre.setTextColor(Color.BLACK);
                    nombre.setGravity(Gravity.CENTER);

                    TextView fecha = new TextView(getContext());
                    fecha.setText("Fecha: " + entradas.get(i).getFecha());
                    fecha.setTextColor(Color.BLACK);

                    TextView cantidad = new TextView(getContext());
                    cantidad.setText(obtieneDosDecimales(entradas.get(i).getCantidad()) + "€");
                    cantidad.setTextColor(Color.BLACK);

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

        return view;
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}