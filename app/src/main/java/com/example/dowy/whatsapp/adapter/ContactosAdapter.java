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

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.MyViewHolder> {

    private List<Usuario> listaContactos;
    private Context contexto;

    public ContactosAdapter(List<Usuario> listaContactos, Context contexto) {
        this.listaContactos = listaContactos;
        this.contexto = contexto;
    }

    public List<Usuario> getContactos() {
        return this.listaContactos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mFoto;
        private TextView mNome;
        private TextView mEmail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mFoto = itemView.findViewById(R.id.foto_contacto);
            mNome = itemView.findViewById(R.id.nome_contacto);
            mEmail = itemView.findViewById(R.id.email_contacto);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.contactos_list_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Usuario usuario = listaContactos.get(i);
        boolean cabecalho = usuario.getEmail().isEmpty();
        myViewHolder.mEmail.setText(usuario.getEmail());
        myViewHolder.mNome.setText(usuario.getNome());

        if (usuario.getFoto() != null) {
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(contexto).load(uri).into(myViewHolder.mFoto);
        } else {
            if (cabecalho) {
                myViewHolder.mFoto.setImageResource(R.drawable.icone_grupo);
                myViewHolder.mEmail.setVisibility(View.GONE);
            } else {
                myViewHolder.mFoto.setImageResource(R.drawable.padrao);
            }

        }
    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }


}
