package com.example.projetointegrador.adapter;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

            MaterialButton btnShare = bottomSheetView.findViewById(R.id.btnShare);
            MaterialButton btnDelete = bottomSheetView.findViewById(R.id.btnDelete);
            MaterialButton btnUpdateNameList = bottomSheetView.findViewById(R.id.btnUpdateNameList);
            MaterialDivider div1 = bottomSheetView.findViewById(R.id.div1);
            MaterialDivider div2 = bottomSheetView.findViewById(R.id.div2);
            MaterialDivider div3 = bottomSheetView.findViewById(R.id.div3);

            // Ocultar por padrão
            btnDelete.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
            btnUpdateNameList.setVisibility(View.GONE);
            div1.setVisibility(View.GONE);
            div2.setVisibility(View.GONE);
            div3.setVisibility(View.GONE);

            // Verifica se o usuário atual é o admin
            if (idUser.equals(admin)) {
                btnDelete.setVisibility(View.VISIBLE);
                btnShare.setVisibility(View.VISIBLE);
                btnUpdateNameList.setVisibility(View.VISIBLE);
                div1.setVisibility(View.VISIBLE);
                div2.setVisibility(View.VISIBLE);
                div3.setVisibility(View.VISIBLE);
            }

            bottomSheetView.findViewById(R.id.btnInfo).setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), ListUserActivity.class);
                intent.putExtra("idList", idList);
                intent.putExtra("admin", admin);
                bottomSheetDialog.dismiss();
                holder.itemView.getContext().startActivity(intent);
            });

            bottomSheetView.findViewById(R.id.btnUpdateNameList).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                updateNameList(lista.getNameList(), idList, holder.itemView.getContext());
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
                bottomSheetDialog.dismiss();
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Confirmar saída")
                        .setMessage("Tem certeza que deseja sair da lista?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            logOutList(idList, idUser);
                        })
                        .setNegativeButton("Não", (dialog, which) -> {

                        })
                        .show();
            });

            bottomSheetView.findViewById(R.id.btnDelete).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Confirmar exclusão")
                        .setMessage("Tem certeza que deseja excluir à lista?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            deleteRelationUserList(idList);
                        })
                        .setNegativeButton("Não", (dialog, which) -> {

                        })
                        .show();
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

    private void updateNameList(String currentNameList, String idList, Context context) {
        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_shet_user, null);

        TextInputLayout textInputLayoutEmailUser = view.findViewById(R.id.textInputLayoutEmailUser);
        TextInputEditText editTextEmailUser = view.findViewById(R.id.editTextEmailUser);
        MaterialButton btnAddUser = view.findViewById(R.id.btnAddUser);
        TextView textViewCancel = view.findViewById(R.id.textViewCancel);

        textInputLayoutEmailUser.setHint("Nome da lista");
        textInputLayoutEmailUser.setHelperText("Informe o novo nome da lista");
        btnAddUser.setText("Renomear");

        editTextEmailUser.setText(currentNameList);
        editTextEmailUser.requestFocus();

        textViewCancel.setOnClickListener(vv -> {
            bottomSheetDialog1.dismiss();
        });

        btnAddUser.setOnClickListener(v -> {
            String newNameList = editTextEmailUser.getText().toString().trim();
            if (newNameList.isEmpty()) {
                textInputLayoutEmailUser.setError("Informe o novo nome da lista");
                return;
            }
            updateNameListFirebase(newNameList, idList);
            bottomSheetDialog1.dismiss();
        });
        bottomSheetDialog1.setContentView(view);
        bottomSheetDialog1.show();
    }

    private void updateNameListFirebase(String newNameList, String idList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lists").document(idList)
                .update("nameList", newNameList)
                .addOnSuccessListener(aVoid -> {
                    for (Lista lista : listaList) {
                        if (lista.getIdList().equals(idList)) {
                            lista.setNameList(newNameList);
                            notifyDataSetChanged();
                            break;
                        }
                    }
                })
                .addOnFailureListener(error -> {
                    System.out.println("Deu ruim");
                });
    }

}
