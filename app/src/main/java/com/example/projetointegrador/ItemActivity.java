package com.example.projetointegrador;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.adapter.ItemAdapter;
import com.example.projetointegrador.databinding.ActivityItemBinding;
import com.example.projetointegrador.databinding.BottomSheetBinding;
import com.example.projetointegrador.db.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity implements OnItemClickListener {

    private ActivityItemBinding binding;
    private FirebaseFirestore db;
    private BottomSheetBinding bottomSheetBinding;

    private RecyclerView recyclerViewItens;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityItemBinding.inflate(getLayoutInflater());
        bottomSheetBinding = BottomSheetBinding.inflate(LayoutInflater.from(this));
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList, this);
        recyclerViewItens = binding.recyclerViewItens;
        recyclerViewItens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItens.setAdapter(itemAdapter);

        loadItemFirebase();

        binding.btnBack.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.btnNewItem.setOnClickListener(v -> bottomSheetDialog.show());

        bottomSheetDialog.setOnShowListener(dialog -> bottomSheetBinding.editTextNewItem.requestFocus());
        //bottomSheetDialog.setOnDismissListener(dialog -> bottomSheetBinding.editTextNewItem.clearFocus());
        bottomSheetBinding.btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = bottomSheetBinding.editTextNewItem.getText().toString().trim();
                if (newItem.isEmpty()) {
                    bottomSheetBinding.textInputLayoutNewItem.setError("Insira um item");
                    return;
                }
                Item item = new Item(newItem, false, null);
                addItemFirebase(new Item(newItem, false, null));
                //addItemToRecyclerView(new Item(newItem, false, null));

                bottomSheetBinding.textInputLayoutNewItem.setError(null);
                bottomSheetBinding.editTextNewItem.setText("");

            }
        });
    }

    private void loadItemFirebase() {
        db.collection("items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firestore", "Erro ao carregar itens.", task.getException());
                        }
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            item.setIdItem(document.getId());
                            itemList.add(item);
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void addItemFirebase(Item item) {
        db.collection("items")
                .add(item)
                .addOnSuccessListener(documentReference -> {

                    String itemId = documentReference.getId();
                    item.setIdItem(itemId);

                    documentReference.update("idItem", itemId)
                            .addOnSuccessListener(aVoid -> {
                                addItemToRecyclerView(item);
                                Log.d("FirebaseSucess", "ID do item atualizado com sucesso");
                            })
                            .addOnFailureListener(e ->
                                    Log.e("FirebaseError", "Falha ao atualizar ID do item"));

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao adicionar item", Toast.LENGTH_SHORT).show();
                });
    }

    private void addItemToRecyclerView(Item item) {
        // Adiciona o novo item à lista
        itemList.add(item);

        // Notifica o adaptador que um item foi adicionado
        itemAdapter.notifyItemInserted(itemList.size() - 1);

        // Rolagem para o fim da lista para mostrar o novo item
        recyclerViewItens.scrollToPosition(itemList.size() - 1);
    }

    @Override
    public void onItemClick(int position) {

    }
}


