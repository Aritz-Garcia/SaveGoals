package com.savegoals.savegoals;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CreditosActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_creditos;
    FloatingActionButton btnAtras;
    FirebaseFirestore db;
    SharedPreferences settingssp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        ll_creditos = findViewById(R.id.ll_creditos);
        btnAtras = findViewById(R.id.btnAtrasCreditos);

        btnAtras.setOnClickListener(this);

        ll_creditos.removeAllViews();

        db.collection("creditos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Toast.makeText(CreditosActivity.this, getString(R.string.error_creditos), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        for (int i = 0; i < task.getResult().size(); i++) {
                            TextView izena = new TextView(CreditosActivity.this);
                            izena.setText(task.getResult().getDocuments().get(i).get("nombre").toString());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            izena.setLayoutParams(params);
                            izena.setTextSize(16);
                            izena.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            izena.setTypeface(izena.getTypeface(), Typeface.BOLD);
                            izena.setPadding(3, 3, 3, 3);

                            ll_creditos.addView(izena);
                        }
                    }
                }
            }
        });

        setDayNight();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ll_creditos.removeAllViews();
        db.collection("creditos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Toast.makeText(CreditosActivity.this, getString(R.string.error_creditos), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        for (int i = 0; i < task.getResult().size(); i++) {
                            TextView izena = new TextView(CreditosActivity.this);
                            izena.setText(task.getResult().getDocuments().get(i).get("nombre").toString());
                            izena.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                            izena.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                            izena.setTextSize(16);
                            izena.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            izena.setTypeface(izena.getTypeface(), Typeface.BOLD);
                            izena.setPadding(3, 3, 3, 3);

                            ll_creditos.addView(izena);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAtras.getId()) {
            finish();
        }
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnAtras.setForegroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnAtras.setForegroundTintList(ColorStateList.valueOf(Color.BLACK));
        }
    }
}