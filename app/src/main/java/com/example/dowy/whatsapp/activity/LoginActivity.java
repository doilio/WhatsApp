package com.example.dowy.whatsapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.dowy.whatsapp.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void abrirTelaCadastro(View view) {
        Intent abrirCadastro = new Intent(this, CadastroActivity.class);
        startActivity(abrirCadastro);
    }
}
