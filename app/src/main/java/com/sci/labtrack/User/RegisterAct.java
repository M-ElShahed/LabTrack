package com.sci.labtrack.User;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sci.labtrack.Lab.LabsList;
import com.sci.labtrack.R;
import com.sci.labtrack.ServicesAndTools.InputLayoutHandler;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterAct extends AppCompatActivity {
    User user = new User();
    boolean connectedFlag;

    @BindView(R.id.progressBar) ProgressBar progressBar ;
    @BindView(R.id.email)       TextInputEditText emailText;
    @BindView(R.id.pass)        TextInputEditText  passwordText;
    @BindView(R.id.name)        TextInputEditText  nameText;
    @BindView(R.id.mob)         TextInputEditText   phoneText;
    @BindView(R.id.confirm)     TextInputEditText confirmPasswordText;
    @BindView(R.id.emailLayout) TextInputLayout emailLayout;
    @BindView(R.id.passLayout)  TextInputLayout passwordLayout;
    @BindView(R.id.nameLayout)  TextInputLayout nameLayout;
    @BindView(R.id.confirmLayout) TextInputLayout confirmPasswordLayout;
    @BindView(R.id.male)        RadioButton male;
    @BindView(R.id.female)      RadioButton female;
    @BindView(R.id.submit)      Button submit;
    @BindView(R.id.mobLayout)   TextInputLayout phoneLayout;
    @BindView(R.id.connectingLinear) LinearLayout connectionBar;
    @BindView(R.id.progressBar3) ProgressBar connectingProgressBar;
    @BindView(R.id.connectingText)
    TextView connecting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        ButterKnife.bind(this);

        ///for Text change Listeners
        TextChangeListeners();
        dataConnectionMonitor();
            // Register button action
            submit.setOnClickListener(v -> {
                if(connectedFlag) {
                    progressBar.setVisibility(View.VISIBLE);
                    initializeUser();
                    switch (user.userValidation()){
                        case User.EMPTY_NAME:{
                            InputLayoutHandler.editTextHandler(nameText,nameLayout,getString(R.string.emptyName));
                            progressBar.setVisibility(View.GONE);
                        }
                        case User.EMPTY_MAIL:{
                            InputLayoutHandler.editTextHandler(emailText,emailLayout,getString(R.string.emptyMail));
                            progressBar.setVisibility(View.GONE);
                        }
                        case User.EMPTY_PASSWORD:{
                            InputLayoutHandler.editTextHandler(passwordLayout,getString(R.string.emptyPass));
                            progressBar.setVisibility(View.GONE);
                        }
                        case User.WRONG_CONFIRM:{
                            InputLayoutHandler.editTextHandler(confirmPasswordText,confirmPasswordLayout,getString(R.string.passwordMatch));
                            progressBar.setVisibility(View.GONE);
                        }break;
                        case User.EMAIL_WRONG_FORMAT:{
                            InputLayoutHandler.editTextHandler(emailText,getString(R.string.emailFormat),phoneLayout,"Ex. some@thing.com");
                            progressBar.setVisibility(View.GONE);
                        }break;
                        case User.PASSWORD_LENGTH:{
                            InputLayoutHandler.editTextHandler(passwordLayout,getString(R.string.passwordLength));
                            progressBar.setVisibility(View.GONE);
                        }break;
                        case User.PHONE_LENGTH_ERROR:{
                            InputLayoutHandler.editTextHandler(phoneText,getString(R.string.phoneLength),phoneLayout,"Ex. 01234567891");
                            progressBar.setVisibility(View.GONE);
                        }break;
                        case User.PHONE_WRONG_FORMAT:{
                           InputLayoutHandler.editTextHandler(phoneText,phoneLayout,getString(R.string.phoneFormat));
                            progressBar.setVisibility(View.GONE);
                        }break;
                        case User.SUCCESSFUL:{
                            CheckEmail();
                        }
                    }


                }
                else{
                    if(!isCon()){
                        Toast.makeText(RegisterAct.this, getString(R.string.connectionError) ,
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(RegisterAct.this, getString(R.string.connectionError),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });


    }
    public void CheckEmail(){
        FirebaseAuth mAuth  = FirebaseAuth.getInstance();
        try {
            mAuth.fetchSignInMethodsForEmail(user.getEmail()).addOnCompleteListener(task -> {
                if(task.getResult().getSignInMethods().isEmpty()){
                    registerUser();
                }
                else{
                    InputLayoutHandler.editTextHandler(emailText,emailLayout,getString(R.string.emailFormat));
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void registerUser() {
         FirebaseAuth mAuth= FirebaseAuth.getInstance();
        try {
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword());
            mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    myRef.child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).setValue(user);
                Toast.makeText(RegisterAct.this, "Registration Successful "+user.getName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterAct.this, LabsList.class));
                finish();
                Animatoo.animateCard(this);
            });
        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public void TextChangeListeners(){
    confirmPasswordText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String passwordString= passwordText.getText().toString();
            confirmPasswordLayout.setError("");
            if(confirmPasswordText.getText().toString().equals(passwordString)){
                confirmPasswordText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
            }
            else{
                confirmPasswordText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_open_black_24dp,0,0,0);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    /////////////////
    InputLayoutHandler.focusChangeListeners(passwordText,passwordLayout);
    //////////////////////
    InputLayoutHandler.editTextListeners(emailText,emailLayout);
    InputLayoutHandler.focusChangeListeners(emailText,emailLayout);
    ////////////////////
    InputLayoutHandler.editTextListeners(nameText,nameLayout);
    InputLayoutHandler.focusChangeListeners(nameText,nameLayout);
    //////////////////////////////
}
    public void initializeUser(){
        user.setName(Objects.requireNonNull(nameText.getText()).toString().trim());
        user.setEmail(Objects.requireNonNull(emailText.getText()).toString().trim());
        user.setPassword(Objects.requireNonNull(passwordText.getText()).toString().trim());
        user.setConfirmPassword(Objects.requireNonNull(confirmPasswordText.getText()).toString().trim());
        user.setPhone((phoneText.getText().toString()));
        user.setPosition(false);
        user.setGender(male.isSelected());
    }
    public void dataConnectionMonitor(){
        DatabaseReference connected = FirebaseDatabase.getInstance().getReference(".info/connected");
        connected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("true") ){
                    connectedFlag = true;
                    connecting.setText("Connected");
                    View view = connectionBar;
                    view.postDelayed(() ->
                            view.setVisibility(View.GONE),2000);
                    connectingProgressBar.setVisibility(View.GONE);
                }
                else if(dataSnapshot.getValue().toString().equals("false")){
                    connectedFlag = false;
                    connecting.setText("Connecting");
                    connectingProgressBar.setVisibility(View.VISIBLE);
                    connectionBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public  boolean  isCon(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(Objects.requireNonNull(connectivityManager).getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateCard(this);
    }
}