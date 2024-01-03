package com.savegoals.savegoals.controlador.menu;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.AlarmNotification;
import com.savegoals.savegoals.R;

import java.util.Calendar;

public class ConfiguracionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String MY_CHANNEL_ID = "myChannel";
    SharedPreferences settingssp;
    SharedPreferences.Editor editor;
    Switch swOscuro, swNotificaciones;
    EditText etNotiHora;
    Spinner spNotiSemana;
    Button btnGuardar;

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

        etNotiHora = view.findViewById(R.id.etNotiHora);
        etNotiHora.setText(settingssp.getString("hora", "00:00"));
        etNotiHora.setOnClickListener(this);

        spNotiSemana = view.findViewById(R.id.spinnerNotiSemana);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.opciones_spinner_semanal,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spNotiSemana != null) {
            spNotiSemana.setAdapter(adapter);
            spNotiSemana.setOnItemSelectedListener(this);
        }

        spNotiSemana.setSelection(settingssp.getInt("semana", 0));

        btnGuardar = view.findViewById(R.id.btnGuardarNoti);
        btnGuardar.setOnClickListener(this);

        swNotificaciones = view.findViewById(R.id.swNotificacion);

        if (settingssp.getBoolean("notificacion", false)) {
            swNotificaciones.setChecked(true);
            spNotiSemana.setVisibility(View.VISIBLE);
            etNotiHora.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
        } else {
            swNotificaciones.setChecked(false);
            spNotiSemana.setVisibility(View.GONE);
            etNotiHora.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
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
        } else if (v.getId() == etNotiHora.getId()) {
            mostrarDialogoHora();
        } else if (v.getId() == btnGuardar.getId()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificacion(0, spNotiSemana.getSelectedItemPosition(), etNotiHora.getText().toString());
            }
        }
    }

    private void notiSwitch() {
        if (swNotificaciones.isChecked()) {
            editor.putBoolean("notificacion", true);
            spNotiSemana.setVisibility(View.VISIBLE);
            etNotiHora.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificacion(0, spNotiSemana.getSelectedItemPosition(), etNotiHora.getText().toString());
            }
        } else {
            editor.putBoolean("notificacion", false);
            spNotiSemana.setVisibility(View.GONE);
            etNotiHora.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificacion(1, 0, "");
            }
        }
    }

    private void mostrarDialogoHora() {
        final Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog dialogoHora = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Manejar la hora seleccionada
                        String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                        etNotiHora.setText(horaSeleccionada);
                    }
                }, hora, minuto, true);

        dialogoHora.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void notificacion(int i, int semana, String hora) {
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
                editor.putInt("semana", semana);
                editor.putString("hora", hora);
                editor.commit();
                // alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 3000, pendingIntent);
                // Notifiaciones todos los domingos a las 19:00
                Calendar calendar = Calendar.getInstance();
                switch (semana) {
                    case 0:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        Log.d("TAG", "notificacion: lunes");
                        break;
                    case 1:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        Log.d("TAG", "notificacion: martes");
                        break;
                    case 2:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        Log.d("TAG", "notificacion: miercoles");
                        break;
                    case 3:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        Log.d("TAG", "notificacion: jueves");
                        break;
                    case 4:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                        Log.d("TAG", "notificacion: viernes");
                        break;
                    case 5:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        Log.d("TAG", "notificacion: sabado");
                        break;
                    case 6:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        Log.d("TAG", "notificacion: domingo");
                        break;
                }

                String horaSeparada[] = hora.split(":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaSeparada[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(horaSeparada[1]));
                calendar.set(Calendar.SECOND, 0);
                /* Quitar 7 dias a calendar */
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24 * 7, pendingIntent);
                Log.d("TAG", "notificacion: " + semana + " " + horaSeparada[0] + " " + horaSeparada[1]);
                Log.d("TAG", "notificacion: " + calendar.getTimeInMillis() + " " + calendar.getTimeZone());
            } else {
                alarmManager.cancel(pendingIntent);
                Log.d("TAG", "notificacion: cancelada");
            }

        }
        /*Log.d("TAG", "notificacion: ");*/
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}