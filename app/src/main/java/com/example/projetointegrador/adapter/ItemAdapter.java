package com.example.projetointegrador.adapter;

import static android.os.Build.VERSION_CODES.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.R;
import com.example.projetointegrador.db.Item;
import com.example.projetointegrador.OnItemClickListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private OnItemClickListener listener;

    public ItemAdapter(Context context, List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.projetointegrador.R.layout.card_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewNameItem.setText(item.getNameItem());
        holder.textViewUnidade.setText(item.getUnidade());
        holder.textViewQtd.setText(String.valueOf(item.getQuantidade()));
        holder.textViewPreco.setText(String.valueOf(String.format("%.2f",item.getPreco())));

        String idItem = item.getIdItem();
        String idList = item.getIdList();

        holder.checkBox.setOnCheckedChangeListener(null); // Remover listener anterior
        holder.checkBox.setChecked(item.checked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
            if (idList == null || idList.isEmpty()) {
                Log.e("ItemAdapter","ID da lista é nulo ou vazio");
                return;
            }
            if (idItem == null || idItem.isEmpty()) {
                Log.e("ItemAdapter", "ID do item é nulo ou vazio");
                return;
            }

            // Atualiza o estado apenas se ele realmente mudou
            if (item.checked() != checked) {
                item.setChecked(checked);
                updateCheckedFirebase(idList, idItem, checked);
                notifyItemChanged(position); // Atualizar a posição específica no RecyclerView
            }
        });

        holder.imageBtnDelete.setOnClickListener(v -> deleteItemFirebase(idList, idItem));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameItem;
        CheckBox checkBox;
        ImageButton imageBtnDelete;
        TextView textViewQtd;
        TextView textViewUnidade;
        TextView textViewPreco;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewNameItem = itemView.findViewById(com.example.projetointegrador.R.id.textViewNameItem);
            checkBox = itemView.findViewById(com.example.projetointegrador.R.id.checkBox);
            imageBtnDelete = itemView.findViewById(com.example.projetointegrador.R.id.imageBtnDelete);
            textViewQtd = itemView.findViewById(com.example.projetointegrador.R.id.textViewQtd);
            textViewUnidade = itemView.findViewById(com.example.projetointegrador.R.id.textViewUnidade);
            textViewPreco = itemView.findViewById(com.example.projetointegrador.R.id.textViewPreco);

            //imageBtnDelete.setVisibility(View.GONE);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                        //imageBtnDelete.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void updateCheckedFirebase(String idList, String idItem, boolean checked) {
        if (idList == null || idList.isEmpty()) {
            System.out.println("ID da lista é nulo ou vazio no updateChecked");
        }
        if (idItem == null || idItem.isEmpty()) {
            System.out.println("ID do item é nulo ou vazio no updateChecked");
            return;
        }
        DocumentReference itemRef = FirebaseFirestore.getInstance()
                .collection("lists")
                .document(idList)
                .collection("items")
                .document(idItem);
        itemRef.update("checked", checked)
                .addOnSuccessListener(unused -> Log.d("Firebase", "Estado do checkbox atualizado com sucesso."))
                .addOnFailureListener(e -> Log.w("Firebase", "Erro ao atualizar estado do checkbox", e));
    }

    private void deleteItemFirebase(String idList, String idItem) {
        if (idList == null || idList.isEmpty()) {
            System.out.println("ID da lista é nulo ou vazio no deleteItem");
        }
        if (idItem == null || idItem.isEmpty()) {
            System.out.println("ID do item é nulo ou vazio no deleteItem");
            return;
        }
        DocumentReference itemRef = FirebaseFirestore.getInstance()
                .collection("lists")
                .document(idList)
                .collection("items")
                .document(idItem);
        itemRef.delete()
                .addOnSuccessListener(unused -> {
                    int position = -1;
                    for (int i = 0; i < itemList.size(); i++){
                        if (itemList.get(i).getIdItem().equals(idItem)){
                            position = i;
                            break;
                        }
                    }
                    if (position != -1){
                        itemList.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao deletar item"));
    }
}
