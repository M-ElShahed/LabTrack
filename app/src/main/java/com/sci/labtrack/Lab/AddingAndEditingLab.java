package com.sci.labtrack.Lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sci.labtrack.DatabaseHandler.FirebaseDataHandler;
import com.sci.labtrack.ServicesAndTools.InputLayoutHandler;
import com.sci.labtrack.R;
import com.sci.labtrack.User.User;


import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddingAndEditingLab extends AppCompatActivity implements FirebaseDataHandler.firebaseDataListener {
    //
    @BindView(R.id.labname) TextInputEditText newName;
    @BindView(R.id.maxCapacity)TextInputEditText maxCap;
    @BindView(R.id.location) TextInputEditText location;
    @BindView(R.id.deletelab)Button delete ;
    @BindView(R.id.save)Button save;
    @BindView(R.id.labnameL) TextInputLayout newNameLayout;
    @BindView(R.id.maxCapacityL)TextInputLayout maxCapLayout;
    @BindView(R.id.locationL) TextInputLayout locationLayout;
    //
    Lab newLab ;
    String oldName = null;
    boolean addingMode;
    FirebaseDataHandler dataHandler;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("labs");
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_editing_lab);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        dataHandler =new  FirebaseDataHandler(this);
        newLab = (Lab) getIntent().getSerializableExtra("SelectedLab");
        if (newLab == null) {
            addingMode = true;
            newLab = new Lab();
        }
        //
        getSupportActionBar().setTitle(getString(R.string.addingLap));
        //
        if (!addingMode) {
           setContent();
            delete.setVisibility(View.VISIBLE);
        }
        delete.setOnClickListener(v -> {
            if(isCon()) {
                myRef.child(newLab.getName()).removeValue();
                finish();
                startActivity(new Intent(AddingAndEditingLab.this, LabsList.class));
                Animatoo.animateCard(this);
                Toast.makeText(AddingAndEditingLab.this, "Removed", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        });
        save.setOnClickListener(v -> {
            if(isCon()){
                newLab.setName(newName.getText().toString().trim());
                newLab.setLocation(location.getText().toString());
                try {
                    newLab.setMaxCap(Integer.parseInt(maxCap.getText().toString()));
                } catch (ArithmeticException e) {
                    InputLayoutHandler.editTextHandler(maxCap,maxCapLayout,getString(R.string.labmaxCapacity));
                }
                if(Integer.parseInt(maxCap.getText().toString())>50||Integer.parseInt(maxCap.getText().toString())<=0){
                    InputLayoutHandler.editTextHandler(maxCap,maxCapLayout,getString(R.string.labLimit));
                }else {
                    checkLabName();
                }
            }else{
                Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setContent(){
        getSupportActionBar().setTitle(getString(R.string.edit)+ newLab.getName() );
        oldName = newLab.getName();
        newName.setText(newLab.getName());
        maxCap.setText("" + newLab.getMaxCap());
        location.setText(newLab.getLocation());
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        switch (id){
            case android.R.id.home :
              onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateCard(this);
    }
    public void checkLabName(){
        try {
            myRef.child(newLab.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("name").getValue() != null) {
                        if (oldName != null) {
                            if (!dataSnapshot.child("name").getValue().equals(oldName)) {
                                InputLayoutHandler.editTextHandler(newName, newNameLayout, getString(R.string.labExist));
                            } else {
                                addOrEditLab();
                            }
                        } else {
                            InputLayoutHandler.editTextHandler(newName, newNameLayout, getString(R.string.labExist));
                        }
                    } else {
                        addOrEditLab();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            InputLayoutHandler.editTextHandler(newName,newNameLayout,getString(R.string.alphaOnly));
        }
    }
    public void addOrEditLab(){
        DatabaseReference labRef = FirebaseDatabase.getInstance().getReference().child("labs");
        DatabaseReference labRef2 = FirebaseDatabase.getInstance().getReference().child("labs");
        if(newLab.getName().length()!=0) {
            try {
                if(!addingMode) {
                    labRef2.child(oldName).removeValue();
                }
                labRef.child(newLab.getName()).setValue(newLab);
                startActivity(new Intent(AddingAndEditingLab.this, LabsList.class));
                Animatoo.animateCard(this);
                finish();
            } catch (Exception e) {
                InputLayoutHandler.editTextHandler(newName,newNameLayout,getString(R.string.alphaOnly));
            }
        }
        else{
            InputLayoutHandler.editTextHandler(newName,newNameLayout,getString(R.string.enterLabName));
        }
    }

    @Override
    public void updateLaps(ArrayList<Lab> LabsArrayList) {
        if(dataHandler.getLab(newLab.getName())==null&& !addingMode){
            Intent i = new Intent(this,LabsList.class);
            startActivity(i);
        }
    }

    @Override
    public void getAllUsers(ArrayList<User> allUsers) {

    }
    public  boolean  isCon(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }
}
