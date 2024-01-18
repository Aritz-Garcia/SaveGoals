package com.savegoals.savegoals.controlador.estObjetivos;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class EstEvolucionFragment extends Fragment {

    TextView tvPorcentaje, tvAhorrado, tvPendiente, tvTotal, tvAhorradotxt, tvPendientetxt, tvTotaltxt, tvError, tvText,
            tvDiatxt, tvSemanatxt, tvMestxt, tvDia, tvSemana, tvMes;
    ProgressBar progressBar;

    public EstEvolucionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_est_evolucion, container, false);

        tvPorcentaje = view.findViewById(R.id.tvPorcentajeEst);
        tvAhorrado = view.findViewById(R.id.tvAhorradoEst);
        tvPendiente = view.findViewById(R.id.tvPendienteEst);
        tvTotal = view.findViewById(R.id.tvTotalEst);
        progressBar = view.findViewById(R.id.progressBarEst);
        tvAhorradotxt = view.findViewById(R.id.tvAhorradotxtEst);
        tvPendientetxt = view.findViewById(R.id.tvPendientetxtEst);
        tvTotaltxt = view.findViewById(R.id.tvTotaltxtEst);
        tvError = view.findViewById(R.id.tvErrorEstEvolucion);
        tvText = view.findViewById(R.id.tvTextObjTotal);
        tvDiatxt = view.findViewById(R.id.tvDiatxtObjTotal);
        tvSemanatxt = view.findViewById(R.id.tvSemanatxtObjTotal);
        tvMestxt = view.findViewById(R.id.tvMestxtObjTotal);
        tvDia = view.findViewById(R.id.tvDiaObjTotal);
        tvSemana = view.findViewById(R.id.tvSemanaObjTotal);
        tvMes = view.findViewById(R.id.tvMesObjTotal);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        progressBar.setProgress(1);
    }

    @Override
    public void onResume() {
        super.onResume();

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        List<Objetivos> objetivos = appDatabase.objetivosDao().getAll();
        float total = 0;
        float ahorrado = 0;
        float pendiente;
        int porcentaje;

        if (objetivos.size() != 0) {
            tvError.setVisibility(View.GONE);
            tvPorcentaje.setVisibility(View.VISIBLE);
            tvAhorrado.setVisibility(View.VISIBLE);
            tvPendiente.setVisibility(View.VISIBLE);
            tvTotal.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tvAhorradotxt.setVisibility(View.VISIBLE);
            tvPendientetxt.setVisibility(View.VISIBLE);
            tvTotaltxt.setVisibility(View.VISIBLE);
            tvText.setVisibility(View.VISIBLE);
            tvDiatxt.setVisibility(View.VISIBLE);
            tvSemanatxt.setVisibility(View.VISIBLE);
            tvMestxt.setVisibility(View.VISIBLE);
            tvDia.setVisibility(View.VISIBLE);
            tvSemana.setVisibility(View.VISIBLE);
            tvMes.setVisibility(View.VISIBLE);

            for (int i = 0; i < objetivos.size(); i++) {
                total = total + objetivos.get(i).getCantidad();
                ahorrado = ahorrado + objetivos.get(i).getAhorrado();
            }

            pendiente = total - ahorrado;

            if (ahorrado == 0) {
                porcentaje = 1;
            } else {
                porcentaje = (int) ((ahorrado * 100) / total);
            }

            if (porcentaje < 50) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_rojo));
            } else if (porcentaje < 75) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_amarillo));
            } else if (porcentaje < 100) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_verde));
            } else {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle_completado));
            }
            progressBar.setProgress(porcentaje);

            tvPorcentaje.setText(porcentaje + "%");

            if (ahorrado == 0) {
                porcentaje = 0;
                progressBar.setProgress(porcentaje);
                tvPorcentaje.setText(porcentaje + "%");
            }

            tvAhorrado.setText(obtieneDosDecimales(ahorrado) + "€");
            tvPendiente.setText(obtieneDosDecimales(pendiente) + "€");
            tvTotal.setText(obtieneDosDecimales(total) + "€");

            List<Objetivos> objetivosSinTerminar = appDatabase.objetivosDao().getAllNotCompleted();
            float dia = 0;
            float semana = 0;
            float mes = 0;

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Date today = new Date();
            today.setHours(0);
            today.setMinutes(0);
            today.setSeconds(0);
            Date fechaFin = null;
            for (int i = 0; i < objetivosSinTerminar.size(); i++) {
                try {
                    fechaFin = formato.parse(objetivosSinTerminar.get(i).getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                float pendienteSinTerminar = objetivosSinTerminar.get(i).getCantidad() - objetivosSinTerminar.get(i).getAhorrado();

                if (leerFecha(today, fechaFin)) {
                    int dias = getDias(today, fechaFin);
                    int semanas = getSemanas(today, fechaFin);
                    int meses = getMeses(today, fechaFin);

                    dia = dia + pendienteSinTerminar / dias;
                    semana = semana + pendienteSinTerminar / semanas;
                    mes = mes + pendienteSinTerminar / meses;

                    tvDia.setText(obtieneDosDecimales(dia) + "€");
                    tvSemana.setText(obtieneDosDecimales(semana) + "€");
                    tvMes.setText(obtieneDosDecimales(mes) + "€");
                }
            }


        } else {
            tvPorcentaje.setVisibility(View.GONE);
            tvAhorrado.setVisibility(View.GONE);
            tvPendiente.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            tvAhorradotxt.setVisibility(View.GONE);
            tvPendientetxt.setVisibility(View.GONE);
            tvTotaltxt.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            tvDiatxt.setVisibility(View.GONE);
            tvSemanatxt.setVisibility(View.GONE);
            tvMestxt.setVisibility(View.GONE);
            tvDia.setVisibility(View.GONE);
            tvSemana.setVisibility(View.GONE);
            tvMes.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
        }

    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    private int getDias(Date hoy, Date fechaFin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate hoyLocal = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return (int) ChronoUnit.DAYS.between(hoyLocal, fechaFinLocal);
        }
        return 1;
    }

    private int getSemanas(Date hoy, Date fechaFin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate hoyLocal = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long dias = ChronoUnit.DAYS.between(hoyLocal, fechaFinLocal);
            if (dias > 7) {
                return (int) (dias / 7);
            }
        }
        return 1;
    }

    private int getMeses(Date hoy, Date fechaFin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate hoyLocal = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFinLocal = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long dias = ChronoUnit.DAYS.between(hoyLocal, fechaFinLocal);
            if (dias > 30) {
                return (int) (dias / 30);
            }
        }
        return 1;
    }

    private boolean leerFecha(Date today, Date fechaFin){

        if (fechaFin.getDay() == today.getDay() && fechaFin.getMonth() == today.getMonth() && fechaFin.getYear() == today.getYear()) {
            return true;
        } else if (fechaFin.before(today)) {
            return false;
        } else {
            return true;
        }
    }

}