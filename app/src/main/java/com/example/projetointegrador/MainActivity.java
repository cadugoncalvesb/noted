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

import com.example.projetointegrador.adapter.ListaAdapter;
import com.example.projetointegrador.databinding.ActivityMainBinding;
import com.example.projetointegrador.db.Item;
import com.example.projetointegrador.db.Lista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ListaAdapter listaAdapter;
    List<Lista> listaList;

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

        loadListFirebase();

        binding.btnSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.btnNewList.setOnClickListener(v -> startActivity(new Intent(this, AddListActivity.class)));
        }

    private void loadListFirebase() {
        db.collection("lists")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firestore", "Erro ao carregar listas.", task.getException());
                        }
                        listaList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Lista lista = document.toObject(Lista.class);
                            lista.setIdList(document.getId());
                            listaList.add(lista);
                            listaAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    }
