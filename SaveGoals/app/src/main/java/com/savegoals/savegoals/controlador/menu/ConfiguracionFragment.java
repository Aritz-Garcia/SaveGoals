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
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.AlarmNotification;
import com.savegoals.savegoals.R;

import java.util.Calendar;

public class ConfiguracionFragment extends Fragment implements View.OnClickListener {

    public static final String MY_CHANNEL_ID = "myChannel";
    SharedPreferences settingssp;
    SharedPreferences.Editor editor;
    Switch swOscuro, swNotificaciones;

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

        swNotificaciones = view.findViewById(R.id.swNotificacion);

        if (settingssp.getBoolean("notificacion", false)) {
            swNotificaciones.setChecked(true);
        } else {
            swNotificaciones.setChecked(false);
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
            if (swNotificaciones.isChecked()) {
                editor.putBoolean("notificacion", true);
                notificacion(0);
            } else {
                editor.putBoolean("notificacion", false);
                notificacion(1);
            }
            editor.commit();
        }
    }

    private void notificacion(int i) {
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
                // alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 3000, pendingIntent);
                // Notifiaciones todos los domingos a las 19:00
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                calendar.set(Calendar.HOUR_OF_DAY, 19);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            } else {
                alarmManager.cancel(pendingIntent);
            }

        }
        Log.d("TAG", "notificacion: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Log.d("TAG", "createChannel: ");
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
}