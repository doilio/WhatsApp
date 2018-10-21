package com.example.dowy.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dowy.whatsapp.R;
import com.example.dowy.whatsapp.config.ConfiguracaoFirebase;
import com.example.dowy.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mEmail;
    private TextInputEditText mSenha;
    private Button mLogin;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.loginMail);
        mSenha = findViewById(R.id.loginSenha);
        mLogin = findViewById(R.id.botaoLogin);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });

    }

    public void abrirTelaCadastro(View view) {
        Intent abrirCadastro = new Intent(this, CadastroActivity.class);
        startActivity(abrirCadastro);
    }

    public void validarCampos() {
        String email = mEmail.getText().toString().trim();
        String senha = mSenha.getText().toString().trim();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);
                logarUsuario(usuario);
            } else {
                Toast.makeText(this, "Insira E-mail!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Insira Senha!", Toast.LENGTH_SHORT).show();
        }
    }

    public void logarUsuario(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Usuario Logado", Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                } else {
                    String excepcao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excepcao = "Usuario nao Existe!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excepcao = "E-mail e senha nao correspondem a um usuario cadastrado!";
                    } catch (Exception e) {
                        excepcao = "Falha ao logar usuario:" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), excepcao, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("LOGIN", "Falha ao Logar Usuario" + e.getMessage());
            }
        });
    }

    public void abrirTelaPrincipal() {
        Intent abrirTelaPrincipal = new Intent(this, MainActivity.class);
        startActivity(abrirTelaPrincipal);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }
}
