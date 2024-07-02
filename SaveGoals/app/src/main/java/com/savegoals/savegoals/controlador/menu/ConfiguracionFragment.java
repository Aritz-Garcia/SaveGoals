package com.savegoals.savegoals.controlador.menu;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.AlarmNotification;
import com.savegoals.savegoals.R;

import java.util.Calendar;

public class ConfiguracionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String MY_CHANNEL_ID = "meterDinero";
    SharedPreferences settingssp;
    SharedPreferences.Editor editor;
    Switch swOscuro, swNotificaciones;
    Spinner spNotiHora;

    public ConfiguracionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = settingssp.edit();
        setDayNight();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        swOscuro = view.findViewById(R.id.swNoche);
        if (settingssp.getBoolean("oscuro", false)) {
            swOscuro.setChecked(true);
        } else {
            swOscuro.setChecked(false);
        }

        swOscuro.setOnClickListener(this);

        spNotiHora = view.findViewById(R.id.spinnerNotiHora);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.opciones_spinner_horas,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spNotiHora != null) {
            spNotiHora.setAdapter(adapter);
            spNotiHora.setOnItemSelectedListener(this);
        }

        spNotiHora.setSelection(settingssp.getInt("hora", 0));

        swNotificaciones = view.findViewById(R.id.swNotificacion);

        if (settingssp.getBoolean("notificacion", false)) {
            swNotificaciones.setChecked(true);
            spNotiHora.setVisibility(View.VISIBLE);
        } else {
            swNotificaciones.setChecked(false);
            spNotiHora.setVisibility(View.GONE);
        }

        swNotificaciones.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }


        return view;
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == swOscuro.getId()) {
            guardarSettings();
        } else if (v.getId() == swNotificaciones.getId()) {
            notiSwitch();
            editor.commit();
        }
    }

    private void notiSwitch() {
        if (swNotificaciones.isChecked()) {
            editor.putBoolean("notificacion", true);
            spNotiHora.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificacion(0, spNotiHora.getSelectedItemPosition());
            }
        } else {
            editor.putBoolean("notificacion", false);
            spNotiHora.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificacion(1, 0);
            }
        }
    }


    private void notificacion(int i, int hora) {
        Intent intent = new Intent(requireContext().getApplicationContext(), AlarmNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext().getApplicationContext(),
                AlarmNotification.NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (i == 0) {
                editor.putInt("hora", hora);
                editor.commit();
                // alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 3000, pendingIntent);
                Calendar calendar = Calendar.getInstance();
                Calendar hoy = Calendar.getInstance();
                hoy.setTimeZone(Calendar.getInstance().getTimeZone());
                calendar.setTimeZone(Calendar.getInstance().getTimeZone());
                switch (hora) {
                    case 0:
                        // 17:00
                        calendar.set(Calendar.HOUR_OF_DAY, 17);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                    case 1:
                        // 18:00
                        calendar.set(Calendar.HOUR_OF_DAY, 18);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                    case 2:
                        // 19:00
                        calendar.set(Calendar.HOUR_OF_DAY, 19);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                    case 3:
                        // 20:00
                        calendar.set(Calendar.HOUR_OF_DAY, 20);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                    case 4:
                        // 21:00
                        calendar.set(Calendar.HOUR_OF_DAY, 21);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                    case 5:
                        // 22:00
                        calendar.set(Calendar.HOUR_OF_DAY, 22);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                    case 6:
                        // 23:00
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;
                }
                if (hoy.before(calendar)) {
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    Log.d("TAG", "notificacion: Sin sumar");
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    Log.d("TAG", "notificacion: Sumando");
                }

                Log.d("TAG", "notificacion: " + hora);
                Log.d("TAG", "notificacion: " + calendar.getTimeInMillis() + " " + calendar.getTimeZone());
            } else {
                alarmManager.cancel(pendingIntent);
                Log.d("TAG", "notificacion: cancelada");
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(
                MY_CHANNEL_ID,
                "Meter Dinero",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void guardarSettings() {
        if (swOscuro.isChecked()) {
            editor.putBoolean("oscuro", true);
        } else {
            editor.putBoolean("oscuro", false);
        }
        editor.commit();
        setDayNight();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (swNotificaciones.isChecked()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificacion(0, spNotiHora.getSelectedItemPosition());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}