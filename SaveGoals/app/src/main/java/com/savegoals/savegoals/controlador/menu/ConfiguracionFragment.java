package com.savegoals.savegoals.controlador.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.savegoals.savegoals.AlarmNotification;
import com.savegoals.savegoals.CuentaActivity;
import com.savegoals.savegoals.R;
import com.savegoals.savegoals.RespaldoActivity;
import com.savegoals.savegoals.controlador.inicioSesion.CorreoInicioSesionActivity;

import java.util.Calendar;

public class ConfiguracionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String MY_CHANNEL_ID = "meterDinero";
    SharedPreferences settingssp;
    SharedPreferences.Editor editor;
    Switch swOscuro, swNotificaciones;
    Spinner spNotiHora;
    View vwLineaNoti, vwLineaCuenta, vwLineaRespaldo;
    TextView tvISGoogle, tvISCorreo, tvCorreoText, tvISTitulo, tvRespaldo, tvCuentaIS;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public ConfiguracionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingssp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = settingssp.edit();
        setDayNight();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @SuppressLint({"UseCompatTextViewDrawableApis", "UseCompatLoadingForColorStateLists"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        swOscuro = view.findViewById(R.id.swNoche);
        tvISGoogle = view.findViewById(R.id.tvISGoogle);
        tvISCorreo = view.findViewById(R.id.tvISCorreo);
        tvCorreoText = view.findViewById(R.id.tvCorreoTextIS);
        tvISTitulo = view.findViewById(R.id.tvISTit);
        tvRespaldo = view.findViewById(R.id.tvRespaldoTextIS);
        tvCuentaIS = view.findViewById(R.id.tvCuentaIS);

        if (settingssp.getBoolean("oscuro", false)) {
            swOscuro.setChecked(true);
            tvISGoogle.setCompoundDrawableTintList(getResources().getColorStateList(R.color.white));
            tvISCorreo.setCompoundDrawableTintList(getResources().getColorStateList(R.color.white));
            tvRespaldo.setCompoundDrawableTintList(getResources().getColorStateList(R.color.white));
            tvCuentaIS.setCompoundDrawableTintList(getResources().getColorStateList(R.color.white));

        } else {
            swOscuro.setChecked(false);
            tvISGoogle.setCompoundDrawableTintList(getResources().getColorStateList(R.color.black));
            tvISCorreo.setCompoundDrawableTintList(getResources().getColorStateList(R.color.black));
            tvRespaldo.setCompoundDrawableTintList(getResources().getColorStateList(R.color.black));
            tvCuentaIS.setCompoundDrawableTintList(getResources().getColorStateList(R.color.black));

        }

        swOscuro.setOnClickListener(this);

        spNotiHora = view.findViewById(R.id.spinnerNotiHora);
        vwLineaNoti = view.findViewById(R.id.vwLineaNoti);
        vwLineaCuenta = view.findViewById(R.id.vwLineaCuenta);
        vwLineaRespaldo = view.findViewById(R.id.vwLineaRespaldo);

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
            vwLineaNoti.setVisibility(View.VISIBLE);
        } else {
            swNotificaciones.setChecked(false);
            spNotiHora.setVisibility(View.GONE);
            vwLineaNoti.setVisibility(View.GONE);
        }

        swNotificaciones.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }


        tvISGoogle.setOnClickListener(this);
        tvISCorreo.setOnClickListener(this);
        tvRespaldo.setOnClickListener(this);
        tvCuentaIS.setOnClickListener(this);

        if (settingssp.getString("uid", "").isEmpty()) {
            // SIN INICIAR SESION
            tvISTitulo.setText(R.string.iniciar_sesion);
            tvISCorreo.setVisibility(View.VISIBLE);
            tvISGoogle.setVisibility(View.VISIBLE);
            tvCorreoText.setVisibility(View.GONE);
            vwLineaCuenta.setVisibility(View.GONE);
            tvRespaldo.setVisibility(View.GONE);
            vwLineaRespaldo.setVisibility(View.GONE);
            tvCuentaIS.setVisibility(View.GONE);
        } else {
            // INICIADO SESION
            tvISTitulo.setText(R.string.cuenta);
            tvISCorreo.setVisibility(View.GONE);
            tvISGoogle.setVisibility(View.GONE);
            tvCorreoText.setVisibility(View.VISIBLE);
            vwLineaCuenta.setVisibility(View.VISIBLE);
            tvRespaldo.setVisibility(View.VISIBLE);
            vwLineaRespaldo.setVisibility(View.VISIBLE);
            tvCuentaIS.setVisibility(View.VISIBLE);

            tvCorreoText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (settingssp.getString("uid", "").isEmpty()) {
            // SIN INICIAR SESION
            tvISTitulo.setText(R.string.iniciar_sesion);
            tvISCorreo.setVisibility(View.VISIBLE);
            tvISGoogle.setVisibility(View.VISIBLE);
            tvCorreoText.setVisibility(View.GONE);
            vwLineaCuenta.setVisibility(View.GONE);
            tvRespaldo.setVisibility(View.GONE);
            vwLineaRespaldo.setVisibility(View.GONE);
            tvCuentaIS.setVisibility(View.GONE);
        } else {
            // INICIADO SESION
            tvISTitulo.setText(R.string.cuenta);
            tvISCorreo.setVisibility(View.GONE);
            tvISGoogle.setVisibility(View.GONE);
            tvCorreoText.setVisibility(View.VISIBLE);
            vwLineaCuenta.setVisibility(View.VISIBLE);
            tvRespaldo.setVisibility(View.VISIBLE);
            vwLineaRespaldo.setVisibility(View.VISIBLE);
            tvCuentaIS.setVisibility(View.VISIBLE);

            tvCorreoText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
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

        } else if (v.getId() == tvISGoogle.getId()) {
            iniciarSesionGoogle();

        } else if (v.getId() == tvISCorreo.getId()) {
            Intent intent = new Intent(getContext(), CorreoInicioSesionActivity.class);
            startActivity(intent);

        } else if (v.getId() == tvRespaldo.getId()) {
            Intent intent = new Intent(getContext(), RespaldoActivity.class);
            startActivity(intent);

        } else if (v.getId() == tvCuentaIS.getId()) {
            Intent intent = new Intent(getContext(), CuentaActivity.class);
            startActivity(intent);

        }
    }

    private void iniciarSesionGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInARL.launch(signInIntent);
    }

    private void notiSwitch() {
        if (swNotificaciones.isChecked()) {
            editor.putBoolean("notificacion", true);
            spNotiHora.setVisibility(View.VISIBLE);
            notificacion(0, spNotiHora.getSelectedItemPosition());
        } else {
            editor.putBoolean("notificacion", false);
            spNotiHora.setVisibility(View.GONE);
            notificacion(1, 0);
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
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
            notificacion(0, spNotiHora.getSelectedItemPosition());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private final ActivityResultLauncher<Intent> googleSignInARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount cuenta = task.getResult(ApiException.class);
                            autenticarCredencialGoogle(cuenta.getIdToken());
                        } catch (ApiException e) {
                            Toast.makeText(getContext(), R.string.error_IS, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    private void autenticarCredencialGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            editor.putString("uid", user.getUid());
                            editor.commit();
                            onResume();
                        } else {
                            Toast.makeText(getContext(), R.string.error_IS, Toast.LENGTH_SHORT).show();
                            editor.remove("uid");
                            editor.commit();
                        }
                    }
                });
    }
}