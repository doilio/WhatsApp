package com.example.dowy.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dowy.whatsapp.R;
import com.example.dowy.whatsapp.config.ConfiguracaoFirebase;
import com.example.dowy.whatsapp.helper.Permissao;
import com.example.dowy.whatsapp.helper.UsuarioFirebase;
import com.example.dowy.whatsapp.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String[] permissoesNecessarias = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private ImageButton camera;
    private ImageButton galeria;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView fotoDePerfil;
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private String identificadorUsuario;
    private EditText editPerfilNome;
    private ImageView nomePerfilBotao;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        Permissao.validarPermissoes(permissoesNecessarias, this, 1);
        camera = findViewById(R.id.imageButtonCamera);
        galeria = findViewById(R.id.imageButtonGallery);
        fotoDePerfil = findViewById(R.id.roundFotoDePerfil);
        editPerfilNome = findViewById(R.id.editPerfilNome);
        nomePerfilBotao = findViewById(R.id.nomePerfilBotao);

        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configuracoes");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar Dados do Usuario
        FirebaseUser usuario = UsuarioFirebase.getUsuarioActual();
        Uri url = usuario.getPhotoUrl();
        if (url != null) {
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(fotoDePerfil);
        } else {
            //Se nao tivermos imagem uploaded, ele exibe uma imagem padrao
            fotoDePerfil.setImageResource(R.drawable.padrao);
        }

        editPerfilNome.setText(usuario.getDisplayName());

        nomePerfilBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editPerfilNome.getText().toString().trim();
                boolean retorno = UsuarioFirebase.actualizarNomeUsuario(nome);
                if (retorno) {

                    usuarioLogado.setNome(nome);
                    usuarioLogado.actualizar();
                    Toast.makeText(ConfiguracoesActivity.this, "Nome Actualizado com sucesso", Toast.LENGTH_SHORT).show();
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Verificar se foi possivel abrir o intent
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Verificar se existe um software de galeria
                if (g.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(g, SELECAO_GALERIA);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imagem != null) {
                fotoDePerfil.setImageBitmap(imagem);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImagem = baos.toByteArray();

                //Salvar imagem no firebase
                final StorageReference imagemRef = storageReference
                        .child("imagens")
                        .child("perfil")
                        .child(identificadorUsuario + ".jpeg");

                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        //Continue with the task to get the download URL
                        return imagemRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                            Uri downloadUri = task.getResult();
                            actualizaFotoUsuario(downloadUri);
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao fazer upload da imagem!" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }
    }

    public void actualizaFotoUsuario(Uri url) {
        boolean retorno = UsuarioFirebase.actualizarFotoUsuario(url);
        if (retorno) {
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.actualizar();
        }
        Toast.makeText(ConfiguracoesActivity.this, "Foto Alterada", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Tratamos a permissao caso ela tenha sido negada
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar a app E necessario aceitar as permissoes.");
        builder.setCancelable(false);
        builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create();
        builder.show();
    }
}
