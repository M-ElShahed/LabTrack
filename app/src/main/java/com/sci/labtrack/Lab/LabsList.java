package com.sci.labtrack.Lab;

import android.app.Notification.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sci.labtrack.DatabaseHandler.LabDao;
import com.sci.labtrack.DatabaseHandler.LabTrackDatabase;
import com.sci.labtrack.DatabaseHandler.PcDao;
import com.sci.labtrack.DatabaseHandler.UserDao;
import com.sci.labtrack.LoginActivity;
import com.sci.labtrack.PC.PcList;
import com.sci.labtrack.R;
import com.sci.labtrack.ServicesAndTools.NotificationService;
import com.sci.labtrack.User.User;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LabsList extends AppCompatActivity {
    //
    @BindView(R.id.labslist)
    ListView labsListView;
    @BindView(R.id.progressBar3)
    ProgressBar connectingProgressBar;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.connectingLinear)
    LinearLayout connectionBar;
    @BindView(R.id.connectingText)
    TextView connecting;
    @BindView(R.id.bottomNav)
    BottomNavigationView bottomNav;
    //
    LabListAdapter adapter;
    ArrayList<Lab> labArrayList = new ArrayList<>();
    User currentLoginUser = new User();
    LabTrackDatabase myDB;
    LabDao labDao;
    PcDao pcDao;
    UserDao userDao;
    AlertDialog myDialogue = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_labs);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.labsList));
        super.onCreate(savedInstanceState);

        //////
        startService();
        //////////////////////Monitoring network changes
        dataConnectionMonitor();
        //////// initializing adapter
        adapter = new LabListAdapter(labArrayList, LabsList.this);
        labsListView.setAdapter(adapter);
        ///////////////initializing database
        myDB = LabTrackDatabase.getDatabase(this);
        labDao = myDB.labDao();
        pcDao = myDB.pcDao();
        userDao = myDB.userDao();
        getLapsData();
        getCurrentUser();

        ///////////////////////////
        labsListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(LabsList.this, PcList.class);
            //Toast.makeText(this, ""+labArrayList.get(position).getPcList().size(), Toast.LENGTH_SHORT).show();
            i.putExtra("SelectedLab", labArrayList.get(position));
            startActivity(i);
            Animatoo.animateCard(this);
        });
        floatingActionButton.setOnClickListener(v -> {

        });
        ////////// start notifications Background service
        bottomNav.setSelectedItemId(R.id.addIcon);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id  = item.getItemId();
            switch (id){
                case R.id.addIcon:{
                    startActivity(new Intent(LabsList.this, AddingAndEditingLab.class));
                }break;
                case R.id.makeBackup:{
                    if(isCon()) {
                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                        myBuilder.setMessage(getString(R.string.upload_backup_dialogue));
                        myBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            DatabaseReference backupRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference().child("labsBackup");
                            backupRef.child("labs").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                                        destinationRef.child(ds.getKey()).setValue(ds.getValue());
                                    }
                                    Toast.makeText(LabsList.this, "Backup Done ", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        });
                        myBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> myDialogue.cancel());
                        myDialogue = myBuilder.create();
                        myDialogue = myBuilder.show();
                }else{
                    Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
                }break;
                case R.id.restoreBackup:{
                    if(isCon()) {
                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                        myBuilder.setMessage(getString(R.string.restore_backup_dialogue));
                        myBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            DatabaseReference backupRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference().child("labs");
                            backupRef.child("labsBackup").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        destinationRef.child(ds.getKey()).setValue(ds.getValue());
                                    }
                                    Toast.makeText(LabsList.this, "Backup restored ", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        });
                        myBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> myDialogue.dismiss());
                        myDialogue = myBuilder.create();
                        myDialogue = myBuilder.show();
                    }else{
                        Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return false;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signout) {
            finish();
            FirebaseAuth.getInstance().signOut();
            Animatoo.animateShrink(this);
            startActivity(new Intent(LabsList.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Animatoo.animateCard(this);
        finish();
    }

    public void startService() {
        Intent serviceIntent = new Intent(LabsList.this, NotificationService.class);
        startService(serviceIntent);
    }

    public void dataConnectionMonitor() {
        DatabaseReference connected = FirebaseDatabase.getInstance().getReference(".info/connected");
        connected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("true")) {
                    connecting.setText("Connected");
                    View view = connectionBar;
                    view.postDelayed(() ->
                            view.setVisibility(View.GONE), 2000);
                    connectingProgressBar.setVisibility(View.GONE);
                } else if (dataSnapshot.getValue().toString().equals("false")) {
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

    public void getLapsData() {
        labDao.getLabs().observe(this, labs -> {
            labArrayList.clear();
            labArrayList.addAll(labs);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < labArrayList.size(); i++) {
                int x = i;
                // Toast.makeText(this, labArrayList.get(i).getName()+labArrayList.get(i).getPcList().size(), Toast.LENGTH_SHORT).show();
                pcDao.getPcList(labArrayList.get(i).getName(), "numberinLab").observe(LabsList.this, pcs -> {
                    if (x < labArrayList.size()) {
                        labArrayList.get(x).setPcList(pcs);
                        //Toast.makeText(this, "asd", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        });

    }

    public void getCurrentUser() {
        currentLoginUser.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        try {
            userDao.getCurrentUser(currentLoginUser.getUID()).observe(this, user -> {
                currentLoginUser = user;
                if (currentLoginUser != null) {
                    Toast.makeText(LabsList.this, "Welcome " + currentLoginUser.getName(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  boolean  isCon(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }

}
