package com.savegoals.savegoals;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.TextoDialog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RespaldoActivity extends AppCompatActivity implements View.OnClickListener, TextoDialog.TextoDialogListener {

    FloatingActionButton btnAtras;
    TextView tvCuentaInfo, tvFechasInfo;
    Button btnCopiaSeg, btnRestaurar;
    AppDatabase dbLocal;
    FirebaseFirestore db;
    SharedPreferences settingssp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respaldo);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        dbLocal = AppDatabase.getDatabase(this);
        db = FirebaseFirestore.getInstance();

        btnAtras = findViewById(R.id.btnAtrasRes);
        tvCuentaInfo = findViewById(R.id.tvCuentaInfo);
        tvFechasInfo = findViewById(R.id.tvFechasInfo);
        btnCopiaSeg = findViewById(R.id.btnCopiaSeg);
        btnRestaurar = findViewById(R.id.btnRestaurar);

        btnAtras.setOnClickListener(this);
        btnCopiaSeg.setOnClickListener(this);
        btnRestaurar.setOnClickListener(this);

        tvCuentaInfo.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        // Meter info de fechas de copias de seguridad
        db.collection("copiaSeguridad").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        tvFechasInfo.setText(getString(R.string.no_hay_copias));
                        btnRestaurar.setEnabled(false);
                    } else {
                        btnRestaurar.setEnabled(true);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp fechaGet = (Timestamp) document.getData().get("fecha");
                            Date fecha = fechaGet.toDate();
                            Date hoy = new Date();
                            long diferencia = hoy.getTime() - fecha.getTime();
                            long segundos = diferencia / 1000;
                            long minutos = segundos / 60;
                            long horas = minutos / 60;
                            long dias = horas / 24;
                            long meses = dias / 30;
                            if (meses > 0) {
                                long diasRestantes = dias - (meses * 30);
                                if (meses == 1) {
                                    if (diasRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.mes_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dia_cop_seg));
                                    } else if (diasRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.mes_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.mes_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dias_cop_seg));
                                    }
                                } else {
                                    if (diasRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.meses_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dia_cop_seg));
                                    } else if (diasRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.meses_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.meses_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dias_cop_seg));
                                    }
                                }
                            } else if (dias > 0) {
                                if (dias == 1) {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + dias + getString(R.string.dia_cop_seg));
                                } else {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + dias + getString(R.string.dias_cop_seg));
                                }
                            } else if (horas > 0) {
                                long minutosRestantes = minutos - (horas * 60);
                                if (horas == 1) {
                                    if (minutosRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.hora_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minuto_cop_seg));
                                    } else if (minutosRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.hora_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.hora_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minutos_cop_seg));
                                    }
                                } else {
                                    if (minutosRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.horas_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minuto_cop_seg));
                                    } else if (minutosRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.horas_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.horas_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minutos_cop_seg));
                                    }
                                }
                            } else {
                                minutos = minutos == 0 ? 1 : minutos;
                                if (minutos == 1) {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + minutos + getString(R.string.minuto_cop_seg));
                                } else {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + minutos + getString(R.string.minutos_cop_seg));
                                }
                            }
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
        db.collection("copiaSeguridad").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        tvFechasInfo.setText(getString(R.string.no_hay_copias));
                        btnRestaurar.setEnabled(false);
                    } else {
                        btnRestaurar.setEnabled(true);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp fechaGet = (Timestamp) document.getData().get("fecha");
                            Date fecha = fechaGet.toDate();
                            Date hoy = new Date();
                            long diferencia = hoy.getTime() - fecha.getTime();
                            long segundos = diferencia / 1000;
                            long minutos = segundos / 60;
                            long horas = minutos / 60;
                            long dias = horas / 24;
                            long meses = dias / 30;
                            if (meses > 0) {
                                long diasRestantes = dias - (meses * 30);
                                if (meses == 1) {
                                    if (diasRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.mes_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dia_cop_seg));
                                    } else if (diasRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.mes_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.mes_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dias_cop_seg));
                                    }
                                } else {
                                    if (diasRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.meses_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dia_cop_seg));
                                    } else if (diasRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.meses_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + meses + getString(R.string.meses_cop_seg) + getString(R.string.y_cop_seg) + diasRestantes + getString(R.string.dias_cop_seg));
                                    }
                                }
                            } else if (dias > 0) {
                                if (dias == 1) {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + dias + getString(R.string.dia_cop_seg));
                                } else {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + dias + getString(R.string.dias_cop_seg));
                                }
                            } else if (horas > 0) {
                                long minutosRestantes = minutos - (horas * 60);
                                if (horas == 1) {
                                    if (minutosRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.hora_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minuto_cop_seg));
                                    } else if (minutosRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.hora_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.hora_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minutos_cop_seg));
                                    }
                                } else {
                                    if (minutosRestantes == 1) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.horas_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minuto_cop_seg));
                                    } else if (minutosRestantes == 0) {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.horas_cop_seg));
                                    } else {
                                        tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + horas + getString(R.string.horas_cop_seg) + getString(R.string.y_cop_seg) + minutosRestantes + getString(R.string.minutos_cop_seg));
                                    }
                                }
                            } else {
                                minutos = minutos == 0 ? 1 : minutos;
                                if (minutos == 1) {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + minutos + getString(R.string.minuto_cop_seg));
                                } else {
                                    tvFechasInfo.setText(getString(R.string.ultima_cop_seg) + minutos + getString(R.string.minutos_cop_seg));
                                }
                            }
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

        } else if (v.getId() == btnCopiaSeg.getId()) {
            eliminarDatosDB();

        } else if (v.getId() == btnRestaurar.getId()) {
            // Recuperar copia de seguridad
            TextoDialog dialog = new TextoDialog(getString(R.string.restaurar_copia), getString(R.string.restaurar_copia_titulo));
            dialog.show(getSupportFragmentManager(), "TextoDialog");

        }
    }

    private void eliminarDatosDB() {
        db.collection("entradas").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0;
                    if (task.getResult().isEmpty()) {
                        // Si no existe ninguna entrada
                        db.collection("objetivos").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int i = 0;
                                    if (task.getResult().isEmpty()) {
                                        // Si no existe ningun objetivo
                                        hacerCopia();
                                    } else {
                                        // Si existe algun objetivo
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            i++;
                                            if (task.getResult().size() == i) {
                                                db.collection("objetivos").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        hacerCopia();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        eliminarDatosDB();
                                                    }
                                                });
                                            } else {
                                                db.collection("objetivos").document(document.getId()).delete().addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        eliminarDatosDB();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        // Si existe alguna entrada
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            i++;
                            if (task.getResult().size() == i) {
                                db.collection("entradas").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("objetivos").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int i = 0;
                                                    if (task.getResult().isEmpty()) {
                                                        // Si no existe ningun objetivo
                                                        hacerCopia();
                                                    } else {
                                                        // Si existe algun objetivo
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            i++;
                                                            if (task.getResult().size() == i) {
                                                                db.collection("objetivos").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        hacerCopia();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        eliminarDatosDB();
                                                                    }
                                                                });
                                                            } else {
                                                                db.collection("objetivos").document(document.getId()).delete().addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        eliminarDatosDB();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });

                            } else {
                                db.collection("entradas").document(document.getId()).delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        eliminarDatosDB();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    private void hacerCopia() {
        List<Objetivos> objetivos = dbLocal.objetivosDao().getAll();

        for (Objetivos objetivo : objetivos) {
            Map<String, Object> data = new HashMap<>();
            data.put("ahorrado", objetivo.getAhorrado());
            data.put("cantidad", objetivo.getCantidad());
            data.put("categoria", objetivo.getCategoria());
            data.put("completado", objetivo.getCompletado());
            data.put("fecha", objetivo.getFecha());
            data.put("id", objetivo.getId());
            data.put("nombre", objetivo.getNombre());
            data.put("uid", settingssp.getString("uid", ""));
            data.put("archivado", objetivo.getArchivado());
            db.collection("objetivos").add(data);
        }

        List<Entradas> entradas = dbLocal.entradasDao().getAll();
        for (Entradas entrada : entradas) {
            Map<String, Object> data = new HashMap<>();
            data.put("cantidad", entrada.getCantidad());
            data.put("categoria", entrada.getCategoria());
            data.put("fecha", entrada.getFecha());
            data.put("idEntrada", entrada.getIdEntrada());
            data.put("idObjetivos", entrada.getIdObjetivos());
            data.put("nombre", entrada.getNombre());
            data.put("uid", settingssp.getString("uid", ""));
            db.collection("entradas").add(data);
        }
        actualizarFechaCopia();
    }

    private void actualizarFechaCopia() {
        db.collection("copiaSeguridad").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("fecha", new Timestamp(new Date()));
                        data.put("uid", settingssp.getString("uid", ""));
                        db.collection("copiaSeguridad").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                onResume();
                            }
                        });
                    } else {
                        db.collection("copiaSeguridad").document(task.getResult().getDocuments().get(0).getId()).update("fecha", new Timestamp(new Date())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                onResume();
                            }
                        });
                    }
                }
            }
        });
    }

    private void restaurar() {
        dbLocal.objetivosDao().deleteAll();
        dbLocal.entradasDao().deleteAll();

        db.collection("objetivos").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Objetivos objetivo = new Objetivos();
                        objetivo.setAhorrado(Float.parseFloat(document.getData().get("ahorrado").toString()));
                        objetivo.setCantidad(Float.parseFloat(document.getData().get("cantidad").toString()));
                        objetivo.setCategoria(Integer.parseInt(document.getData().get("categoria").toString()));
                        objetivo.setCompletado(Boolean.parseBoolean(document.getData().get("completado").toString()));
                        objetivo.setFecha(document.getData().get("fecha").toString());
                        objetivo.setId(Integer.parseInt(document.getData().get("id").toString()));
                        objetivo.setNombre(document.getData().get("nombre").toString());
                        if (document.getData().get("archivado") != null) {
                            objetivo.setArchivado(Boolean.parseBoolean(document.getData().get("archivado").toString()));
                        } else {
                            objetivo.setArchivado(false);
                        }
                        dbLocal.objetivosDao().insertAll(objetivo);
                    }
                }
            }
        });

        db.collection("entradas").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Entradas entrada = new Entradas();
                        entrada.setCantidad(Float.parseFloat(document.getData().get("cantidad").toString()));
                        entrada.setCategoria(Integer.parseInt(document.getData().get("categoria").toString()));
                        entrada.setFecha(document.getData().get("fecha").toString());
                        entrada.setIdEntrada(Integer.parseInt(document.getData().get("idEntrada").toString()));
                        entrada.setIdObjetivos(Integer.parseInt(document.getData().get("idObjetivos").toString()));
                        entrada.setNombre(document.getData().get("nombre").toString());
                        dbLocal.entradasDao().insertAll(entrada);
                    }
                }
            }
        });

        Toast.makeText(this, R.string.copia_restaurada, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogPositiveClickTextoDialog(DialogFragment dialog) {
        restaurar();
    }

    @Override
    public void onDialogNegativeClickTextoDialog(DialogFragment dialog) {

    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnAtras.setForegroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnCopiaSeg.setTextColor(Color.WHITE);
            btnRestaurar.setTextColor(Color.WHITE);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnAtras.setForegroundTintList(ColorStateList.valueOf(Color.BLACK));
            btnCopiaSeg.setTextColor(Color.WHITE);
            btnRestaurar.setTextColor(Color.WHITE);
        }
    }
}