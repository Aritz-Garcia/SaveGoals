package com.savegoals.savegoals.controlador.inicioSesion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.savegoals.savegoals.R;

public class CorreoInicioSesionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvRegistro, tvTitulo, tvOlvidarPass;
    EditText etEmail, etPass, etPass2;
    FloatingActionButton btnAtras;
    Button btnIniciarSesionIS;
    boolean inicioSesion = true;
    private FirebaseAuth mAuth;
    SharedPreferences settingssp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correo_inicio_sesion);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = settingssp.edit();

        btnAtras = findViewById(R.id.btnAtrasIs);
        tvTitulo = findViewById(R.id.tvTituloIS);
        etEmail = findViewById(R.id.etEmailIS);
        etPass = findViewById(R.id.etPassIS);
        etPass2 = findViewById(R.id.etPassIS2);
        btnIniciarSesionIS = findViewById(R.id.btnIniciarSesionIS);
        tvRegistro = findViewById(R.id.tvRegistroIS);
        tvOlvidarPass = findViewById(R.id.tvOlvidarPassIS);

        btnAtras.setOnClickListener(this);
        btnIniciarSesionIS.setOnClickListener(this);
        tvRegistro.setOnClickListener(this);
        tvOlvidarPass.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnAtras.getId()) {
            finish();
        } else if (view.getId() == btnIniciarSesionIS.getId()) {
            if (inicioSesion) {
                // Iniciar sesion
                iniciarSesion();
            } else {
                // Registrar
                registrar();
            }
        } else if (view.getId() == tvRegistro.getId()) {
            registro();
        } else if (view.getId() == tvOlvidarPass.getId()) {
            // Olvidar contraseña
            olvidarContrasena();
        }
    }

    private void iniciarSesion() {
        // Iniciar sesion
        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        if (leerString(email, etEmail) && leerString(password, etPass)) {
            // Iniciar sesion
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                editor.putString("uid", user.getUid());
                                editor.commit();
                                finish();
                            } else {
                                Toast.makeText(CorreoInicioSesionActivity.this, R.string.error_IS, Toast.LENGTH_SHORT).show();
                                editor.remove("uid");
                                editor.commit();
                            }
                        }
                    });
        }
    }

    private void registrar() {
        // Registrar
        mAuth = FirebaseAuth.getInstance();
        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();
        String password2 = etPass2.getText().toString();

        if (leerString(email, etEmail) && leerString(password, etPass) && leerString(password2, etPass2) && mismaContrasena(password, password2, etPass, etPass2)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                editor.putString("uid", user.getUid());
                                editor.commit();
                                finish();
                            } else {
                                Toast.makeText(CorreoInicioSesionActivity.this, R.string.error_IS, Toast.LENGTH_SHORT).show();
                                editor.remove("uid");
                                editor.commit();
                            }
                        }
                    });
        }
    }

    private void registro() {
        inicioSesion = false;
        btnIniciarSesionIS.setText(R.string.registrarse);
        tvTitulo.setText(R.string.title_is_registro);
        tvRegistro.setVisibility(View.GONE);
        tvOlvidarPass.setVisibility(View.GONE);
        etPass2.setVisibility(View.VISIBLE);
    }

    private boolean leerString(String textua, EditText text){
        if( textua.isEmpty() ){
            text.setError(getString(R.string.necesario));
            return false;
        } else{
            return true;
        }
    }

    private boolean mismaContrasena(String pass1, String pass2, EditText etPass, EditText etPass2){
        if( pass1.equals(pass2) ){
            return true;
        } else{
            etPass.setError(getString(R.string.contrasenas_no_coinciden));
            etPass2.setError(getString(R.string.contrasenas_no_coinciden));
            return false;
        }

    }

    private void olvidarContrasena() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = etEmail.getText().toString();
        if (emailAddress.isEmpty()) {
            etEmail.setError(getString(R.string.necesario));
        } else {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CorreoInicioSesionActivity.this, R.string.correo_olvidado_ok, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CorreoInicioSesionActivity.this, R.string.correo_olvidado_ko, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}