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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private List<Lista> listaList;
    private OnItemClickListener listener;
    //private FirebaseFirestore db;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String idUser = currentUser.getUid();

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
        holder.textViewNameList.setText(lista.getNameList());
        String idList = lista.getIdList();

        holder.imageBtnOptions.setOnClickListener(v -> deleteRelationUserList(idList));
    }

    @Override
    public int getItemCount() {
        return listaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameList;
        ImageButton imageBtnOptions;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewNameList = itemView.findViewById(R.id.textViewNameList);
            imageBtnOptions = itemView.findViewById(R.id.imageBtnOptions);

            itemView.setOnClickListener(v -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    private void deleteRelationUserList(String idList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (idList == null || idList.isEmpty()) {
            System.out.println("ID da lista é nulo");
            return;
        }

        db.collection("users-lists")
                .whereEqualTo("idList", idList)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        db.collection("users-lists")
                                .document(doc.getId())
                                .delete();
                    }

                    deleteSubcollections(idList);
                })
                .addOnFailureListener(e -> System.out.println("Erro ao deletar relação usuário-lista"));
    }

    private void deleteSubcollections(String idList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lists").document(idList)
                .collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        db.collection("lists").document(idList)
                                .collection("items")
                                .document(doc.getId())
                                .delete();
                    }

                    // Após deletar todas as subcoleções, deletar o documento principal da lista
                    deleteListFirebase(idList);
                })
                .addOnFailureListener(e -> System.out.println("Erro ao apagar subcoleções"));
    }

    private void deleteListFirebase(String idList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (idList == null || idList.isEmpty()) {
            Log.e("Firebase", "O ID da lista não pode ser nulo ou vazio.");
            return;
        }

        db.collection("lists")
                .document(idList)
                .delete()
                .addOnSuccessListener(unused -> {
                    int position = -1;
                    for (int i = 0; i < listaList.size(); i++) {
                        if (listaList.get(i).getIdList().equals(idList)) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        listaList.remove(position);
                        notifyItemRemoved(position);
                    }
                    System.out.println("Lista e subcoleções deletadas com sucesso");
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao deletar lista"));
    }

}
