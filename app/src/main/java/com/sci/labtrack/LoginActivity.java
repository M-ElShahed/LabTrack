package com.sci.labtrack;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sci.labtrack.Lab.LabsList;
import com.sci.labtrack.ServicesAndTools.InputLayoutHandler;
import com.sci.labtrack.User.RegisterAct;
import com.sci.labtrack.User.User;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.forgetPassword)     TextView forgetPassword;
    @BindView(R.id.RegButton)          Button regButton ;
    @BindView(R.id.loginButton)        Button loginButton;
    @BindView(R.id.emailText)          TextInputEditText emailText;
    @BindView(R.id.passwordText)       TextInputEditText passText;
    @BindView(R.id.emailTextLayout)    TextInputLayout emailLayout;
    @BindView(R.id.passwordTextLayout) TextInputLayout passLayout;
    @BindView(R.id.progressBar)        ProgressBar progressBar ;

    User user = new User();
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        // if the user already logged in
        userOrNot();
        /////////////////////////////////////
        editTextListeners();
        ///////////////////////////////////
        regButton.setOnClickListener(v -> {
            if (isCon()) {
                Intent i = new Intent(LoginActivity.this, RegisterAct.class);
                startActivity(i);
                Animatoo.animateCard(this);
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.connectionError),
                        Toast.LENGTH_LONG).show();
            }
        });
        //////////////////////////////////////////////////
        forgetPassword.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            user.setEmail(emailText.getText().toString().trim());
            CheckEmail();
            if(!Objects.requireNonNull(emailText.getText()).toString().trim().isEmpty()){
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailText.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "email sent", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
            else{
                emailLayout.setError(getString(R.string.emailErr));
                emailText.requestFocus();
                progressBar.setVisibility(View.GONE);
            }
        });
        ///////////////////////////////////////////////////////////
        loginButton.setOnClickListener(v -> {
            if(isCon()) {
                progressBar.setVisibility(View.VISIBLE);
                initializeUser();
                switch (user.userLoginValidation()){
                    case User.EMPTY_MAIL : {
                        emailErrorHandling(User.EMPTY_MAIL);
                        progressBar.setVisibility(View.GONE);
                    }
                    case User.EMPTY_PASSWORD:{
                        PasswordErrorHandling(User.EMPTY_PASSWORD);
                        progressBar.setVisibility(View.GONE);
                    }break;
                    case User.WRONG_PASSWORD:{
                        PasswordErrorHandling(User.WRONG_PASSWORD);
                        progressBar.setVisibility(View.GONE);
                    }break;
                    case User.EMAIL_WRONG_FORMAT:{
                        emailErrorHandling(User.EMAIL_WRONG_FORMAT);
                        progressBar.setVisibility(View.GONE);
                    }break;
                    case User.SUCCESSFUL:{
                        CheckEmail();
                    }break;
                }

            }else{
                Toast.makeText(LoginActivity.this, getString(R.string.connectionError) ,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    ////////////////////////////////
    public void CheckEmail(){
        FirebaseAuth mAuth  = FirebaseAuth.getInstance();
        mAuth.fetchSignInMethodsForEmail(user.getEmail()).addOnCompleteListener(task -> {
           try {
                if (task.getResult().getSignInMethods().isEmpty()) {
                    emailErrorHandling(User.EMAIL_NOT_REGISTERED);
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    passwordLoginCheck();
                }
            }catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                InputLayoutHandler.editTextHandler(emailText,emailLayout,getString(R.string.emailFormat));
            }

        });
    }
    ////////////////////////////
    private void passwordLoginCheck(){
        FirebaseAuth mAuth  = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful()) {
                    Intent i = new Intent(this, LabsList.class);
                    //Toast.makeText(this, "welcome : " + user.getEmail(), Toast.LENGTH_LONG).show();
                    startActivity(i);
                    finish();
                    Animatoo.animateCard(this);

                } else if (!task.isSuccessful()) {
                    PasswordErrorHandling(User.WRONG_PASSWORD);
                    progressBar.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
     ///////////////////////////
    private void PasswordErrorHandling(int errorType) {
        if (errorType == User.EMPTY_PASSWORD) {
            passLayout.setError(getString(R.string.emptyPass));
            passText.requestFocus();
        }
        else if (errorType == User.WRONG_PASSWORD){
            passLayout.setError(getString(R.string.wrongPass));
            passText.requestFocus();
        }
    }
     ///////////////////////////////////////////
    private void emailErrorHandling(int errorType) {
        if (errorType == User.EMPTY_MAIL) {
            emailText.setError(getString(R.string.emptyMail));
            emailText.requestFocus();
        }
        else if (errorType == User.EMAIL_NOT_REGISTERED){
            emailText.setError(getString(R.string.emailFormat));
            emailText.requestFocus();
            emailLayout.setError(getString(R.string.emailFormat));
        }
        else{
            emailText.setError(getString(R.string.emailFormat));
            emailText.requestFocus();
            emailLayout.setError("EX. example@some.thing");
        }
    }
    //////////////////////////////////////
    public void initializeUser(){
        user.setEmail(emailText.getText().toString());
        user.setPassword(passText.getText().toString());
    }
    ////////////////////////////////////
    public  boolean  isCon(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }
    /////////////////////////////////////
    public void userOrNot(){
        FirebaseUser firebaseUser= mAuth.getCurrentUser() ;
        if(firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this, LabsList.class));
            finish();
            Animatoo.animateCard(this);

        }

    }
    public void editTextListeners(){
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Objects.requireNonNull(emailText.getText()).toString().isEmpty()){
                    emailLayout.setError(getString(R.string.emptyMail));
                }
                else{
                    emailLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passText.getText().toString().isEmpty()){
                    passLayout.setError(getString(R.string.emptyPass));
                }
                else{
                    passLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}