package com.sci.labtrack.DatabaseHandler;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.User.User;

import java.util.ArrayList;

public class FirebaseDataHandler {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("labs");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference allUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    String currUserUID ;
    public User getCurrUser() {
        return currUser;
    }
    User currUser;
    ArrayList<Lab> labsList = new ArrayList<>();
    firebaseDataListener listener;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public FirebaseDataHandler(firebaseDataListener mListener){
        listener = mListener;
    }
    public void getData(){
        ////////////////////////
        ////////////getLabsList
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labsList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    labsList.add(ds.getValue(Lab.class));
                }
                listener.updateLaps(labsList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ////////////////////////////////////////////
        ////get all Users List
        allUsersRef.addValueEventListener(new ValueEventListener() {
            ArrayList<User> allFirebaseUsers = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    allFirebaseUsers.add(ds.getValue(User.class));
                }
                listener.getAllUsers(allFirebaseUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public Lab getLab(String labName){
        for(Lab lab: labsList){
            if(lab.getName().equals(labName)){
                return lab;
            }
        }
        return null;
    }
    public interface firebaseDataListener{
        public void updateLaps(ArrayList<Lab> LabsArrayList);
        public void getAllUsers(ArrayList<User> allUsers);
    }


}
