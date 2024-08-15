package com.example.projetointegrador;

import static android.app.PendingIntent.getActivity;

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
import java.util.UUID;

public class ViewItensActivity extends AppCompatActivity implements OnItemClickListener{

    private ActivityViewItensBinding binding;
    private DatabaseReference mDatabase;
    private BottomSheetBinding bottomSheetBinding;

    private RecyclerView recyclerViewItens;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewItensBinding.inflate(getLayoutInflater());
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
        mDatabase = FirebaseDatabase.getInstance().getReference("items");
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList, this);
        recyclerViewItens = binding.recyclerViewItens;
        recyclerViewItens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItens.setAdapter(itemAdapter);

        findViewById(R.id.btnNewItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemBottomSheet bottomSheet = new AddItemBottomSheet(ViewItensActivity.this);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

        binding.btnBack.setOnClickListener(v -> { finish(); startActivity(new Intent(this, MainActivity.class));});
        binding.btnNewItem.setOnClickListener(v -> bottomSheetDialog.show());

        bottomSheetBinding.btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = bottomSheetBinding.editTextNewItem.getText().toString().trim();
                if (!newItem.isEmpty()) {
                    Item item = new Item("", newItem, false, "");
                    addItemToRecyclerView(newItem);
                    bottomSheetBinding.textInputLayoutNewItem.setError(null); // Limpa o erro, se houver
                    bottomSheetBinding.editTextNewItem.setText(""); // Limpa o campo de texto
                } else bottomSheetBinding.textInputLayoutNewItem.setError("Insira um item");
            }
        });
    }

    private void addItemToRecyclerView(String newItem) {
        // Adiciona o novo item à lista
        itemList.add(new Item(UUID.randomUUID().toString(), newItem, false, ""));

        // Notifica o adaptador que um item foi adicionado
        itemAdapter.notifyItemInserted(itemList.size() - 1);

        // Rolagem para o fim da lista para mostrar o novo item
        recyclerViewItens.scrollToPosition(itemList.size() - 1);
    }

    @Override
    public void onItemClick(int position) {

    }
}


