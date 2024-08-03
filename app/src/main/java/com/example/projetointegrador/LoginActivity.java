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

import com.example.projetointegrador.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnLogin.setOnClickListener(v -> {
            if (validations()){
                binding.progressBar.setVisibility(View.VISIBLE);
                loginFirebase();
            }
        });
        binding.textViewCadastrar.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
        binding.textViewRecoverPassword.setOnClickListener(v -> startActivity(new Intent(this, RecoverPasswordActivity.class)));
    }

    public boolean validations() {

        if (binding.editTextEmail.getText().toString().isEmpty()){
            binding.textInputLayoutEmail.setError("Campo obrigatório");
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (binding.editTextPassword.getText().toString().isEmpty()){
            binding.textInputLayoutPassword.setError("Campo obrigatório");
            binding.editTextPassword.requestFocus();
            return false;
        }
        if (binding.editTextPassword.getText().toString().length() < 6){
            binding.textInputLayoutPassword.setError("A senha deve ter no mínimo 6 caracteres");
            binding.editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void loginFirebase() {
        String email = binding.editTextEmail.getText().toString();
        String senha = binding.editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                finish();
                startActivity(new Intent(this, MainActivity.class));

            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Ocorreu um erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}