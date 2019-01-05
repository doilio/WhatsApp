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
import com.example.dowy.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GrupoSelecionadoAdapter extends RecyclerView.Adapter<GrupoSelecionadoAdapter.MyViewHolder> {

    private List<Usuario> contactosSelecionados;
    private Context context;
    private LayoutInflater mInflater;

    public GrupoSelecionadoAdapter(List<Usuario> listaMembros, Context context) {
        this.contactosSelecionados = listaMembros;
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imageViewFotoMembroSelecionado);
            nome = itemView.findViewById(R.id.textNomeMembroSelecionado);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.adapter_grupo_selecionado, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Usuario usuario = contactosSelecionados.get(i);
        myViewHolder.nome.setText(usuario.getNome());

        if (usuario.getFoto() != null) {
            Uri url = Uri.parse(usuario.getFoto());
            Glide.with(context).load(url).into(myViewHolder.foto);
        } else {
            myViewHolder.foto.setImageResource(R.drawable.padrao);
        }


    }

    @Override
    public int getItemCount() {
        return contactosSelecionados.size();
    }


}
