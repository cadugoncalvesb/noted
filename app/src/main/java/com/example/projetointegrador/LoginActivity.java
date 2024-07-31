package com.example.projetointegrador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetointegrador.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnLogin.setOnClickListener(v -> validacoes());
    }

    public void validacoes() {
        String email = binding.editTextEmail.getText().toString();

        if (binding.editTextEmail.getText().toString().isEmpty()){
            binding.textInputLayoutEmail.setError("Campo obrigatório");
            binding.editTextEmail.requestFocus();
            return;
        }
        if (!isEmailValid(email)){
            binding.textInputLayoutEmail.setError("E-mail inválido");
            binding.editTextEmail.requestFocus();
            return;
        }
        if (binding.editTextPassword.getText().toString().isEmpty()){
            binding.textInputLayoutPassword.setError("Campo obrigatório");
            binding.editTextPassword.requestFocus();
            return;
        }
        if (binding.editTextPassword.getText().toString().length() < 6){
            binding.textInputLayoutPassword.setError("A senha deve ter no mínimo 6 caracteres");
            binding.editTextPassword.requestFocus();
            return;
        }
    }
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }
}