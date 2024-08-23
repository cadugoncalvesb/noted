package com.example.projetointegrador.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.MainActivity;
import com.example.projetointegrador.OnItemClickListener;
import com.example.projetointegrador.R;
import com.example.projetointegrador.db.Lista;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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
        String idList = lista.getIdList();

        holder.imageBtnOptions.setOnClickListener(v -> deleteListFirebase(idList));
    }

    @Override
    public int getItemCount() {
        return listaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameNameList;
        ImageButton imageBtnOptions;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewNameNameList = itemView.findViewById(R.id.textViewNameList);
            imageBtnOptions = itemView.findViewById(R.id.imageBtnOptions);

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
    private void deleteListFirebase(String idList) {
        if (idList == null || idList.isEmpty()) {
            Log.e("Firebase", "O ID da lista não pode ser nulo ou vazio.");
            return;
        }
        DocumentReference itemRef = FirebaseFirestore.getInstance()
                .collection("lists")
                .document(idList);
        itemRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //itemList.removeIf(item -> item.getIdItem().equals(idItem));
                        int position = -1;
                        for (int i = 0; i < listaList.size(); i++){
                            if (listaList.get(i).getIdList().equals(idList)){
                                position = i;
                                break;
                            }
                        }
                        if (position != -1){
                            listaList.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Erro ao deletar lista");
                    }
                });
    }
}
