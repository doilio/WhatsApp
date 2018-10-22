package com.example.dowy.whatsapp.helper;

import com.example.dowy.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario() {

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }
}
