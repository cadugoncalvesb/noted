package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.adapter.UserAdapter;
import com.example.projetointegrador.databinding.ActivityListUserBinding;
import com.example.projetointegrador.db.User;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListUserActivity extends AppCompatActivity implements OnItemClickListener{
    private ActivityListUserBinding binding;
    private FirebaseFirestore db;

    private ArrayList<User> listaUsers;
    private UserAdapter userAdapter;
    private RecyclerView recyclerViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityListUserBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listaUsers = new ArrayList<>();
        userAdapter = new UserAdapter(listaUsers, this);
        recyclerViewUsers = binding.recyclerViewUsers;
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        verificationUsersList();

        binding.btnBack.setOnClickListener(v -> finish());

    }

    private void verificationUsersList() {

        String idList = getIntent().getStringExtra("idList");

        db.collection("users-lists")
                .whereEqualTo("idList", idList)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao buscar os usuários", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value == null || value.isEmpty()) {
                        Toast.makeText(this, "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> arrayIdUsers = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        String idUser = doc.getString("idUser");
                        if (idUser != null) {
                            arrayIdUsers.add(idUser);
                        }
                    }
                    if (!arrayIdUsers.isEmpty()) {
                        loadDetailsUsers(arrayIdUsers);
                    }
                });
    }

    private void loadDetailsUsers(ArrayList<String> arrayIdUsers) {
        listaUsers.clear();

        db.collection("users")
                .whereIn(FieldPath.documentId(), arrayIdUsers)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao buscar detalhes dos usuários", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        listaUsers.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            User user = doc.toObject(User.class);
                            if (user != null) {
                                user.setIdUser(doc.getId());
                                listaUsers.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(int position) {

    }
}