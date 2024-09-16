package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetointegrador.databinding.ActivityAddListBinding;
import com.example.projetointegrador.db.Lista;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User não autenticado");
            finish();
            return;
        }
        String admin = currentUser.getUid();

        binding.editTextAddNewList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                binding.textInputLayoutNewList.setError(null);
            }
        });

        binding.editTextAddNewList.requestFocus();
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAddList.setOnClickListener(v -> {
            String newList = binding.editTextAddNewList.getText().toString().trim();
            if (newList.isEmpty()){
                binding.textInputLayoutNewList.setError("Informe o nome da nova lista");
                return;
            }

            Lista lista = new Lista(admin, newList, null, null);
            addListFirebase(lista);
            Toast.makeText(this, "Lista criada com sucesso!", Toast.LENGTH_SHORT).show();
        });
    }

    public void addListFirebase(Lista lista) {

        Map<String, Object> listData = new HashMap<>();
        listData.put("admin", lista.getAdmin());
        listData.put("nameList", lista.getNameList());
        listData.put("dateCreate", FieldValue.serverTimestamp());
        listData.put("dataModification", lista.getDateModification());

        db.collection("lists")
                .add(listData)
                .addOnSuccessListener(documentReference -> {

                    lista.setIdList(documentReference.getId());
                    idList = lista.getIdList();
                    nameList = lista.getNameList();
                    Log.d("FirebaseSucess", "ID da lista atualizado com sucesso");

                    weakEntity(idList, lista.getAdmin());

                    Intent intent = new Intent(this, ItemActivity.class);
                    intent.putExtra("idList", idList);
                    intent.putExtra("nameList", nameList);
                    intent.putExtra("admin", lista.getAdmin());
                    System.out.println("Estado do ID: " + idList);
                    finish();
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao adicionar lista", Toast.LENGTH_SHORT).show();
                });
    }

    private void weakEntity(String idList, String idUser) {

        Map<String, Object> userListData = new HashMap<>();
        userListData.put("idList", idList);
        userListData.put("idUser", idUser);

        db.collection("users-lists")
                .add(userListData)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Usuária associado à lista com sucesso");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Erro ao associar usuário à lista");
                });

    }
}