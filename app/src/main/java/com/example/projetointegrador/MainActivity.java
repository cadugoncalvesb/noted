package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.adapter.ListaAdapter;
import com.example.projetointegrador.databinding.ActivityMainBinding;
import com.example.projetointegrador.db.Lista;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheIndexManager;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                        // Usar cache somente de memória
                        .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                        // Usar cache de disco persistente (padrão)
                        .setLocalCacheSettings(PersistentCacheSettings.newBuilder()
                                .build())
                        .build();
        db.setFirestoreSettings(settings);

        // return type: @Nullable PersistentCacheIndexManager
        PersistentCacheIndexManager indexManager = FirebaseFirestore.getInstance().getPersistentCacheIndexManager();
        if (indexManager != null) {
            // Indexing is disabled by default
            indexManager.enableIndexAutoCreation();
        }

        // If not check indexManager != null, IDE shows warning: Method invocation 'enableIndexAutoCreation' may produce 'NullPointerException'
        FirebaseFirestore.getInstance().getPersistentCacheIndexManager().enableIndexAutoCreation();

        listaList = new ArrayList<>();
        listaAdapter = new ListaAdapter(listaList, this);
        recyclerViewMyLists = binding.recyclerViewMyLists;
        recyclerViewMyLists.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyLists.setAdapter(listaAdapter);

        verificationListsUser();

        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.btnNewList.setOnClickListener(v -> startActivity(new Intent(this, AddListActivity.class)));
    }

    private void verificationListsUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("Usuário não autenticado");
            return;
        }
        String idUser = currentUser.getUid();

        db.collection("users-lists")
                .whereEqualTo("idUser", idUser)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value == null || value.isEmpty()) {
                        //Toast.makeText(this, "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> arrayIdLists = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        String idList = doc.getString("idList");
                        if (idList != null) {
                            arrayIdLists.add(idList);
                        }
                    }
                    if (!arrayIdLists.isEmpty()) {
                        loadDetailsLists(arrayIdLists);
                    }
                });
    }

    private void loadDetailsLists(ArrayList<String> arrayIdLists) {
        listaList.clear();

        db.collection("lists")
                .whereIn(FieldPath.documentId(), arrayIdLists)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao buscar detalhes das listas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        listaList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Lista lista = doc.toObject(Lista.class);
                            if (lista != null) {
                                lista.setIdList(doc.getId());
                                listaList.add(lista);
                            }
                        }
                        listaAdapter.notifyDataSetChanged();
                    } else {
                        //Toast.makeText(this, "Nenhuma lista encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Lista lista = listaList.get(position);
        String idList = lista.getIdList();
        String nameList = lista.getNameList();
        String admin = lista.getAdmin();

        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("idList", idList);
        intent.putExtra("nameList", nameList);
        intent.putExtra("admin", admin);
        System.out.println("Estado do ID na main: " + idList);

        //finish();
        startActivity(intent);
    }
}