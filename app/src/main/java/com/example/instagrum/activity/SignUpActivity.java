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
import com.example.instagrum.helper.UserFirebase;
import com.example.instagrum.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity {

    private EditText fieldUser, fieldEmail, fieldPassword;
    private Button btnCadastrar;
    private ProgressBar progressBar;

    private User user;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeComponents();

        // Cadastrar Users
        progressBar.setVisibility(View.GONE);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoNome    = fieldUser.getText().toString();
                String textoEmail   = fieldEmail.getText().toString();
                String textoSenha   = fieldPassword.getText().toString();

                if ( !textoNome.isEmpty() ){
                    if ( !textoEmail.isEmpty() ){
                        if ( !textoSenha.isEmpty() ){

                            user = new User();
                            user.setName( textoNome );
                            user.setEmail( textoEmail );
                            user.setPassword( textoSenha );

                            // Salvando o obj usuário
                            signup( user );

                        } else {
                            Toast.makeText( SignUpActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        Toast.makeText( SignUpActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( SignUpActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT ).show();
                }

            }
        });

    }

    // Método responsável por cadastrar usuário com e-mail e senha e fazer a validação ao realizar o cadastro
    public void signup(final User user ) {

        progressBar.setVisibility( View.VISIBLE );
        auth = ConfigFirebase.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if ( task.isSuccessful() ){



                            try {

                                progressBar.setVisibility( View.GONE );

                                // Salvar dados no firebase
                                String idUser = task.getResult().getUser().getUid();
                                user.setId( idUser );
                                user.save();

                                // Salvar dados no profile do firebase
                                UserFirebase.attNameUser( user.getName() );

                                Toast.makeText( SignUpActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT ).show();

                                startActivity( new Intent(getApplicationContext(), MainActivity.class) );
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        } else {

                            progressBar.setVisibility( View.GONE );

                            String erroExcecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "Por favor, digite um e-mail válido!";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExcecao = "Esta conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText( SignUpActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_SHORT ).show();

                        }

                    }
                }
        );

    }

    public void initializeComponents() {

        fieldUser       = findViewById(R.id.txtCadastroNome);
        fieldEmail      = findViewById(R.id.txtCadastroEmail);
        fieldPassword   = findViewById(R.id.txtCadastroPassword);
        btnCadastrar    = findViewById(R.id.buttonSignUp);
        progressBar     = findViewById(R.id.progressSignUp);

    }

}