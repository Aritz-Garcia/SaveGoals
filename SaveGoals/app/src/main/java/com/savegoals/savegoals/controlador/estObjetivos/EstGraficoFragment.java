package com.savegoals.savegoals.controlador.estObjetivos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.datos.ColoresHighLight;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EstGraficoFragment extends Fragment implements View.OnClickListener {

    TextView tvError, tvTitle;
    LineChart lineChart;
    LinearLayout lyEdit;
    RadioGroup rgGrafico;
    RadioButton rbTodos, rbObjetivos;
    SharedPreferences settingssp;
    int numeroRandom;
    boolean edit = false;
    boolean todos = true;

    public EstGraficoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_est_grafico, container, false);

        tvError = view.findViewById(R.id.tvErrorLineChartEstGrafico);
        tvTitle = view.findViewById(R.id.tvTitleEstGrafico);
        lineChart = view.findViewById(R.id.linechartEstGrafico);
        lyEdit = view.findViewById(R.id.lyEditEstGraf);
        rgGrafico = view.findViewById(R.id.rgEstGrafico);
        rbTodos = view.findViewById(R.id.rbTodosEstGraf);
        rbObjetivos = view.findViewById(R.id.rbObjEstGraf);

        rbTodos.setOnClickListener(this);
        rbObjetivos.setOnClickListener(this);

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

        if (todos) {
            rbTodos.setChecked(true);
        } else {
            rbObjetivos.setChecked(true);
        }

        if (edit) {
            lyEdit.setVisibility(View.VISIBLE);
        } else {
            lyEdit.setVisibility(View.GONE);
        }

        AppDatabase db = AppDatabase.getDatabase(getContext());

        if (todos) {
            List<Objetivos> objetivos = db.objetivosDao().getAll();

            if (objetivos.isEmpty()) {
                // No hay objetivos
                tvTitle.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            } else {
                // Hay objetivos
                tvTitle.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);

                List<Entry> entries = new ArrayList<>();
                List<Entradas> entradasAll = db.entradasDao().getAll();
                // ordenar entradas por fechas
                Collections.sort(entradasAll, new Comparator<Entradas>() {
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
                    for (int j = 0; j < entradasAll.size(); j++) {
                        String[] fecha = entradasAll.get(j).getFecha().split("/");
                        String[] anoArray = fecha[2].split("");
                        String anoString = anoArray[2] + anoArray[3];
                        if (fechas.get(i).equals(fecha[0] + "/" + fecha[1] + "/" + anoString)) {
                            dentro++;
                            if (dentro == 1 && 0 != j) {
                                for (int z = 0; z < j; z++) {
                                    total += entradasAll.get(z).getCantidad();
                                }
                                for (int z = 0; z < i; z++) {
                                    entries.set(z, new Entry(z, total));
                                }
                            }
                            total += entradasAll.get(j).getCantidad();
                        }
                    }
                    entries.add(new Entry(i, total));
                }

                if (total == 0) {
                    for (int i = 0; i < entradasAll.size(); i++) {
                        String[] fecha = entradasAll.get(i).getFecha().split("/");
                        LocalDate fechaEntrada = LocalDate.of(Integer.parseInt(fecha[2]), Integer.parseInt(fecha[1]), Integer.parseInt(fecha[0]));
                        if (fechaEntrada.isBefore(fecha1)) {
                            total += entradasAll.get(i).getCantidad();
                        }
                    }
                    for (int i = 0; i < fechas.size(); i++) {
                        entries.set(i, new Entry(i, total));
                    }
                }

                LineDataSet lineDataSet = new LineDataSet(entries, "Total Ahorrado");
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
                lineChart.animateXY(3000, 2000, Easing.EaseInOutBack, Easing.EaseInOutBack);
                lineChart.getDescription().setEnabled(false);
            }
        } else {
            List<Objetivos> objetivos = db.objetivosDao().getAll();

            if (objetivos.isEmpty()) {
                // No hay objetivos
                tvTitle.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            } else {
                // Hay objetivos
                tvTitle.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);

                List<Entradas> entradasAll = db.entradasDao().getAll();
                // Ordenar entradas por fechas
                Collections.sort(entradasAll, new Comparator<Entradas>() {
                    @Override
                    public int compare(Entradas e1, Entradas e2) {
                        return e1.getFechaAsDate().compareTo(e2.getFechaAsDate());
                    }
                });
                LocalDate fecha1 = LocalDate.now();
                LocalDate fecha2 = fecha1.minusDays(15);
                LocalDate fecha3 = fecha2;

                int diffDays = 15;

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


                List<Objetivos> objetivosAll = db.objetivosDao().getAll();
                ArrayList<ILineDataSet> listLineDataSet = new ArrayList<>();

                for (int i = 0; i < objetivosAll.size(); i++) {
                    float total = 0;
                    List<Entry> entries = new ArrayList<>();
                    List<Entradas> entradas = db.entradasDao().findByIdObj(objetivosAll.get(i).getId());
                    int dentro = 0;

                    for (int z = 0; z < fechas.size(); z++) {
                        for (int j = 0; j < entradas.size(); j++) {
                            String[] fecha = entradas.get(j).getFecha().split("/");
                            String[] anoArray = fecha[2].split("");
                            String anoString = anoArray[2] + anoArray[3];
                            if (fechas.get(z).equals(fecha[0] + "/" + fecha[1] + "/" + anoString)) {
                                dentro++;
                                if (dentro == 1 && 0 != j) {
                                    for (int k = 0; k < j; k++) {
                                        total += entradas.get(k).getCantidad();
                                    }
                                    for (int k = 0; k < z; k++) {
                                        entries.set(k, new Entry(k, total));
                                    }
                                }
                                total += entradas.get(j).getCantidad();
                            }
                        }
                        entries.add(new Entry(z, total));
                    }

                    if (total == 0) {
                        for (int j = 0; j < entradas.size(); j++) {
                            String[] fecha = entradas.get(j).getFecha().split("/");
                            LocalDate fechaEntrada = LocalDate.of(Integer.parseInt(fecha[2]), Integer.parseInt(fecha[1]), Integer.parseInt(fecha[0]));
                            if (fechaEntrada.isBefore(fecha1)) {
                                total += entradas.get(j).getCantidad();
                            }
                        }
                        for (int j = 0; j < fechas.size(); j++) {
                            entries.set(j, new Entry(j, total));
                        }
                    }


                    LineDataSet lineDataSet = new LineDataSet(entries, objetivosAll.get(i).getNombre());
                    lineDataSet.setDrawFilled(true);
                    int num = 0;
                    if (i < 5) {
                        lineDataSet.setFillColor(ColorTemplate.VORDIPLOM_COLORS[i]);
                        lineDataSet.setColor(ColoresHighLight.COLORESGET_VORDIPLOM_COLORS[i]);
                        lineDataSet.setHighLightColor(ColoresHighLight.COLORESGET_VORDIPLOM_COLORS[i]);
                    } else if (i >= 5 && i < 10) {
                        lineDataSet.setFillColor(ColorTemplate.COLORFUL_COLORS[num]);
                        lineDataSet.setColor(ColoresHighLight.COLORESGET_COLORFUL_COLORS[num]);
                        lineDataSet.setHighLightColor(ColoresHighLight.COLORESGET_COLORFUL_COLORS[num]);
                        num++;
                    } else {
                        numeroRandom = (int) (Math.random() * 2);
                        if (numeroRandom == 0) {
                            numeroRandom = (int) (Math.random() * 5);
                            lineDataSet.setFillColor(ColorTemplate.VORDIPLOM_COLORS[numeroRandom]);
                            lineDataSet.setColor(ColoresHighLight.COLORESGET_VORDIPLOM_COLORS[numeroRandom]);
                            lineDataSet.setHighLightColor(ColoresHighLight.COLORESGET_VORDIPLOM_COLORS[numeroRandom]);
                        } else {
                            numeroRandom = (int) (Math.random() * 5);
                            lineDataSet.setFillColor(ColorTemplate.COLORFUL_COLORS[numeroRandom]);
                            lineDataSet.setColor(ColoresHighLight.COLORESGET_COLORFUL_COLORS[numeroRandom]);
                            lineDataSet.setHighLightColor(ColoresHighLight.COLORESGET_COLORFUL_COLORS[numeroRandom]);
                        }
                    }
                    lineDataSet.setCircleColor(Color.DKGRAY);
                    lineDataSet.setValueTextSize(12);
                    if (settingssp.getBoolean("oscuro", false)) {
                        lineDataSet.setValueTextColor(Color.WHITE);
                    } else {
                        lineDataSet.setValueTextColor(Color.BLACK);
                    }

                    if (total > 0) {
                        listLineDataSet.add(lineDataSet);
                    }
                }
                if (settingssp.getBoolean("oscuro", false)) {
                    lineChart.getXAxis().setTextColor(Color.WHITE);
                    lineChart.getAxisLeft().setTextColor(Color.WHITE);
                    lineChart.getAxisRight().setTextColor(Color.WHITE);
                    lineChart.getLegend().setTextColor(Color.WHITE);
                } else {
                    lineChart.getXAxis().setTextColor(Color.BLACK);
                    lineChart.getAxisLeft().setTextColor(Color.BLACK);
                    lineChart.getAxisRight().setTextColor(Color.BLACK);
                    lineChart.getLegend().setTextColor(Color.BLACK);
                }
                lineChart.setData(new LineData(listLineDataSet));
                lineChart.animateXY(3000, 2000, Easing.EaseInOutBack, Easing.EaseInOutBack);
                lineChart.getDescription().setEnabled(false);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == rbTodos.getId()) {
            if (rgGrafico.getCheckedRadioButtonId() == rbTodos.getId()) {
                todos = true;
                onResume();
            }

        } else if (v.getId() == rbObjetivos.getId()) {
            if (rgGrafico.getCheckedRadioButtonId() == rbObjetivos.getId()) {
                todos = false;
                onResume();
            }
        }
    }
}