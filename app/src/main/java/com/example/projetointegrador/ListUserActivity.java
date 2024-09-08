package com.example.projetointegrador;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
import com.example.projetointegrador.databinding.BottomSheetBinding;
import com.example.projetointegrador.db.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        userAdapter = new UserAdapter(listaUsers,"",this);
        recyclerViewUsers = binding.recyclerViewUsers;
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        verificationUsersList();

        binding.btnNewUser.setVisibility(View.GONE);
        String admin = getIntent().getStringExtra("admin");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        if (idUser.equals(admin)){
            binding.btnNewUser.setVisibility(View.VISIBLE);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnNewUser.setOnClickListener(v -> bottomSheetUser());
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
        String admin = getIntent().getStringExtra("admin");

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
                        userAdapter = new UserAdapter(listaUsers, admin, position -> {});
                        recyclerViewUsers.setAdapter(userAdapter);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bottomSheetUser() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_shet_user, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextInputLayout textInputLayoutEmailUser = view.findViewById(R.id.textInputLayoutEmailUser);
        TextInputEditText editTextEmailUser = view.findViewById(R.id.editTextEmailUser);
        MaterialButton btnAddUser = view.findViewById(R.id.btnAddUser);
        TextView textViewCancel = view.findViewById(R.id.textViewCancel);
        editTextEmailUser.requestFocus();

        btnAddUser.setOnClickListener(v -> {
            String email = editTextEmailUser.getText().toString().trim();
            if (email.isEmpty()) {
                textInputLayoutEmailUser.setError("Informe o e-mail do usuário");
                editTextEmailUser.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        textInputLayoutEmailUser.setError(null);
                    }
                });
                return;
            }
            searchUser(email);
            bottomSheetDialog.dismiss();
        });
        textViewCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private void searchUser(String email) {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_shet_user, null);

        TextInputEditText editTextEmailUser = view.findViewById(R.id.editTextEmailUser);
        TextInputLayout textInputLayoutEmailUser = view.findViewById(R.id.textInputLayoutEmailUser);

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        textInputLayoutEmailUser.setError("Usuário inexistente.");
                        Toast.makeText(this, "Usuário inexistente", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String idUser = documentSnapshot.getId();
                        validateUser(idUser);
                    }
                })
                .addOnFailureListener(error -> Toast.makeText(this, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show());
    }

    private void validateUser(String idUser) {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_shet_user, null);
        String idList = getIntent().getStringExtra("idList");

        TextInputLayout textInputLayoutEmailUser = view.findViewById(R.id.textInputLayoutEmailUser);

        db.collection("users-lists")
                .whereEqualTo("idList", idList)
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        textInputLayoutEmailUser.setError("Usuário já existente na lista.");
                        Toast.makeText(this, "Usuário já existente na lista", Toast.LENGTH_SHORT).show();
                    } else {
                        addUser(idUser);
                    }
                })
                .addOnFailureListener(error -> Toast.makeText(this, "Erro ao adicionar usuário", Toast.LENGTH_SHORT).show());
    }

    private void addUser(String idUser) {
        String idList = getIntent().getStringExtra("idList");

        Map<String, Object> usersLists = new HashMap<>();
        usersLists.put("idList", idList);
        usersLists.put("idUser", idUser);

        db.collection("users-lists")
                .add(usersLists)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Usuário adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> Toast.makeText(this, "Erro ao adicionar usuário", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(int position) {

    }
}