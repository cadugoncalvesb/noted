package com.example.projetointegrador.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.db.Item;
import com.example.projetointegrador.OnItemClickListener;
import com.example.projetointegrador.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewNameItem.setText(item.getNameItem());
        holder.checkBox.setChecked(item.checked());
        String idItem = item.getIdItem();
        String idList = item.getIdList();

        // TODO: precisa estar online
        holder.checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
            if (idList == null || idList.isEmpty()) {
                Log.e("ItemAdapter","ID da lista é nulo ou vazio");
                return;
            }
            if (idItem == null || idItem.isEmpty()) {
                Log.e("ItemAdapter", "ID do item é nulo ou vazio");
                return;
            }

            item.setChecked(checked);
            updateCheckedFirebase(idList, idItem, checked);
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

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewNameItem = itemView.findViewById(R.id.textViewNameItem);
            checkBox = itemView.findViewById(R.id.checkBox);
            imageBtnDelete = itemView.findViewById(R.id.imageBtnDelete);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
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

    //TODO: precisa estar online
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
