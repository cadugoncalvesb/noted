package com.example.projetointegrador.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.MainActivity;
import com.example.projetointegrador.OnItemClickListener;
import com.example.projetointegrador.R;
import com.example.projetointegrador.db.Lista;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private List<Lista> listaList;
    private OnItemClickListener listener;
    private FirebaseFirestore db;

    public ListaAdapter(List<Lista> listaList, OnItemClickListener listener){
        this.listaList = listaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAdapter.ViewHolder holder, int position) {
        Lista lista = listaList.get(position);
        holder.textViewNameNameList.setText(lista.getNameList());
    }

    @Override
    public int getItemCount() {
        return listaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameNameList;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewNameNameList = itemView.findViewById(R.id.textViewNameList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
