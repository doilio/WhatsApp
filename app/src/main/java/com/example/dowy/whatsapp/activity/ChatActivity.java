package com.example.dowy.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dowy.whatsapp.R;
import com.example.dowy.whatsapp.adapter.MensagensAdapter;
import com.example.dowy.whatsapp.config.ConfiguracaoFirebase;
import com.example.dowy.whatsapp.helper.Base64Custom;
import com.example.dowy.whatsapp.helper.UsuarioFirebase;
import com.example.dowy.whatsapp.model.Conversa;
import com.example.dowy.whatsapp.model.Grupo;
import com.example.dowy.whatsapp.model.Mensagem;
import com.example.dowy.whatsapp.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int SELECAO_CAMERA = 100;
    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;
    private ImageView imageCamera;
    private Grupo grupo;

    // ID dos Usuarios
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Config Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Config Initial
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        textViewNome = findViewById(R.id.textViewNomeChat);
        editMensagem = findViewById(R.id.editMensagem);
        imageCamera = findViewById(R.id.imageCamera);

        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        // Configurar Adapter
        adapter = new MensagensAdapter(mensagens, ChatActivity.this);

        // Configurar RecyclerMensagens
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);

        // Recuperar remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();


        //Dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("chatGrupo")) {

                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();
                textViewNome.setText(grupo.getNome());

                String foto = grupo.getFoto();

                if (foto != null) {
                    Uri url = Uri.parse(foto);
                    Glide.with(ChatActivity.this).load(url).into(circleImageViewFoto);

                } else {
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }


            } else {
                /*********/
                usuarioDestinatario = (Usuario) bundle.getSerializable("chatContacto");
                textViewNome.setText(usuarioDestinatario.getNome());
                String foto = usuarioDestinatario.getFoto();

                if (foto != null) {
                    Uri url = Uri.parse(foto);
                    Glide.with(ChatActivity.this).load(url).into(circleImageViewFoto);

                } else {
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }

                //Recuperar destinatario
                idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());

                /*********/
            }

        }


        database = ConfiguracaoFirebase.getFirebaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });
    }

    public void enviarMensagem(View view) {

        String textoMensagem = editMensagem.getText().toString().trim();

        if (!textoMensagem.isEmpty()) {

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente); // ID do usuario Logado
            mensagem.setMensagem(textoMensagem);
            //mensagem.setImagem();

            //Salvar a mensagem para remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

            //Salvar a mensagem para destinatario
            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

            //Salvar conversa
            salvarConversa(mensagem);

        } else {
            Toast.makeText(ChatActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarConversa(Mensagem msg) {

        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setUsuarioExibicao(usuarioDestinatario);

        conversaRemetente.salvar();

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem) {

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        mensagensRef = database.child("mensagens");

        mensagensRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(mensagem);

        //Limpar texto
        editMensagem.setText("");
    }

    private void recuperarMensagens() {
        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imagem = null;

        try {

            if (requestCode == SELECAO_CAMERA && resultCode == RESULT_OK) {

                imagem = (Bitmap) data.getExtras().get("data");
            }

            if (imagem != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImagem = baos.toByteArray();

                String nomeImagem = UUID.randomUUID().toString();
                final StorageReference imagemRef = storage.child("imagens")
                        .child("fotos")
                        .child(idUsuarioRemetente)
                        .child(nomeImagem);

                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imagemRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatActivity.this, "Sucesso ao enviar imagem", Toast.LENGTH_SHORT).show();
                            Uri downloadUri = task.getResult();

                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdUsuario(idUsuarioRemetente);
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem(downloadUri.toString());

                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        } else {
                            Toast.makeText(ChatActivity.this, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mensagens.clear();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }
}
