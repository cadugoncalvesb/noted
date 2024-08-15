package com.example.projetointegrador;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.annotation.Nullable;

public class AddItemBottomSheet extends BottomSheetDialogFragment {

    private EditText editTextNewItem;
    private ViewItensActivity viewItensActivity;

    public AddItemBottomSheet(ViewItensActivity viewItensActivity) {
        this.viewItensActivity = viewItensActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        editTextNewItem = view.findViewById(R.id.editTextNewItem);
        Button btnAddNewItem = view.findViewById(R.id.btnAddNewItem);

        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = editTextNewItem.getText().toString();
                if (!itemName.isEmpty()) {
                    //viewItensActivity.addItem(itemName);
                    dismiss();

                } else Toast.makeText(viewItensActivity, "Adicione o nome do item", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextNewItem.requestFocus();

        // Mostrar o teclado
        showKeyboard(editTextNewItem);
    }

    private void showKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}
