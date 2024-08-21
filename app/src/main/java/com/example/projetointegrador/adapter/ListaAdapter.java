package com.example.projetointegrador.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.OnItemClickListener;
import com.example.projetointegrador.db.Lista;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private List<Lista> listaList;
    private OnItemClickListener listener;
    private FirebaseFirestore db;

    @NonNull
    @Override
    public ListaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
