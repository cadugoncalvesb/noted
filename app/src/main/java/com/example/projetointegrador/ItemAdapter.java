package com.example.projetointegrador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter  extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Context context;
    private List<Item> itemList;
    private OnItemClickListener listener;

    public ItemAdapter(Context context, List<Item> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList != null ? itemList : new ArrayList<>(); // Inicialize a lista se estiver null
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewNameItem.setText(item.getNameItem());
        holder.checkBox.setChecked(item.isChecked());
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameItem;
        CheckBox checkBox;

        public ViewHolder(View view, OnItemClickListener listener) {
            super(view);
            textViewNameItem = itemView.findViewById(R.id.textViewNameItem);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(final Item item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
