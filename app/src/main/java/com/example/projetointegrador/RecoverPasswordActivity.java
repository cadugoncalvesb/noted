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

import com.example.projetointegrador.databinding.ActivityRecoverPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordActivity extends AppCompatActivity {

    private ActivityRecoverPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRecoverPasswordBinding .inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();

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

        binding.btnRecover.setOnClickListener(v -> {
            if (validations()){
                binding.progressBar.setVisibility(View.VISIBLE);
                recoverPasswordFirebase();
            }
        });
    }

    public boolean validations() {
        String email = binding.editTextEmailRecover.getText().toString();

        if (binding.editTextEmailRecover.getText().toString().isEmpty()){
            binding.textInputLayoutEmailRecover.setError("Campo obrigatório");
            binding.editTextEmailRecover.requestFocus();
            return false;

        } else binding.textInputLayoutEmailRecover.setError(null);

        if (!isEmailValid(email)){
            binding.textInputLayoutEmailRecover.setError("E-mail inválido");
            binding.editTextEmailRecover.requestFocus();
            return false;

        } else binding.textInputLayoutEmailRecover.setError(null);

        return true;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private void recoverPasswordFirebase() {
        String email = binding.editTextEmailRecover.getText().toString();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "Verifique sua caixa de e-mail", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Ocorreu um erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }
}