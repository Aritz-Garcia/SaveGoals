package com.savegoals.savegoals.controlador.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savegoals.savegoals.AddObjetivosActivity;
import com.savegoals.savegoals.MainActivity;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObjetivosFragment extends Fragment implements View.OnClickListener {

    private Menu mOptionsMenu;
    LinearLayout linearLayoutObjetivos, linearLayoutCumplidos, linearLayoutFechaPasada, linearLayoutArchivados;
    TextView tv_objetivos, tv_cumplidos, tv_fecha_pasada,tv_archivados, tv_error;
    FloatingActionButton btnadd;
    SharedPreferences settingssp;
    GradientDrawable fondoSelect;
    long tiempoEnMS = 0;
    boolean checkboxVisible = false, archivado = false;

    public ObjetivosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objetivos, container, false);

        linearLayoutObjetivos = view.findViewById(R.id.objetivos);
        linearLayoutCumplidos = view.findViewById(R.id.cumplidos);
        linearLayoutFechaPasada = view.findViewById(R.id.fecha_pasada);
        linearLayoutArchivados = view.findViewById(R.id.archivados);
        tv_objetivos = view.findViewById(R.id.tv_objetivos);
        tv_cumplidos = view.findViewById(R.id.tv_cumplidos);
        tv_fecha_pasada = view.findViewById(R.id.tv_fecha_pasada);
        tv_archivados = view.findViewById(R.id.tv_archivados);
        tv_error = view.findViewById(R.id.tv_error_sin_objetivos);
        btnadd = view.findViewById(R.id.btnAddObj);

        btnadd.setOnClickListener(this);
        linearLayoutObjetivos.setOnClickListener(this);
        linearLayoutCumplidos.setOnClickListener(this);
        linearLayoutFechaPasada.setOnClickListener(this);
        linearLayoutArchivados.setOnClickListener(this);

        fondoSelect = new GradientDrawable();
        fondoSelect.setShape(GradientDrawable.RECTANGLE);
        fondoSelect.setColor(Color.rgb(65, 105, 225));
        fondoSelect.setCornerRadius(20f);

        setDayNight();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        linearLayoutObjetivos.removeAllViews();
        linearLayoutCumplidos.removeAllViews();
        linearLayoutFechaPasada.removeAllViews();
        linearLayoutArchivados.removeAllViews();
        checkboxVisible = false;
        archivado = false;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    public void onResume() {
        super.onResume();

        boolean hayObjetivos = false;

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        if (!archivado) {
            tv_objetivos.setVisibility(View.VISIBLE);
            linearLayoutObjetivos.setVisibility(View.VISIBLE);
            tv_cumplidos.setVisibility(View.VISIBLE);
            linearLayoutCumplidos.setVisibility(View.VISIBLE);
            tv_fecha_pasada.setVisibility(View.VISIBLE);
            linearLayoutFechaPasada.setVisibility(View.VISIBLE);
            tv_archivados.setVisibility(View.GONE);
            linearLayoutArchivados.setVisibility(View.GONE);

            List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();
            List<Objetivos> objetivosCompletados = appDatabase.objetivosDao().getAllCompleted();
            List<Objetivos> objetivosFechaPasada = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date date = new Date();
            for (int i = 0; i < objetivosSinCompletar.size(); i++) {
                String fecha = objetivosSinCompletar.get(i).getFecha();
                try {
                    Date fechaObjeto = dateFormat.parse(fecha);
                    if (date.after(fechaObjeto)) {
                        // Fecha pasada
                        objetivosFechaPasada.add(objetivosSinCompletar.get(i));
                        objetivosSinCompletar.remove(i);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
            if (!objetivosSinCompletar.isEmpty()) {
                hayObjetivos = true;
                tv_objetivos.setVisibility(View.VISIBLE);
                for (int i = 0; i < objetivosSinCompletar.size(); i++) {

                    int porcentajeInt = (int) ((objetivosSinCompletar.get(i).getAhorrado() * 100) / objetivosSinCompletar.get(i).getCantidad());

                    LinearLayout linearLayoutTodo = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsTodo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsTodo.setMargins(25, 2, 25, 2);
                    linearLayoutTodo.setPadding(5, 23, 5, 23);
                    linearLayoutTodo.setLayoutParams(paramsTodo);
                    linearLayoutTodo.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutTodo.setId(objetivosSinCompletar.get(i).getId());

                    LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayoutGeneral.setLayoutParams(paramsGeneral);
                    linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutMedio = new LinearLayout(getContext());
                    linearLayoutMedio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayoutMedio.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout linearLayoutPeq = new LinearLayout(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.width = 0;
                    params.weight = 4;
                    linearLayoutPeq.setLayoutParams(params);
                    linearLayoutPeq.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutProgressBar = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsProgressBar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgressBar.setMargins(30, 20, 30, 0);
                    linearLayoutProgressBar.setLayoutParams(paramsProgressBar);
                    linearLayoutProgressBar.setOrientation(LinearLayout.HORIZONTAL);

                    CheckBox checkBox = new CheckBox(getContext());
                    LinearLayout.LayoutParams paramsCheckbox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsCheckbox.setMargins(0, 0, 15, 0);
                    checkBox.setLayoutParams(paramsCheckbox);
                    checkBox.setClickable(false);
                    checkBox.setGravity(Gravity.CENTER);
                    checkBox.setVisibility(View.GONE);

                    TextView nombre = new TextView(getContext());
                    nombre.setText(objetivosSinCompletar.get(i).getNombre());
                    nombre.setTextSize(18);
                    LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsNombre.width = 0;
                    paramsNombre.weight = 6;
                    nombre.setLayoutParams(paramsNombre);
                    nombre.setGravity(Gravity.CENTER);

                    ImageView icono = new ImageView(getContext());
                    LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsIcono.width = 0;
                    paramsIcono.weight = 1;
                    icono.setLayoutParams(paramsIcono);
                    switch (objetivosSinCompletar.get(i).getCategoria()) {
                        case 1:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                            break;

                        case 2:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                            break;

                        case 3:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                            break;

                        case 4:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                            break;

                        case 5:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                            break;

                        case 6:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.mando));
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

                    TextView fecha = new TextView(getContext());
                    fecha.setText(getString(R.string.fecha) + objetivosSinCompletar.get(i).getFecha());

                    TextView cantidad = new TextView(getContext());
                    cantidad.setText(obtieneDosDecimales(objetivosSinCompletar.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivosSinCompletar.get(i).getCantidad()) + "€");

                    ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                    LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgress.width = 0;
                    paramsProgress.weight = 10;
                    paramsProgress.setMargins(0, 0, 50, 0);
                    progressBar.setLayoutParams(paramsProgress);
                    progressBar.setMax(100);
                    progressBar.setProgress(porcentajeInt);
                    if (porcentajeInt < 50) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_rojo));
                    } else if (porcentajeInt < 75) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_amarillo));
                    } else if (porcentajeInt < 100) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_verde));
                    } else {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_completado));
                    }

                    TextView porcentaje = new TextView(getContext());
                    porcentaje.setText(porcentajeInt + "%");
                    LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsPorcentaje.width = 0;
                    paramsPorcentaje.weight = 2;
                    porcentaje.setLayoutParams(paramsPorcentaje);
                    porcentaje.setGravity(Gravity.CENTER);

                    // Add views
                    linearLayoutPeq.addView(cantidad);
                    linearLayoutPeq.addView(fecha);

                    linearLayoutMedio.addView(icono);
                    linearLayoutMedio.addView(nombre);
                    linearLayoutMedio.addView(linearLayoutPeq);

                    linearLayoutGeneral.addView(linearLayoutMedio);

                    linearLayoutProgressBar.addView(progressBar);
                    linearLayoutProgressBar.addView(porcentaje);

                    linearLayoutGeneral.addView(linearLayoutProgressBar);
                    linearLayoutTodo.addView(checkBox);
                    linearLayoutTodo.addView(linearLayoutGeneral);
                    linearLayoutObjetivos.addView(linearLayoutTodo);

                    linearLayoutTodo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!checkboxVisible) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("id", linearLayoutTodo.getId());
                                startActivity(intent);
                            } else {
                                checkBox.setChecked(!checkBox.isChecked());
                                if (checkBox.isChecked()) {
                                    linearLayoutTodo.setBackground(fondoSelect);
                                } else {
                                    linearLayoutTodo.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }

                        }
                    });

                    linearLayoutTodo.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, android.view.MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN){
                                tiempoEnMS = (Long) System.currentTimeMillis();
                            }
                            else if (event.getAction() == MotionEvent.ACTION_UP){
                                if (((Long) System.currentTimeMillis() - tiempoEnMS) >= 1000){
                                    if (!checkboxVisible) {
                                        checkboxVisible = true;
                                        linearLayoutTodo.setBackground(fondoSelect);
                                        checkBox.setChecked(true);
                                        checkboxVisible();
                                    }

                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                }

            } else {
                tv_objetivos.setVisibility(View.GONE);
            }

            if (!objetivosCompletados.isEmpty()) {
                hayObjetivos = true;
                tv_cumplidos.setVisibility(View.VISIBLE);
                for (int i = 0; i < objetivosCompletados.size(); i++) {
                    int porcentajeInt = (int) ((objetivosCompletados.get(i).getAhorrado() * 100) / objetivosCompletados.get(i).getCantidad());

                    LinearLayout linearLayoutTodo = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsTodo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsTodo.setMargins(25, 2, 25, 2);
                    linearLayoutTodo.setPadding(5, 23, 5, 23);
                    linearLayoutTodo.setLayoutParams(paramsTodo);
                    linearLayoutTodo.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutTodo.setId(objetivosCompletados.get(i).getId());

                    LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayoutGeneral.setLayoutParams(paramsGeneral);
                    linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutMedio = new LinearLayout(getContext());
                    linearLayoutMedio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayoutMedio.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout linearLayoutPeq = new LinearLayout(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.width = 0;
                    params.weight = 4;
                    linearLayoutPeq.setLayoutParams(params);
                    linearLayoutPeq.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutProgressBar = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsProgressBar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgressBar.setMargins(30, 20, 30, 0);
                    linearLayoutProgressBar.setLayoutParams(paramsProgressBar);
                    linearLayoutProgressBar.setOrientation(LinearLayout.HORIZONTAL);

                    CheckBox checkBox = new CheckBox(getContext());
                    LinearLayout.LayoutParams paramsCheckbox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsCheckbox.setMargins(0, 0, 15, 0);
                    checkBox.setLayoutParams(paramsCheckbox);
                    checkBox.setClickable(false);
                    checkBox.setGravity(Gravity.CENTER);
                    checkBox.setVisibility(View.GONE);

                    TextView nombre = new TextView(getContext());
                    nombre.setText(objetivosCompletados.get(i).getNombre());
                    nombre.setTextSize(18);
                    LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsNombre.width = 0;
                    paramsNombre.weight = 6;
                    nombre.setLayoutParams(paramsNombre);
                    nombre.setGravity(Gravity.CENTER);

                    ImageView icono = new ImageView(getContext());
                    LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsIcono.width = 0;
                    paramsIcono.weight = 1;
                    icono.setLayoutParams(paramsIcono);
                    switch (objetivosCompletados.get(i).getCategoria()) {
                        case 1:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                            break;

                        case 2:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                            break;

                        case 3:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                            break;

                        case 4:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                            break;

                        case 5:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                            break;

                        case 6:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.mando));
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

                    TextView fecha = new TextView(getContext());
                    fecha.setText(getString(R.string.fecha) + objetivosCompletados.get(i).getFecha());

                    TextView cantidad = new TextView(getContext());
                    cantidad.setText(obtieneDosDecimales(objetivosCompletados.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivosCompletados.get(i).getCantidad()) + "€");

                    ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                    LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgress.width = 0;
                    paramsProgress.weight = 10;
                    paramsProgress.setMargins(0, 0, 50, 0);
                    progressBar.setLayoutParams(paramsProgress);
                    progressBar.setMax(100);
                    progressBar.setProgress(porcentajeInt);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_completado));

                    TextView porcentaje = new TextView(getContext());
                    porcentaje.setText(porcentajeInt + "%");
                    LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsPorcentaje.width = 0;
                    paramsPorcentaje.weight = 2;
                    porcentaje.setLayoutParams(paramsPorcentaje);
                    porcentaje.setGravity(Gravity.CENTER);

                    // Add views
                    linearLayoutPeq.addView(cantidad);
                    linearLayoutPeq.addView(fecha);

                    linearLayoutMedio.addView(icono);
                    linearLayoutMedio.addView(nombre);
                    linearLayoutMedio.addView(linearLayoutPeq);

                    linearLayoutGeneral.addView(linearLayoutMedio);

                    linearLayoutProgressBar.addView(progressBar);
                    linearLayoutProgressBar.addView(porcentaje);

                    linearLayoutGeneral.addView(linearLayoutProgressBar);
                    linearLayoutTodo.addView(checkBox);
                    linearLayoutTodo.addView(linearLayoutGeneral);
                    linearLayoutCumplidos.addView(linearLayoutTodo);

                    linearLayoutTodo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!checkboxVisible) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("id", linearLayoutTodo.getId());
                                startActivity(intent);
                            } else {
                                checkBox.setChecked(!checkBox.isChecked());
                                if (checkBox.isChecked()) {
                                    linearLayoutTodo.setBackground(fondoSelect);
                                } else {
                                    linearLayoutTodo.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }
                        }
                    });

                    linearLayoutTodo.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, android.view.MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN){
                                tiempoEnMS = (Long) System.currentTimeMillis();
                            }
                            else if (event.getAction() == MotionEvent.ACTION_UP){
                                if (((Long) System.currentTimeMillis() - tiempoEnMS) >= 1000){
                                    if (!checkboxVisible) {
                                        checkboxVisible = true;
                                        linearLayoutTodo.setBackground(fondoSelect);
                                        checkBox.setChecked(true);
                                        checkboxVisible();
                                    }

                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            } else {
                tv_cumplidos.setVisibility(View.GONE);
            }

            if (!objetivosFechaPasada.isEmpty()) {
                hayObjetivos = true;
                tv_fecha_pasada.setVisibility(View.VISIBLE);
                for (int i = 0; i < objetivosFechaPasada.size(); i++) {

                    int porcentajeInt = (int) ((objetivosFechaPasada.get(i).getAhorrado() * 100) / objetivosFechaPasada.get(i).getCantidad());

                    LinearLayout linearLayoutTodo = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsTodo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsTodo.setMargins(25, 2, 25, 2);
                    linearLayoutTodo.setPadding(5, 23, 5, 23);
                    linearLayoutTodo.setLayoutParams(paramsTodo);
                    linearLayoutTodo.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutTodo.setId(objetivosFechaPasada.get(i).getId());

                    LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayoutGeneral.setLayoutParams(paramsGeneral);
                    linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutMedio = new LinearLayout(getContext());
                    linearLayoutMedio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayoutMedio.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout linearLayoutPeq = new LinearLayout(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.width = 0;
                    params.weight = 4;
                    linearLayoutPeq.setLayoutParams(params);
                    linearLayoutPeq.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutProgressBar = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsProgressBar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgressBar.setMargins(30, 20, 30, 0);
                    linearLayoutProgressBar.setLayoutParams(paramsProgressBar);
                    linearLayoutProgressBar.setOrientation(LinearLayout.HORIZONTAL);

                    CheckBox checkBox = new CheckBox(getContext());
                    LinearLayout.LayoutParams paramsCheckbox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsCheckbox.setMargins(0, 0, 15, 0);
                    checkBox.setLayoutParams(paramsCheckbox);
                    checkBox.setClickable(false);
                    checkBox.setGravity(Gravity.CENTER);
                    checkBox.setVisibility(View.GONE);

                    TextView nombre = new TextView(getContext());
                    nombre.setText(objetivosFechaPasada.get(i).getNombre());
                    nombre.setTextSize(18);
                    LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsNombre.width = 0;
                    paramsNombre.weight = 6;
                    nombre.setLayoutParams(paramsNombre);
                    nombre.setGravity(Gravity.CENTER);

                    ImageView icono = new ImageView(getContext());
                    LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsIcono.width = 0;
                    paramsIcono.weight = 1;
                    icono.setLayoutParams(paramsIcono);
                    switch (objetivosFechaPasada.get(i).getCategoria()) {
                        case 1:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                            break;

                        case 2:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                            break;

                        case 3:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                            break;

                        case 4:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                            break;

                        case 5:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                            break;

                        case 6:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.mando));
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

                    TextView fecha = new TextView(getContext());
                    fecha.setText(getString(R.string.fecha) + objetivosFechaPasada.get(i).getFecha());

                    TextView cantidad = new TextView(getContext());
                    cantidad.setText(obtieneDosDecimales(objetivosFechaPasada.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivosFechaPasada.get(i).getCantidad()) + "€");

                    ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                    LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgress.width = 0;
                    paramsProgress.weight = 10;
                    paramsProgress.setMargins(0, 0, 50, 0);
                    progressBar.setLayoutParams(paramsProgress);
                    progressBar.setMax(100);
                    progressBar.setProgress(porcentajeInt);
                    if (porcentajeInt < 50) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_rojo));
                    } else if (porcentajeInt < 75) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_amarillo));
                    } else if (porcentajeInt < 100) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_verde));
                    } else {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_completado));
                    }

                    TextView porcentaje = new TextView(getContext());
                    porcentaje.setText(porcentajeInt + "%");
                    LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsPorcentaje.width = 0;
                    paramsPorcentaje.weight = 2;
                    porcentaje.setLayoutParams(paramsPorcentaje);
                    porcentaje.setGravity(Gravity.CENTER);

                    // Add views
                    linearLayoutPeq.addView(cantidad);
                    linearLayoutPeq.addView(fecha);

                    linearLayoutMedio.addView(icono);
                    linearLayoutMedio.addView(nombre);
                    linearLayoutMedio.addView(linearLayoutPeq);

                    linearLayoutGeneral.addView(linearLayoutMedio);

                    linearLayoutProgressBar.addView(progressBar);
                    linearLayoutProgressBar.addView(porcentaje);

                    linearLayoutGeneral.addView(linearLayoutProgressBar);
                    linearLayoutTodo.addView(checkBox);
                    linearLayoutTodo.addView(linearLayoutGeneral);
                    linearLayoutFechaPasada.addView(linearLayoutTodo);

                    linearLayoutTodo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!checkboxVisible) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("id", linearLayoutTodo.getId());
                                startActivity(intent);
                            } else {
                                checkBox.setChecked(!checkBox.isChecked());
                                if (checkBox.isChecked()) {
                                    linearLayoutTodo.setBackground(fondoSelect);
                                } else {
                                    linearLayoutTodo.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }
                        }
                    });

                    linearLayoutTodo.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, android.view.MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN){
                                tiempoEnMS = (Long) System.currentTimeMillis();
                            }
                            else if (event.getAction() == MotionEvent.ACTION_UP){
                                if (((Long) System.currentTimeMillis() - tiempoEnMS) >= 1000){
                                    if (!checkboxVisible) {
                                        checkboxVisible = true;
                                        linearLayoutTodo.setBackground(fondoSelect);
                                        checkBox.setChecked(true);
                                        checkboxVisible();
                                    }

                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            } else {
                tv_fecha_pasada.setVisibility(View.GONE);
            }

            if (!hayObjetivos) {
                tv_error.setVisibility(View.VISIBLE);
            } else {
                tv_error.setVisibility(View.GONE);
            }
        } else {
            tv_objetivos.setVisibility(View.GONE);
            linearLayoutObjetivos.setVisibility(View.GONE);
            tv_cumplidos.setVisibility(View.GONE);
            linearLayoutCumplidos.setVisibility(View.GONE);
            tv_fecha_pasada.setVisibility(View.GONE);
            linearLayoutFechaPasada.setVisibility(View.GONE);
            tv_archivados.setVisibility(View.VISIBLE);
            linearLayoutArchivados.setVisibility(View.VISIBLE);

            List<Objetivos> objetivosArchivados = appDatabase.objetivosDao().getAllArchived();

            if (!objetivosArchivados.isEmpty()) {
                hayObjetivos = true;
                tv_archivados.setVisibility(View.VISIBLE);
                for (int i = 0; i < objetivosArchivados.size(); i++) {

                    int porcentajeInt = (int) ((objetivosArchivados.get(i).getAhorrado() * 100) / objetivosArchivados.get(i).getCantidad());

                    LinearLayout linearLayoutTodo = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsTodo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsTodo.setMargins(25, 2, 25, 2);
                    linearLayoutTodo.setPadding(5, 23, 5, 23);
                    linearLayoutTodo.setLayoutParams(paramsTodo);
                    linearLayoutTodo.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutTodo.setId(objetivosArchivados.get(i).getId());

                    LinearLayout linearLayoutGeneral = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsGeneral = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayoutGeneral.setLayoutParams(paramsGeneral);
                    linearLayoutGeneral.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutMedio = new LinearLayout(getContext());
                    linearLayoutMedio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayoutMedio.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout linearLayoutPeq = new LinearLayout(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.width = 0;
                    params.weight = 4;
                    linearLayoutPeq.setLayoutParams(params);
                    linearLayoutPeq.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout linearLayoutProgressBar = new LinearLayout(getContext());
                    LinearLayout.LayoutParams paramsProgressBar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgressBar.setMargins(30, 20, 30, 0);
                    linearLayoutProgressBar.setLayoutParams(paramsProgressBar);
                    linearLayoutProgressBar.setOrientation(LinearLayout.HORIZONTAL);

                    CheckBox checkBox = new CheckBox(getContext());
                    LinearLayout.LayoutParams paramsCheckbox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsCheckbox.setMargins(0, 0, 15, 0);
                    checkBox.setLayoutParams(paramsCheckbox);
                    checkBox.setClickable(false);
                    checkBox.setGravity(Gravity.CENTER);
                    checkBox.setVisibility(View.GONE);

                    TextView nombre = new TextView(getContext());
                    nombre.setText(objetivosArchivados.get(i).getNombre());
                    nombre.setTextSize(18);
                    LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsNombre.width = 0;
                    paramsNombre.weight = 6;
                    nombre.setLayoutParams(paramsNombre);
                    nombre.setGravity(Gravity.CENTER);

                    ImageView icono = new ImageView(getContext());
                    LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsIcono.width = 0;
                    paramsIcono.weight = 1;
                    icono.setLayoutParams(paramsIcono);
                    switch (objetivosArchivados.get(i).getCategoria()) {
                        case 1:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.avion));
                            break;

                        case 2:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.hucha));
                            break;

                        case 3:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.regalo));
                            break;

                        case 4:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.carrito));
                            break;

                        case 5:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.clase));
                            break;

                        case 6:
                            icono.setImageDrawable(getResources().getDrawable(R.drawable.mando));
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

                    TextView fecha = new TextView(getContext());
                    fecha.setText(getString(R.string.fecha) + objetivosArchivados.get(i).getFecha());

                    TextView cantidad = new TextView(getContext());
                    cantidad.setText(obtieneDosDecimales(objetivosArchivados.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivosArchivados.get(i).getCantidad()) + "€");

                    ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                    LinearLayout.LayoutParams paramsProgress = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsProgress.width = 0;
                    paramsProgress.weight = 10;
                    paramsProgress.setMargins(0, 0, 50, 0);
                    progressBar.setLayoutParams(paramsProgress);
                    progressBar.setMax(100);
                    progressBar.setProgress(porcentajeInt);
                    if (porcentajeInt < 50) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_rojo));
                    } else if (porcentajeInt < 75) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_amarillo));
                    } else if (porcentajeInt < 100) {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_verde));
                    } else {
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar_completado));
                    }

                    TextView porcentaje = new TextView(getContext());
                    porcentaje.setText(porcentajeInt + "%");
                    LinearLayout.LayoutParams paramsPorcentaje = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsPorcentaje.width = 0;
                    paramsPorcentaje.weight = 2;
                    porcentaje.setLayoutParams(paramsPorcentaje);
                    porcentaje.setGravity(Gravity.CENTER);

                    // Add views
                    linearLayoutPeq.addView(cantidad);
                    linearLayoutPeq.addView(fecha);

                    linearLayoutMedio.addView(icono);
                    linearLayoutMedio.addView(nombre);
                    linearLayoutMedio.addView(linearLayoutPeq);

                    linearLayoutGeneral.addView(linearLayoutMedio);

                    linearLayoutProgressBar.addView(progressBar);
                    linearLayoutProgressBar.addView(porcentaje);

                    linearLayoutGeneral.addView(linearLayoutProgressBar);
                    linearLayoutTodo.addView(checkBox);
                    linearLayoutTodo.addView(linearLayoutGeneral);
                    linearLayoutArchivados.addView(linearLayoutTodo);

                    linearLayoutTodo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!checkboxVisible) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("id", linearLayoutTodo.getId());
                                startActivity(intent);
                            } else {
                                checkBox.setChecked(!checkBox.isChecked());
                                if (checkBox.isChecked()) {
                                    linearLayoutTodo.setBackground(fondoSelect);
                                } else {
                                    linearLayoutTodo.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }

                        }
                    });

                    linearLayoutTodo.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, android.view.MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN){
                                tiempoEnMS = (Long) System.currentTimeMillis();
                            }
                            else if (event.getAction() == MotionEvent.ACTION_UP){
                                if (((Long) System.currentTimeMillis() - tiempoEnMS) >= 1000){
                                    if (!checkboxVisible) {
                                        checkboxVisible = true;
                                        linearLayoutTodo.setBackground(fondoSelect);
                                        checkBox.setChecked(true);
                                        checkboxVisible();
                                    }

                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                }

            }

            if (!hayObjetivos) {
                tv_error.setVisibility(View.VISIBLE);
            } else {
                tv_error.setVisibility(View.GONE);
            }
        }

    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mOptionsMenu = menu;
        getActivity().getMenuInflater().inflate(R.menu.main_menu_select_archivar_0, mOptionsMenu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cancelarBtn) {
            checkboxVisible = !checkboxVisible;
            addMenu();
            checkboxVisible();
            return true;
        } else if (id == R.id.selectBtn) {
            checkboxVisible = !checkboxVisible;
            checkboxVisible();
            return true;
        } else if (id == R.id.archivarBtn) {
            archivado = !archivado;
            checkboxVisible = false;
            addMenu();
            checkboxVisible();
            eliminar();
            onResume();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnadd.getId()) {
            if (checkboxVisible) {
                archivarODesarchivar();
                checkboxVisible = false;
                addMenu();
                checkboxVisible();
                eliminar();
                onResume();

            } else {
                Intent intent = new Intent(getContext(), AddObjetivosActivity.class);
                startActivity(intent);
            }
        }

    }

    private void eliminar() {
        linearLayoutObjetivos.removeAllViews();
        linearLayoutCumplidos.removeAllViews();
        linearLayoutFechaPasada.removeAllViews();
        linearLayoutArchivados.removeAllViews();
    }

    private void addMenu() {
        mOptionsMenu.clear();
        if (!checkboxVisible) {
            getActivity().getMenuInflater().inflate(R.menu.main_menu_select_archivar_1, mOptionsMenu);
        } else {
            getActivity().getMenuInflater().inflate(R.menu.main_menu_select_archivar_2, mOptionsMenu);
        }
    }

    private void checkboxVisible() {
        addMenu();
        if (checkboxVisible) {
            btnadd.setForeground(getResources().getDrawable(R.drawable.caja));
        } else {
            btnadd.setForeground(getResources().getDrawable(R.drawable.mas));
        }
        if (!archivado) {
            int num = linearLayoutObjetivos.getChildCount();

            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutObjetivos.getChildAt(i);
                if (checkboxVisible) {
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.select_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setVisibility(View.VISIBLE);
                } else {
                    ll.setBackgroundColor(Color.TRANSPARENT);
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.deselect_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setChecked(false);
                    cb.setVisibility(View.GONE);
                }
            }

            num = linearLayoutFechaPasada.getChildCount();

            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutFechaPasada.getChildAt(i);
                if (checkboxVisible) {
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.select_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setVisibility(View.VISIBLE);
                } else {
                    ll.setBackgroundColor(Color.TRANSPARENT);
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.deselect_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setChecked(false);
                    cb.setVisibility(View.GONE);
                }
            }

            num = linearLayoutCumplidos.getChildCount();

            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutCumplidos.getChildAt(i);
                if (checkboxVisible) {
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.select_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setVisibility(View.VISIBLE);
                } else {
                    ll.setBackgroundColor(Color.TRANSPARENT);
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.deselect_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setChecked(false);
                    cb.setVisibility(View.GONE);
                }
            }
        } else {
            int num = linearLayoutArchivados.getChildCount();

            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutArchivados.getChildAt(i);
                if (checkboxVisible) {
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.select_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setVisibility(View.VISIBLE);
                } else {
                    ll.setBackgroundColor(Color.TRANSPARENT);
                    Animation animationCheckbox = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.deselect_anim);
                    CheckBox cb = (CheckBox) ll.getChildAt(0);
                    cb.startAnimation(animationCheckbox);
                    cb.setChecked(false);
                    cb.setVisibility(View.GONE);
                }
            }
        }


    }

    private void archivarODesarchivar() {
        if (!archivado) {
            int num = linearLayoutObjetivos.getChildCount();
            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutObjetivos.getChildAt(i);
                CheckBox cb = (CheckBox) ll.getChildAt(0);
                if (cb.isChecked()) {
                    AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
                    appDatabase.objetivosDao().updateArchivado(ll.getId(), true);
                }
            }

            num = linearLayoutCumplidos.getChildCount();
            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutCumplidos.getChildAt(i);
                CheckBox cb = (CheckBox) ll.getChildAt(0);
                if (cb.isChecked()) {
                    AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
                    appDatabase.objetivosDao().updateArchivado(ll.getId(), true);
                }
            }

            num = linearLayoutFechaPasada.getChildCount();
            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutFechaPasada.getChildAt(i);
                CheckBox cb = (CheckBox) ll.getChildAt(0);
                if (cb.isChecked()) {
                    AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
                    appDatabase.objetivosDao().updateArchivado(ll.getId(), true);
                }
            }
        } else {
            int num = linearLayoutArchivados.getChildCount();

            for (int i = 0; i < num; i++) {
                LinearLayout ll = (LinearLayout) linearLayoutArchivados.getChildAt(i);
                CheckBox cb = (CheckBox) ll.getChildAt(0);
                if (cb.isChecked()) {
                    AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
                    appDatabase.objetivosDao().updateArchivado(ll.getId(), false);
                }
            }
        }

        checkboxVisible = false;
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnadd.setForegroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnadd.setForegroundTintList(ColorStateList.valueOf(Color.BLACK));
        }
    }
}