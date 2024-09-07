package com.example.projetointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetointegrador.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

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

        binding.editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.textInputLayoutEmail.setError(null);
            }
        });
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.textInputLayoutPassword.setError(null);
            }
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
        String email = binding.editTextEmail.getText().toString();

        if (binding.editTextEmail.getText().toString().isEmpty()){
            binding.textInputLayoutEmail.setError("Campo obrigatório");
            binding.editTextEmail.requestFocus();
            return false;

        } else binding.textInputLayoutEmail.setError(null);

        if (!isEmailValid(email)){
            binding.textInputLayoutEmail.setError("E-mail inválido");
            binding.editTextEmail.requestFocus();
            return false;

        } else binding.textInputLayoutEmail.setError(null);

        if (binding.editTextPassword.getText().toString().isEmpty()){
            binding.textInputLayoutPassword.setError("Campo obrigatório");
            binding.editTextPassword.requestFocus();
            return false;

        } else binding.textInputLayoutPassword.setError(null);

        if (binding.editTextPassword.getText().toString().length() < 6){
            binding.textInputLayoutPassword.setError("A senha deve ter no mínimo 6 caracteres");
            binding.editTextPassword.requestFocus();
            return false;

        } else binding.textInputLayoutPassword.setError(null);

        return true;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

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