package com.example.dowy.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dowy.whatsapp.R;
import com.example.dowy.whatsapp.model.Conversa;
import com.example.dowy.whatsapp.model.Grupo;
import com.example.dowy.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> listaConversa;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        context = c;
        this.listaConversa = lista;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imagem;
        private TextView nome;
        private TextView ultimaMensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.foto_conversa);
            nome = itemView.findViewById(R.id.nome_conversa);
            ultimaMensagem = itemView.findViewById(R.id.ultima_mensagem);
        }
    }

    @NonNull
    @Override
    public ConversasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.conversa_list_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasAdapter.MyViewHolder myViewHolder, int i) {

        Conversa conversaCorrente = listaConversa.get(i);
        myViewHolder.ultimaMensagem.setText(conversaCorrente.getUltimaMensagem());

        if (conversaCorrente.getIsGroup().equals("true")) {

            Grupo grupo = conversaCorrente.getGrupo();
            myViewHolder.nome.setText(grupo.getNome());
            if (grupo.getFoto() != null) {
                Uri fotoUrl = Uri.parse(grupo.getFoto());

                Glide.with(context).load(fotoUrl).into(myViewHolder.imagem);
            } else {
                myViewHolder.imagem.setImageResource(R.drawable.padrao);
            }

        } else {
            Usuario usuario = conversaCorrente.getUsuarioExibicao();
            myViewHolder.nome.setText(usuario.getNome());
            if (usuario.getFoto() != null) {
                Uri fotoUrl = Uri.parse(usuario.getFoto());

                Glide.with(context).load(fotoUrl).into(myViewHolder.imagem);
            } else {
                myViewHolder.imagem.setImageResource(R.drawable.padrao);
            }


        }

    }

    @Override
    public int getItemCount() {
        return listaConversa.size();
    }


}
