package com.sci.labtrack.PC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sci.labtrack.DatabaseHandler.LabTrackDatabase;
import com.sci.labtrack.DatabaseHandler.PcDao;
import com.sci.labtrack.Lab.AddingAndEditingLab;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.LoginActivity;
import com.sci.labtrack.R;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PcList extends AppCompatActivity  {

    @BindView(R.id.myrc) RecyclerView myRC ;
    @BindView(R.id.floatingActionButton) FloatingActionButton floatingActionButton;
    MenuItem editPc;
    MenuItem counter;
    MenuItem sortMenu;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("labs");
    ArrayList<PC> pcArrayList = new ArrayList<>();
    ArrayList<PC> removeList = new ArrayList<>();
    Lab lab = new Lab();
    PcAdapter adapter;
    int currPos;
    boolean multiSelect;
    ActionMode mMode= null;
    SharedPreferences mySharedPreferences ;
    SharedPreferences.Editor editor;
    LabTrackDatabase myDB;
    PcDao pcDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mySharedPreferences.edit();
        setContentView(R.layout.activity_pc_list);
        ButterKnife.bind(this);
        myDB = LabTrackDatabase.getDatabase(this);
        pcDao = myDB.pcDao();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        lab = (Lab) getIntent().getSerializableExtra("SelectedLab");
        invalidateOptionsMenu();
        getSupportActionBar().setTitle(getString(R.string.pcList)+ lab.getName());
        pcArrayList = (ArrayList<PC>) lab.getPcList();
        adapter = new PcAdapter(pcArrayList,listener);
        myRC.setLayoutManager(new GridLayoutManager(PcList.this,2));
       /* myRC.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom= 0;
                outRect.top = 1;
                outRect.set(1,1,1,1);
            }
        });*/
        adapter.notifyDataSetChanged();
        myRC.setAdapter(adapter);
        updateList("numberInLab");
        floatingActionButton.setOnClickListener(v -> {
            startActivity(new Intent(PcList.this, PcAddAndEdit.class).
                    putExtra("labname", lab.getName()).putExtra("SelectedLab", lab));
            Animatoo.animateCard(this);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        MenuItem item = menu.findItem(R.id.edit);
        item.setVisible(true);
        MenuItem backup = menu.findItem(R.id.edit);
        backup.setVisible(true);
        sortMenu = menu.findItem(R.id.sortMenu);
        sortMenu.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        switch (id){
            case R.id.signout : {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(PcList.this, LoginActivity.class));
                Animatoo.animateShrink(this);
            }break;
            case android.R.id.home :
                onBackPressed();
                break;
            case R.id.edit:
                startActivity(new Intent(PcList.this, AddingAndEditingLab.class).putExtra("SelectedLab", lab));
                Animatoo.animateCard(this)
                ;break;
            case R.id.numberSort :{
                updateList("numberInLab");
             //   Toast.makeText(this, "numberInLab", Toast.LENGTH_SHORT).show();
            }break;
            case R.id.notworkingSort :{
                updateList("needrepair");
                //Toast.makeText(this, "needrepair", Toast.LENGTH_SHORT).show();
            }
            case R.id.workingSort :{
                updateList("needRepair");
             //   Toast.makeText(this, "needrepair", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.delete_menu,menu);
            counter  = menu.findItem(R.id.deletecounter);
            editPc = menu.findItem(R.id.editPc);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id= item.getItemId();
            switch (id){
                case R.id.deletePc:{
                    ArrayList<PC> temp = new ArrayList<>();
                    for(PC pc :pcArrayList){
                        if(!pc.isSelection()){
                            temp.add(pc);
                        }
                        myRef.child(lab.getName()).child("pcList").setValue(temp);
                    }
                    listener.longClickActivated(false);
                    removeList.clear();
                    if (mMode != null ) {
                        mMode.finish();
                    }
                }break;
                case R.id.editPc:{
                    finish();
                    for(PC pc :pcArrayList){
                        if(pc.isSelection()){
                            currPos = pcArrayList.indexOf(pc);
                        }
                    }
                    PC pc = pcArrayList.get(currPos);
                    startActivity(new Intent(PcList.this, PcAddAndEdit.class).
                            putExtra("PcPos",currPos).putExtra("SelectedLab", lab).putExtra("SelectedPc",pc));
                    Animatoo.animateCard(PcList.this);

                }break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMode= null;
            listener.longClickActivated(false);
        }
    };
    PcAdapter.Listener listener = new PcAdapter.Listener() {
        @Override
        public void onStateChange(boolean state, PC pc, int pos) {
            if(multiSelect) {
                if (state) {
                    pcArrayList.get(pos).setSelection(true);
                    adapter.notifyDataSetChanged();
                    removeList.add(pc);
                    counter.setTitle(""+removeList.size());
                    if(removeList.size()==1){
                        editPc.setVisible(true);
                    }else {
                        editPc.setVisible(false);
                    }
                } else {
                    removeList.remove(pc);
                    pcArrayList.get(pos).setSelection(false);
                    counter.setTitle(""+removeList.size());
                    adapter.notifyDataSetChanged();
                    if(removeList.size()==1){
                        editPc.setVisible(true);
                        currPos =pos;
                    }else {
                        editPc.setVisible(false);
                    }
                    if(removeList.size() == 0){
                        if (mMode != null ) {
                            mMode.finish();
                        }
                    }

                }
            }
            else{
                currPos =pos;
                Intent i = new Intent(PcList.this,PcInfo.class);
                i.putExtra("PcInfo",pc);
                i.putExtra("SelectedLab",lab);
                i.putExtra("PcPos",currPos);
                startActivity(i);
                Animatoo.animateSlideUp(PcList.this);
            }
        }
        @Override
        public void longClickActivated(boolean activated) {
            if(activated){
                 //Toast.makeText(PcList.this, "Longclick activated", Toast.LENGTH_SHORT).show();
                mMode = startSupportActionMode(actionModeCallbacks);
                multiSelect = true;
                counter.setVisible(true);
            }
            else {
                removeList.clear();
                multiSelect = false;
                for (PC pc : pcArrayList) {
                    pc.setSelection(false);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onReportClick(PC pc, int pos, ArrayList<PC> pcArrayList) {
            ReportProblemDialogue dialog = new ReportProblemDialogue(pc,pos);
            dialog.show(getSupportFragmentManager(),"myDialog");
            dialog.setCancelable(false);
        }


    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateCard(this);
    }
    public void updateList(String order){
        if(order.equals("needrepair")){
            pcDao.getPcListDesc(lab.getName(),order).observe(this, pcs -> {
                pcArrayList.clear();
                pcArrayList.addAll(pcs);
           //     Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            });

        }else {
            pcDao.getPcList(lab.getName(), order).observe(this, pcs -> {
                pcArrayList.clear();
                pcArrayList.addAll(pcs);
                adapter.notifyDataSetChanged();
            });
        }

    }

}
