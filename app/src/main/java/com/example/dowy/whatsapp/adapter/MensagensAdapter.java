package com.example.dowy.whatsapp.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dowy.whatsapp.R;
import com.example.dowy.whatsapp.helper.UsuarioFirebase;
import com.example.dowy.whatsapp.model.Mensagem;
import com.example.dowy.whatsapp.model.Usuario;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.context = c;
        this.mensagens = lista;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mensagem;
        private ImageView imagem;
        private TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textNomeExibicao);
            mensagem = itemView.findViewById(R.id.textMensagemTexto);
            imagem = itemView.findViewById(R.id.imageMensagemFoto);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemView = null;

        if (viewType == TIPO_REMETENTE) {
            itemView = LayoutInflater.from(context).inflate(R.layout.adapter_mensagem_remetente, viewGroup, false);
        } else if (viewType == TIPO_DESTINATARIO) {
            itemView = LayoutInflater.from(context).inflate(R.layout.adapter_mensagem_destinatario, viewGroup, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        Mensagem mensagem = mensagens.get(position);

        if (mensagem.getImagem() != null) {
            //Antes de passar para Glide Converter para Uri
            Uri url = Uri.parse(mensagem.getImagem());
            Glide.with(context).load(url).into(myViewHolder.imagem);

            String nome = mensagem.getNome();
            if (!nome.isEmpty()) {
                myViewHolder.nome.setText(nome);
            } else {
                myViewHolder.nome.setVisibility(View.GONE);
            }

            //Esconder Texto
            myViewHolder.mensagem.setVisibility(View.GONE);
        } else {

            myViewHolder.mensagem.setText(mensagem.getMensagem());

            String nome = mensagem.getNome();
            if (!nome.isEmpty()) {
                myViewHolder.nome.setText(nome);
            } else {
                myViewHolder.nome.setVisibility(View.GONE);
            }

            //Esconder Imagem
            myViewHolder.imagem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        if (idUsuario.equals(mensagem.getIdUsuario())) {
            return TIPO_REMETENTE;
        } else {
            return TIPO_DESTINATARIO;
        }
    }
}
