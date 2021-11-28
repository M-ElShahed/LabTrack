package com.sci.labtrack.PC;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.R;
import com.sci.labtrack.DatabaseHandler.FirebaseDataHandler;
import com.sci.labtrack.User.User;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PcInfo extends AppCompatActivity implements FirebaseDataHandler.firebaseDataListener {

    @BindView(R.id.serial1)    TextView serialText;
    @BindView(R.id.buy1)    TextView buyDateText;
    @BindView(R.id.proc1)    TextView processorText;
    @BindView(R.id.processorState)    TextView processorState;
    @BindView(R.id.ram1)    TextView ramText;
    @BindView(R.id.ramState)    TextView ramState;
    @BindView(R.id.hard1)    TextView hardText;
    @BindView(R.id.hardState)    TextView hardState;
    @BindView(R.id.mouseState)    TextView mouseState;
    @BindView(R.id.keyboardState)    TextView keyboardState;
    @BindView(R.id.monitorState)    TextView screenState;
    @BindView(R.id.gpuState)    TextView gpuState;
    @BindView(R.id.win1)    TextView windowsText;
    @BindView(R.id.stateW)    TextView windowsState;
    @BindView(R.id.prog)    TextView programsText;
    @BindView(R.id.pcImg)    Button pcImg;

    PC pc ;
    Lab lab  ;
    int pcPos;
    FirebaseDataHandler dataHandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_info);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        dataHandler =new FirebaseDataHandler(this);
        dataHandler.getData();
        ///////
        lab = (Lab) getIntent().getSerializableExtra("SelectedLab");
        pc = (PC) getIntent().getSerializableExtra("PcInfo");
        pcPos = getIntent().getIntExtra("PcPos",-1);
        ////
        setPcData();
    }
    public void setPcData(){
        serialText.setText(pc.getSerialNumber());
//        buyDateText.setText(pc.getBuyingDate().getDate()+"/"+pc.getBuyingDate().getMonth()+"/"+pc.getBuyingDate().getYear());
        processorText.setText(pc.getCpu());
        ramText.setText(pc.getRam()+" GB");
        hardText.setText(pc.getHardDrive()+" GB");
        pcImg.setText(pc.getNumberInLab());
        if(pc.isMouse()){
            mouseState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }
        else{
            mouseState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
        if(pc.isKeyBoard()){
            keyboardState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }
        else{
            keyboardState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
        if(pc.isMonitor()){
            screenState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }
        else{
            screenState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
        if(pc.isGpu()){
            gpuState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }
        else{
            gpuState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
        if(pc.isHardDiskWorking()){
            hardState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }else{
            hardState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
        if(pc.isRamWorking()){
            ramState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }else{
            ramState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
        if(pc.isProcessorWorking()){
            processorState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
        }else{
            processorState.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        MenuItem edit = menu.findItem(R.id.edit);
        MenuItem delete = menu.findItem(R.id.delete);
        edit.setVisible(true);
        edit.setIcon(R.drawable.ic_edit);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        delete.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId()== R.id.edit){
            Intent i = new Intent(PcInfo.this,PcAddAndEdit.class);
            i.putExtra("SelectedLab",lab);
            i.putExtra("PcPos",getIntent().getIntExtra("PcPos",-1));
            i.putExtra("SelectedPc",pc);
           // Toast.makeText(this, ""+lab.getPcList().indexOf(pc), Toast.LENGTH_SHORT).show();
            Animatoo.animateInAndOut(this);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideDown(this);
    }

    @Override
    public void updateLaps(ArrayList<Lab> LabsArrayList) {
        lab = dataHandler.getLab(lab.getName());
        pc = lab.getPcList().get(pcPos);
        setPcData();
    }



    @Override
    public void getAllUsers(ArrayList<User> allUsers) {

    }
}
