package com.sci.labtrack.ServicesAndTools;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sci.labtrack.R;


public class InputLayoutHandler {
    public static void editTextHandler(TextInputEditText editText,String editTextMessage, TextInputLayout textInputLayout,String layoutMessage){
        editText.setError(editTextMessage);
        editText.requestFocus();
        textInputLayout.setError(layoutMessage);
    }
    public static void editTextHandler(TextInputEditText editText, TextInputLayout textInputLayout,String Message){
        editText.setError(Message);
        editText.requestFocus();
        textInputLayout.setError(Message);
    }
    public static void editTextHandler(TextInputEditText editText,String editTextMessage){
        editText.setError(editTextMessage);
        editText.requestFocus();
    }
    public static void editTextHandler(TextInputLayout editTextLayout,String editTextMessage){
        editTextLayout.setError(editTextMessage);
        editTextLayout.requestFocus();
    }
    public static void editTextListeners(TextInputEditText editText, TextInputLayout textInputLayout){

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!editText.getText().toString().isEmpty()){
                    textInputLayout.setError("");
                }
                else{
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.cantbeempty));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public static void focusChangeListeners(TextInputEditText editText, TextInputLayout textInputLayout){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!editText.getText().toString().isEmpty()){
                    textInputLayout.setError("");
                }
                else{
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.cantbeempty));
                }
            }
        });
    }

}
