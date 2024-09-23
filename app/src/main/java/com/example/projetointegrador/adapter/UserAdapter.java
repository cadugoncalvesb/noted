package com.example.projetointegrador.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.OnItemClickListener;
import com.example.projetointegrador.R;
import com.example.projetointegrador.db.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> listaUsers;
    private OnItemClickListener listener;
    private String admin;

    public UserAdapter(List<User> listaUsers, String admin, OnItemClickListener listener) {
        this.listaUsers = listaUsers;
        this.listener = listener;
        this.admin = admin;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user, parent, false);
        return new ViewHolder(view, listener);
    }
    //TODO: funcionalidade incompleta para remoção de usuário da lista
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = listaUsers.get(position);
        holder.textViewNameUser.setText(user.getName());

        if (user.getIdUser().equals(admin)) {
            holder.textViewAdmin.setVisibility(View.VISIBLE);
        }
        holder.imageBtnDelete.setVisibility(View.GONE);
        holder.imageBtnDelete.setOnClickListener(v -> {
            if (user.getIdUser().equals(admin)) {
                holder.imageBtnDelete.setVisibility(View.VISIBLE);
                holder.imageBtnDelete.setOnClickListener(vv -> {
                    if (UserAdapter.this.listener != null) {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {

                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameUser, textViewAdmin;
        ImageButton imageBtnDelete;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewNameUser = itemView.findViewById(R.id.textViewNameUser);
            textViewAdmin = itemView.findViewById(R.id.textViewAdmin);
            imageBtnDelete = itemView.findViewById(R.id.imageBtnDelete);

            itemView.setOnClickListener(v -> {
                if (UserAdapter.this.listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        UserAdapter.this.listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
