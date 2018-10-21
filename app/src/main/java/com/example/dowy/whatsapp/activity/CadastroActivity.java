package com.example.dowy.whatsapp.activity;

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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText mNome;
    private TextInputEditText mEmail;
    private TextInputEditText mSenha;
    private Button mCadastro;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAuth();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        mNome = findViewById(R.id.cadastroNome);
        mEmail = findViewById(R.id.cadastroMail);
        mSenha = findViewById(R.id.cadastroSenha);
        mCadastro = findViewById(R.id.botaoCadastro);

        //Cadastrar Usuario
        mCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });
    }

    public void efectuarCadastro(Usuario usuario) {
            autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Usuario cadastrado", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {

                        String excepcao = "";

                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            excepcao = "Digite uma senha mais forte!";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            excepcao = "Digite um E-mail valido!";
                        } catch (FirebaseAuthUserCollisionException e) {
                            excepcao = "Esta conta ja foi cadastrada!";
                        } catch (Exception e) {
                            excepcao = "Erro ao Cadastrar Usuario: " + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), excepcao, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("CADASTRO", "Falha ao Registar Usuario: " + e.getMessage());
                }
            });

    }


    public void validarCampos() {
        String nome = mNome.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String senha = mSenha.getText().toString().trim();

        //Verificar se os campos estao vazios
        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {
                   Usuario usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);

                    efectuarCadastro(usuario);

                } else {
                    Toast.makeText(this, "Preencha senha!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha e-mail!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha nome!", Toast.LENGTH_SHORT).show();
        }

    }
}
