package com.savegoals.savegoals;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.savegoals.savegoals.controlador.inicioSesion.EliminarCuentaDialog;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.TextoDialog;

import java.util.ArrayList;
import java.util.List;

public class CuentaActivity extends AppCompatActivity implements View.OnClickListener, EliminarCuentaDialog.EliminarCuentaDialogListener, TextoDialog.TextoDialogListener {

    FloatingActionButton btnAtras;
    TextView tvCuentaCorreo;
    Button btnCerrarSesion, btnEliminarCuenta;
    SharedPreferences settingssp;
    AppDatabase dbLocal;
    boolean errorEliminar = false;
    boolean eliminar = false;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        dbLocal = AppDatabase.getDatabase(this);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnAtras = findViewById(R.id.btnAtrasCuenta);
        tvCuentaCorreo = findViewById(R.id.tvCuentaCorreo);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        tvCuentaCorreo.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        btnAtras.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(this);
        btnEliminarCuenta.setOnClickListener(this);

        setDayNight();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAtras.getId()) {
            finish();

        } else if (v.getId() == btnCerrarSesion.getId()) {
            TextoDialog dialog = new TextoDialog(getString(R.string.cerrar_sesion_dialog), getString(R.string.cerrar_sesion));
            dialog.show(getSupportFragmentManager(), "TextoDialog");

        } else if (v.getId() == btnEliminarCuenta.getId()) {
            EliminarCuentaDialog dialog = new EliminarCuentaDialog();
            dialog.show(getSupportFragmentManager(), "EliminarCuentaDialog");

        }
    }

    private void eliminarBD() {
        // Eliminar base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Integer> idsObj = new ArrayList<>();
        db.collection("objetivos").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        idsObj.add(document.getLong("id").intValue());
                    }
                    for (int i = 0; i < idsObj.size(); i++) {
                        db.collection("entradas").whereEqualTo("idObjetivos", idsObj.get(i)).whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (!errorEliminar) {
                                            db.collection("entradas").document(document.getId()).delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(CuentaActivity.this, R.string.error_eliminar_cuenta, Toast.LENGTH_SHORT).show();
                                                            errorEliminar = true;
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        });

                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!errorEliminar) {
                            db.collection("objetivos").document(document.getId()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CuentaActivity.this, R.string.error_eliminar_cuenta, Toast.LENGTH_SHORT).show();
                                            errorEliminar = true;
                                        }
                                    });;
                        }
                    }
                }
            }
        });

        db.collection("copiaSeguridad").whereEqualTo("uid", settingssp.getString("uid", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        if (!errorEliminar) {
                            eliminarCuenta();
                        }
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!errorEliminar) {
                            db.collection("copiaSeguridad").document(document.getId()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (!errorEliminar) {
                                                eliminarCuenta();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CuentaActivity.this, R.string.error_eliminar_cuenta, Toast.LENGTH_SHORT).show();
                                            errorEliminar = true;
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    private void eliminarCuenta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CuentaActivity.this, R.string.text_toast_eliminar_cuenta, Toast.LENGTH_SHORT).show();
                    settingssp.edit().remove("uid").commit();
                    mGoogleSignInClient.signOut();
                    TextoDialog dialog1 = new TextoDialog(getString(R.string.eliminar_disp_dialog), getString(R.string.eliminar_disp_dialog_title));
                    dialog1.show(getSupportFragmentManager(), "TextoDialog");
                } else {
                    Toast.makeText(CuentaActivity.this, R.string.error_eliminar_cuenta, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        eliminar = true;
        errorEliminar = false;
        eliminarBD();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public void onDialogPositiveClickTextoDialog(DialogFragment dialog) {
        if (eliminar) {
            dbLocal.objetivosDao().deleteAll();
            dbLocal.entradasDao().deleteAll();
            finish();
        } else {
            eliminar = true;
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut();
            settingssp.edit().remove("uid").commit();
            TextoDialog dialog1 = new TextoDialog(getString(R.string.eliminar_disp_dialog), getString(R.string.eliminar_disp_dialog_title));
            dialog1.show(getSupportFragmentManager(), "TextoDialog");
        }

    }

    @Override
    public void onDialogNegativeClickTextoDialog(DialogFragment dialog) {
        if (eliminar) {
            finish();
        }
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnAtras.setForegroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnCerrarSesion.setTextColor(Color.WHITE);
            btnEliminarCuenta.setTextColor(Color.WHITE);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnAtras.setForegroundTintList(ColorStateList.valueOf(Color.BLACK));
            btnCerrarSesion.setTextColor(Color.WHITE);
            btnEliminarCuenta.setTextColor(Color.WHITE);
        }
    }
}