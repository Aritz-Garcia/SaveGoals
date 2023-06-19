package com.savegoals.savegoals.controlador.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.savegoals.savegoals.MainActivity;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarioFragment extends Fragment implements View.OnClickListener {

    TextView tvMesCal, tvObjetivosCal, tvEntradasCal;
    Button btnIrDia;
    CompactCalendarView compactCalendarView;
    LinearLayout lyCalDatosObj, lyCalDatosEnt;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
    SharedPreferences settingssp;

    public CalendarioFragment() {
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
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        tvMesCal = view.findViewById(R.id.tvMesCal);
        tvObjetivosCal = view.findViewById(R.id.tvObjetivosCal);
        tvEntradasCal = view.findViewById(R.id.tvEntradasCal);
        btnIrDia = view.findViewById(R.id.btnIrDia);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        lyCalDatosObj = view.findViewById(R.id.lyCalDatosObj);
        lyCalDatosEnt = view.findViewById(R.id.lyCalDatosEnt);

        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault());
        compactCalendarView.setShouldDrawDaysHeader(true);

        btnIrDia.setEnabled(false);
        btnIrDia.setOnClickListener(this);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                fechaSelect(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mesText();
                comprovarFecha();
                Date primerDia = compactCalendarView.getFirstDayOfCurrentMonth();
                fechaSelect(primerDia);
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        compactCalendarView.removeAllEvents();
        Date date = new Date();
        compactCalendarView.setCurrentDate(date);
    }

    @Override
    public void onResume() {
        super.onResume();

        AppDatabase db = AppDatabase.getDatabase(getContext());
        Date dateHoy = new Date();

        List<Objetivos> objetivos = db.objetivosDao().getAll();
        List<Entradas> entradas = db.entradasDao().getAll();

        mesText();
        fechaSelect(dateHoy);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = 0;
        params.weight = 1;
        tvMesCal.setLayoutParams(params);
        btnIrDia.setLayoutParams(params);

        if (objetivos.size() != 0) {
            for (int i = 0; i < objetivos.size(); i++) {
                Date date = new Date();
                try {
                    date = dateFormat.parse(objetivos.get(i).getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long fecha = date.getTime();
                Event event = new Event(Color.parseColor("#00FF00"), fecha, objetivos.get(i).getNombre());
                compactCalendarView.addEvent(event);
            }
        }

        if (entradas.size() != 0) {
            for (int i = 0; i < entradas.size(); i++) {
                Date date = new Date();
                try {
                    date = dateFormat.parse(entradas.get(i).getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long fecha = date.getTime();
                Event event = new Event(Color.parseColor("#0000FF"), fecha, entradas.get(i).getNombre());
                compactCalendarView.addEvent(event);

            }
        }

    }

    private void mesText() {
        Date firstDayOfMonth = compactCalendarView.getFirstDayOfCurrentMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfMonth);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        month++;
        String mes = monthFormat.format(calendar.getTime());
        String primeraLetraMayuscula = mes.substring(0, 1).toUpperCase();
        String restoDelTexto = mes.substring(1);
        String mesMayuscula = primeraLetraMayuscula + restoDelTexto;
        tvMesCal.setText(mesMayuscula + " / " + year);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnIrDia.getId()) {
            Date date = new Date();
            compactCalendarView.setCurrentDate(date);
            mesText();
            btnIrDia.setEnabled(false);
            fechaSelect(date);
        }
    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    private void comprovarFecha() {
        Date primerDia = compactCalendarView.getFirstDayOfCurrentMonth();
        Date date = new Date();
        if (date.getDate() == primerDia.getDate() && date.getMonth() == primerDia.getMonth() && date.getYear() == primerDia.getYear()) {
            btnIrDia.setEnabled(false);
        } else {
            btnIrDia.setEnabled(true);
        }
    }

    private void fechaSelect(Date dateClicked) {
        lyCalDatosObj.removeAllViews();
        lyCalDatosEnt.removeAllViews();

        AppDatabase db = AppDatabase.getDatabase(getContext());

        Date date = new Date();
        if (date.getDate() == dateClicked.getDate() && date.getMonth() == dateClicked.getMonth() && date.getYear() == dateClicked.getYear()) {
            btnIrDia.setEnabled(false);
        } else {
            btnIrDia.setEnabled(true);
        }

        String fecha = dateFormat.format(dateClicked);

        List<Objetivos> objetivos = db.objetivosDao().findByFecha(fecha);
        List<Entradas> entradas = db.entradasDao().findByFecha(fecha);
        if (objetivos.size() != 0) {
            tvObjetivosCal.setVisibility(View.VISIBLE);
            lyCalDatosObj.setVisibility(View.VISIBLE);
            for (int i = 0; i < objetivos.size(); i++) {

                LinearLayout lyObjetivos = new LinearLayout(getContext());
                lyObjetivos.setId(objetivos.get(i).getId());
                lyObjetivos.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 50, 30, 0);
                lyObjetivos.setLayoutParams(params);

                ImageView icono = new ImageView(getContext());
                LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsIcono.width = 0;
                paramsIcono.weight = 1;
                icono.setLayoutParams(paramsIcono);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    icono.setForegroundGravity(Gravity.CENTER);
                }
                switch (objetivos.get(i).getCategoria()) {
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

                TextView tvObjetivo = new TextView(getContext());
                LinearLayout.LayoutParams paramsObjetivo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsObjetivo.width = 0;
                paramsObjetivo.weight = 6;
                tvObjetivo.setLayoutParams(paramsObjetivo);
                tvObjetivo.setText(objetivos.get(i).getNombre());
                tvObjetivo.setGravity(Gravity.CENTER);

                TextView cantidad = new TextView(getContext());
                LinearLayout.LayoutParams paramsCantidad = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsCantidad.width = 0;
                paramsCantidad.weight = 4;
                cantidad.setText(obtieneDosDecimales(objetivos.get(i).getAhorrado()) + "€ / " + obtieneDosDecimales(objetivos.get(i).getCantidad()) + "€");
                cantidad.setGravity(Gravity.CENTER);

                lyObjetivos.addView(icono);
                lyObjetivos.addView(tvObjetivo);
                lyObjetivos.addView(cantidad);

                lyCalDatosObj.addView(lyObjetivos);

                lyObjetivos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("id", lyObjetivos.getId());
                        startActivity(intent);
                    }
                });
            }
        } else {
            tvObjetivosCal.setVisibility(View.GONE);
            lyCalDatosObj.setVisibility(View.GONE);
        }

        if (entradas.size() != 0) {
            tvEntradasCal.setVisibility(View.VISIBLE);
            lyCalDatosEnt.setVisibility(View.VISIBLE);
            for (int i = 0; i < entradas.size(); i++) {

                Objetivos objetivo = db.objetivosDao().findById(entradas.get(i).getIdObjetivos());

                LinearLayout lyEntradas = new LinearLayout(getContext());
                lyEntradas.setId(entradas.get(i).getIdObjetivos());
                lyEntradas.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 50, 30, 0);
                lyEntradas.setLayoutParams(params);

                ImageView icono = new ImageView(getContext());
                LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsIcono.width = 0;
                paramsIcono.weight = 1;
                icono.setLayoutParams(paramsIcono);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    icono.setForegroundGravity(Gravity.CENTER);
                }
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

                TextView tvEntrada = new TextView(getContext());
                LinearLayout.LayoutParams paramsEntradas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsEntradas.width = 0;
                paramsEntradas.weight = 6;
                tvEntrada.setLayoutParams(paramsEntradas);
                tvEntrada.setText(objetivo.getNombre() + " / " + entradas.get(i).getNombre());
                tvEntrada.setGravity(Gravity.CENTER);

                TextView cantidad = new TextView(getContext());
                LinearLayout.LayoutParams paramsCantidad = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsCantidad.width = 0;
                paramsCantidad.weight = 4;
                cantidad.setText(obtieneDosDecimales(entradas.get(i).getCantidad()) + "€");
                cantidad.setGravity(Gravity.CENTER);

                lyEntradas.addView(icono);
                lyEntradas.addView(tvEntrada);
                lyEntradas.addView(cantidad);

                lyCalDatosEnt.addView(lyEntradas);

                lyEntradas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("id", lyEntradas.getId());
                        intent.putExtra("tab", 2);
                        startActivity(intent);
                    }
                });
            }
        } else {
            tvEntradasCal.setVisibility(View.GONE);
            lyCalDatosEnt.setVisibility(View.GONE);
        }
    }
}