package com.example.dowy.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference database;
    private static StorageReference storage;

    /**
     * Retorna Instancia do FirebaseAuth
     */
    public static FirebaseAuth getFirebaseAuth() {

        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();

        }
        return autenticacao;
    }

    /**
     * Retorna Instancia do FirebaseDatabase
     */
    public static DatabaseReference getFirebaseDatabase() {

        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();

        }
        return database;
    }

    /**
     * Retorna Instancia do FirebaseStorage
     */
    public static StorageReference getFirebaseStorage(){

        if(storage == null){
             storage = FirebaseStorage.getInstance().getReference();

        }
        return storage;
    }
}
