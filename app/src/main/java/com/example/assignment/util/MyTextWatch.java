package com.example.assignment.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MyTextWatch implements TextWatcher {
    private EditText editText;
    private final ImageView clearTextButton;

    public MyTextWatch(EditText editText, ImageView clearTextButton) {
        this.editText = editText;
        this.clearTextButton = clearTextButton;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            clearTextButton.setVisibility(View.VISIBLE);
        } else {
            clearTextButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
