package com.example.projetointegrador.adapter;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.ListUserActivity;
import com.example.projetointegrador.OnItemClickListener;
import com.example.projetointegrador.ProfileActivity;
import com.example.projetointegrador.R;
import com.example.projetointegrador.db.Lista;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private List<Lista> listaList;
    private OnItemClickListener listener;
    private String admin;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String idUser = currentUser.getUid();

    public ListaAdapter(List<Lista> listaList, String admin, OnItemClickListener listener){
        this.listaList = listaList;
        this.listener = listener;
        this.admin = admin;
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
        String admin = lista.getAdmin();

        holder.imageBtnOptions.setOnClickListener(v -> {
            View bottomSheetView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.bottom_sheet_list, null);

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext());
            bottomSheetDialog.setContentView(bottomSheetView);

            bottomSheetView.findViewById(R.id.btnInfo).setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), ListUserActivity.class);
                intent.putExtra("idList", idList);
                intent.putExtra("admin", admin);
                holder.itemView.getContext().startActivity(intent);
            });

            bottomSheetView.findViewById(R.id.btnShare).setOnClickListener(view -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND); // Intent que permite compartilhar com outros app
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Faça parte da minha lista:\nhttps://yourdomain.com/list?id=12345");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                bottomSheetDialog.dismiss();
                holder.itemView.getContext().startActivity(shareIntent);
            });

            bottomSheetView.findViewById(R.id.btnlogOut).setOnClickListener(view -> {
                logOutList(idList, idUser);
                bottomSheetDialog.dismiss();
            });

            bottomSheetView.findViewById(R.id.btnDelete).setOnClickListener(view -> {
                deleteRelationUserList(idList);
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });
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

    public void logOutList(String idList, String idUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users-lists")
                .whereEqualTo("idList", idList)
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            doc.getReference().delete();
                            System.out.println("Saiu");
                        }
                    } else System.out.println("Nada encontrado");
                })
                .addOnFailureListener(error -> {
                    System.out.println("Erro ao buscar documentos: " + error);
                });
    }

    public void deleteRelationUserList(String idList) {
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

    public void deleteSubcollections(String idList) {
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

    public void deleteListFirebase(String idList) {
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
