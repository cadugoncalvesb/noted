package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetointegrador.databinding.ActivityCadastroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {
    private ActivityCadastroBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String idUser;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.btnCadastrar.setOnClickListener(v -> {
            if (validations()){
                binding.progressBar.setVisibility(View.VISIBLE);
                createAccountFirebase();
            }
        });
    }

    public boolean validations() {

        if (binding.editTextNome.getText().toString().isEmpty()){
            binding.textInputLayoutNome.setError("Campo obrigatório");
            binding.editTextNome.requestFocus();
            return false;
        }
        binding.textInputLayoutNome.setError(null);

        if (binding.editTextCadastroEmail.getText().toString().isEmpty()){
            binding.textInputLayoutCadastroEmail.setError("Campo obrigatório");
            binding.editTextCadastroEmail.requestFocus();
            return false;
        }
        binding.textInputLayoutCadastroEmail.setError(null);

        String email = binding.editTextCadastroEmail.getText().toString();
        if (!isEmailValid(email)){
            binding.textInputLayoutCadastroEmail.setError("E-mail inválido");
            binding.editTextCadastroEmail.requestFocus();
            return false;
        }
        binding.textInputLayoutCadastroEmail.setError(null);

        if (binding.editTextCadastroPassword.getText().toString().isEmpty()){
            binding.textInputLayoutCadastroPassword.setError("Campo obrigatório");
            binding.editTextCadastroPassword.requestFocus();
            return false;
        }
        binding.textInputLayoutCadastroPassword.setError(null);

        if (binding.editTextCadastroPassword.getText().toString().length() < 6){
            binding.textInputLayoutCadastroPassword.setError("A senha deve ter no mínimo 6 caracteres");
            binding.editTextCadastroPassword.requestFocus();
            return false;
        }
        binding.textInputLayoutCadastroPassword.setError(null);

        return true;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void createAccountFirebase() {
        String email = binding.editTextCadastroEmail.getText().toString();
        String password = binding.editTextCadastroPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task ->  {
            if (task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                addUserFirebase(user);
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                binding.progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Ocorreu um erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addUserFirebase(FirebaseUser user) {
        String name = binding.editTextNome.getText().toString().toString();
        String email = binding.editTextCadastroEmail.getText().toString().trim();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);

        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Sucesso");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Deu ruim");
                });
    }
}