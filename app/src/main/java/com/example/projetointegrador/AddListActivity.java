package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.adapter.ListaAdapter;
import com.example.projetointegrador.databinding.ActivityAddListBinding;
import com.example.projetointegrador.db.Item;
import com.example.projetointegrador.db.Lista;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddListActivity extends AppCompatActivity {

    private ActivityAddListBinding binding;
    private FirebaseFirestore db;
    private String idList;
    private String nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        binding.editTextAddNewList.requestFocus();

        binding.btnBack.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.btnAddList.setOnClickListener(v -> {
            binding.editTextAddNewList.requestFocus();
            String newList = binding.editTextAddNewList.getText().toString().trim();
            if (newList.isEmpty()){
                binding.textInputLayoutNewList.setError("Informe o nome da nova lista");
                return;
            }

            Lista lista = new Lista(null, null, newList, null, null);
            addListFirebase(lista);
            Toast.makeText(this, "Lista criada com sucesso!", Toast.LENGTH_SHORT).show();
        });
    }

    public void addListFirebase(Lista lista) {

        Map<String, Object> listData = new HashMap<>();
        listData.put("admin", lista.getAdmin());
        listData.put("idUser", lista.getIdUser());
        listData.put("nameList", lista.getNameList());
        listData.put("dateCreate", lista.getDateCreate());
        listData.put("dataModification", lista.getDateModification());

        db.collection("lists")
                .add(listData)
                .addOnSuccessListener(documentReference -> {

                    lista.setIdList(documentReference.getId());
                    idList = lista.getIdList();
                    Log.d("FirebaseSucess", "ID da lista atualizado com sucesso");

                    Intent intent = new Intent(this, ItemActivity.class);
                    intent.putExtra("idList", idList);
                    System.out.println("Estado do ID: " + idList);
                    nameList = lista.getNameList();
                    intent.putExtra("nameList", nameList);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao adicionar lista", Toast.LENGTH_SHORT).show();
                });
    }
}