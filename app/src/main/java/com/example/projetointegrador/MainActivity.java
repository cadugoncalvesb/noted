package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.adapter.ListaAdapter;
import com.example.projetointegrador.databinding.ActivityMainBinding;
import com.example.projetointegrador.db.Item;
import com.example.projetointegrador.db.Lista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Lista> listaList;
    private ListaAdapter listaAdapter;
    private RecyclerView recyclerViewMyLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        listaList = new ArrayList<>();
        listaAdapter = new ListaAdapter(listaList, this);
        recyclerViewMyLists = binding.recyclerViewMyLists;
        recyclerViewMyLists.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyLists.setAdapter(listaAdapter);

        loadListFirebase();

        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.btnNewList.setOnClickListener(v -> startActivity(new Intent(this, AddListActivity.class)));
        }

    private void loadListFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("Usuário não autenticado");
            return;
        }
        String idUser = currentUser.getUid();

        db.collection("users-lists")
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        System.out.println("Erro ao carregar associações de listas: " + task.getException());
                        return;
                    }

                    listaList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idList = document.getString("idList");

                        // Buscar os detalhes da lista na coleção "lists"
                        db.collection("lists").document(idList)
                                .get()
                                .addOnSuccessListener(listDocument -> {
                                    if (listDocument.exists()) {
                                        Lista lista = listDocument.toObject(Lista.class);
                                        lista.setIdList(idList);
                                        listaList.add(lista);
                                        listaAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Erro ao carregar detalhes da lista.", e);
                                });
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Lista lista = listaList.get(position);
        String idList = lista.getIdList();
        String nameList = lista.getNameList();

        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("idList", idList);
        intent.putExtra("nameList", nameList);
        System.out.println("Estado do ID na main: " + idList);

        //finish();
        startActivity(intent);
    }
}