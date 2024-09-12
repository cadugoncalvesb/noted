package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetointegrador.adapter.ItemAdapter;
import com.example.projetointegrador.adapter.ListaAdapter;
import com.example.projetointegrador.databinding.ActivityItemBinding;
import com.example.projetointegrador.databinding.BottomSheetBinding;
import com.example.projetointegrador.db.Item;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityItemBinding binding;
    private FirebaseFirestore db;

    private RecyclerView recyclerViewItens;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityItemBinding.inflate(getLayoutInflater());
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList, this);
        recyclerViewItens = binding.recyclerViewItens;
        recyclerViewItens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItens.setAdapter(itemAdapter);

        loadItemFirebase();
        loadDateCreateList();

        String nameList = getIntent().getStringExtra("nameList");
        binding.textViewNameList.setText(nameList);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOptions.setOnClickListener(v -> bottomSheetOptions());
        binding.btnNewItem.setOnClickListener(v -> createItem());

    }

    private void bottomSheetOptions() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_list, null);

        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(this);
        bottomSheetDialog1.setContentView(view);
        bottomSheetDialog1.show();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        String idList = getIntent().getStringExtra("idList");
        String admin = getIntent().getStringExtra("admin");

        MaterialButton btnInfo = view.findViewById(R.id.btnInfo);
        MaterialButton btnShare = view.findViewById(R.id.btnShare);
        MaterialButton btnLogOut = view.findViewById(R.id.btnlogOut);
        MaterialButton btnDelete = view.findViewById(R.id.btnDelete);
        MaterialDivider div1 = view.findViewById(R.id.div1);
        MaterialDivider div2 = view.findViewById(R.id.div2);

        btnDelete.setVisibility(View.GONE);
        div1.setVisibility(View.GONE);
        div2.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);
        if (idUser.equals(admin)) {
            div1.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
        }

        btnInfo.setOnClickListener(view2 -> {
            Intent intent = new Intent(this, ListUserActivity.class);
            intent.putExtra("idList", idList);
            intent.putExtra("admin", admin);
            bottomSheetDialog1.dismiss();
            startActivity(intent);
        });

        btnShare.setOnClickListener(view2 -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND); // Intent que permite compartilhar com outros app
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Faça parte da minha lista:\nhttps://yourdomain.com/list?id=12345");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            bottomSheetDialog1.dismiss();
            startActivity(shareIntent);
        });

        btnLogOut.setOnClickListener(view2 -> {
            bottomSheetDialog1.dismiss();
            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle("Confirmar saída")
                    .setMessage("Tem certeza que deseja sair da lista?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        logOutList(idList, idUser);
                    })
                    .setNegativeButton("Não", (dialog, which) -> {

                    })
                    .show();
        });
    }

    private void loadItemFirebase() {
        String idList = getIntent().getStringExtra("idList");

        db.collection("lists")
                .document(idList)
                .collection("items")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Erro ao carregar itens.", error);
                    }
                    if (value != null && !value.isEmpty()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Item item = document.toObject(Item.class);
                            item.setIdItem(document.getId());
                            itemList.add(0, item);
                        }
                        itemAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadDateCreateList() {
        String idList = getIntent().getStringExtra("idList");

        db.collection("lists")
                .document(idList)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Timestamp timestamp = documentSnapshot.getTimestamp("dateCreate");
                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            String formattedDate = sdf.format(date);

                            binding.textViewDateCreateList.setText("Criada em: " + formattedDate);
                        } else {
                            binding.textViewDateCreateList.setText("Data de criação indisponível");
                        }
                    }
                })
                .addOnFailureListener(error -> {
                    binding.textViewDateCreateList.setText("Erro ao carregar data de criação");
                });
    }

    //TODO: está adicionando qtd e preco com zero
    private void createItem() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextInputLayout textInputLayoutNewItem = view.findViewById(R.id.textInputLayoutNewItem);
        EditText editTextNewItem = view.findViewById(R.id.editTextNewItem);
        EditText editTextUnidade = view.findViewById(R.id.editTextUnidade);
        EditText editTextQtd = view.findViewById(R.id.editTextQtd);
        EditText editTextPreco = view.findViewById(R.id.editTextPreco);
        MaterialButton btnAddNewItem = view.findViewById(R.id.btnAddNewItem);

        bottomSheetDialog.setOnShowListener(dialog -> editTextNewItem.requestFocus());

        btnAddNewItem.setOnClickListener(v -> {
            String newItem = editTextNewItem.getText().toString().trim();
            if (newItem.isEmpty()) {
                textInputLayoutNewItem.setError("Insira um item");
                return;
            }

            int quantidade = 0;
            float preco = 0.0f;
            String unidade = editTextUnidade.getText().toString().trim();

            String idList = getIntent().getStringExtra("idList");
            Item item = new Item(idList, newItem, false, unidade, quantidade, preco);
            addItemFirebase(item);

            textInputLayoutNewItem.setError(null);
            editTextNewItem.setText("");
            editTextUnidade.setText("");
            editTextQtd.setText("");
            editTextPreco.setText("");
        });

        editTextNewItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textInputLayoutNewItem.setError(null);
            }
        });
    }

    public void addItemFirebase(Item item) {
        String idList = getIntent().getStringExtra("idList");

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("idList", idList);
        itemData.put("nameItem", item.getNameItem());
        itemData.put("checked", item.checked());
        itemData.put("unidade", item.getUnidade());
        itemData.put("quantidade", item.getQuantidade());
        itemData.put("preco", item.getPreco());

        db.collection("lists")
                .document(idList)
                .collection("items")
                .add(itemData)
                .addOnSuccessListener(documentReference -> {
                    item.setIdItem(documentReference.getId());
                    Log.d("FirebaseSucess", "ID do item atualizado com sucesso");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Erro ao adicionar item");
                });
    }

    public void logOutList(String idList, String idUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users-lists")
                .whereEqualTo("idList", idList)
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            doc.getReference().delete();
                            System.out.println("Saiu");
                        }
                    } else System.out.println("Nada encontrado");
                })
                .addOnFailureListener(error -> {
                    System.out.println("Erro ao buscar documentos: " + error);
                });
    }

    @Override
    public void onItemClick(int position) {

    }
}


