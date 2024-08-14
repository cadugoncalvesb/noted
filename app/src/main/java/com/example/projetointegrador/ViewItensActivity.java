package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.projetointegrador.databinding.ActivityViewItensBinding;
import com.example.projetointegrador.databinding.BottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewItensActivity extends AppCompatActivity implements OnItemClickListener{

    private DatabaseReference mDatabase;
    private ActivityViewItensBinding binding;
    private BottomSheetBinding bottomSheetBinding;

    private RecyclerView recyclerViewItens;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("items");

        binding = ActivityViewItensBinding.inflate(getLayoutInflater());
        bottomSheetBinding = BottomSheetBinding.inflate(LayoutInflater.from(this));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());

        recyclerViewItens = binding.recyclerViewItens;
        recyclerViewItens.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(this, itemList, this);
        recyclerViewItens.setAdapter(itemAdapter);
        itemList = new ArrayList<>();

                setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadItemsFromFirebase();

        findViewById(R.id.btnNewItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemBottomSheet bottomSheet = new AddItemBottomSheet(ViewItensActivity.this);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.btnNewItem.setOnClickListener(v -> bottomSheetDialog.show());

        bottomSheetBinding.btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = bottomSheetBinding.editTextNewItem.getText().toString();
                if (!newItem.isEmpty()) {
                    bottomSheetDialog.dismiss();
                } else {
                    bottomSheetBinding.textInputLayoutNewItem.setError("Insira um item na lista");
                }
            }
        });
    }

    public void addItem(String itemName) {

        String itemId = mDatabase.push().getKey(); // Gera um id único para o item
        if (itemId != null) {
            Item item = new Item(itemId, itemName, false);
            mDatabase.child(itemId).setValue(item)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(ViewItensActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(ViewItensActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadItemsFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewItensActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Item item) {
        Toast.makeText(this, "Item clicado: " + item.getNameItem(), Toast.LENGTH_SHORT).show();
    }
}


