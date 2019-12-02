package com.example.instagrum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagrum.R;
import com.example.instagrum.helper.ConfigFirebase;
import com.example.instagrum.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private Button btnEntrar;
    private ProgressBar progressBar;

    private User user;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verifyUserLogin();
        initializeComponents();

        // Fazer login do usuario
        progressBar.setVisibility(View.GONE);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail   = fieldEmail.getText().toString();
                String textoSenha   = fieldPassword.getText().toString();

                if ( !textoEmail.isEmpty() ){
                    if ( !textoSenha.isEmpty() ){

                        user = new User();
                        user.setEmail(textoEmail);
                        user.setPassword(textoSenha);
                        validateLogin( user );

                    } else {
                        Toast.makeText( LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT ).show();
                    }

                } else {
                    Toast.makeText( LoginActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT ).show();
                }

            }
        });

    }

    public void verifyUserLogin() {
        auth = ConfigFirebase.getFirebaseAuth();
        if ( auth.getCurrentUser() != null ) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validateLogin( User user ) {

        progressBar.setVisibility(View.VISIBLE);
        auth = ConfigFirebase.getFirebaseAuth();

        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ) {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText( LoginActivity.this, "Erro ao fazer o login!", Toast.LENGTH_SHORT ).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

    }

    public void openSignUp(View view) {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity( i );
    }

    public void initializeComponents() {

        fieldEmail      = findViewById(R.id.txtEmail);
        fieldPassword   = findViewById(R.id.txtPassword);
        btnEntrar       = findViewById(R.id.buttonLogin);
        progressBar     = findViewById(R.id.progressLogin);

        fieldEmail.requestFocus();

    }
}
