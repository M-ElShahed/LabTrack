package com.sci.labtrack.PC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sci.labtrack.DatabaseHandler.FirebaseDataHandler;
import com.sci.labtrack.ServicesAndTools.InputLayoutHandler;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.R;
import com.sci.labtrack.User.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PcAddAndEdit extends AppCompatActivity implements FirebaseDataHandler.firebaseDataListener{

    @BindView(R.id.pcDone)  Button Done;
    @BindView(R.id.serialText)TextInputEditText serial;
    @BindView(R.id.ramText) TextInputEditText ram;
    @BindView(R.id.HDDText) TextInputEditText HDD;
    @BindView(R.id.serialTextL)TextInputLayout serialLayout;
    @BindView(R.id.ramTextL) TextInputLayout ramLayout;
    @BindView(R.id.HDDTextL) TextInputLayout HDDLayout;
    @BindView(R.id.Mouse)    CheckBox mouse;
    @BindView(R.id.keyBoard) CheckBox keyboard;
    @BindView(R.id.GPU)      CheckBox gpu;
    @BindView(R.id.monitor)  CheckBox monitor;
    @BindView(R.id.spinner)  Spinner cpuList;
    @BindView(R.id.bDate)  TextView bDate;
    @BindView(R.id.numberInLab)TextInputEditText numberInLab;
    @BindView(R.id.numberInLabL)TextInputLayout numberInLabLayout;
    @BindView(R.id.generateButton)ImageButton generateButton;

    Lab editingLab = new Lab();
    int pcPos;
    String date ;
    PC pc = new PC();
    FirebaseDataHandler dataHandler ;
    private DatePickerDialog.OnDateSetListener onData;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference().child("labs");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_add);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        dataHandler = new FirebaseDataHandler(this);
        dataHandler.getData();
        pcPos = getIntent().getIntExtra("PcPos", -1);
        editingLab = (Lab) getIntent().getSerializableExtra("SelectedLab");

        /////////// Editing mode
        if(pcPos  != -1){
            pc = (PC) getIntent().getSerializableExtra("SelectedPc");
            updateContent();
        }else{
            pcPos = editingLab.getPcList().size();
        }
        /////////////////////////////
        editTextListeners();
        ////////////////////////////////
        generateButton.setOnClickListener(v -> {
            final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            int count = 11;
            StringBuilder builder = new StringBuilder();
            while (count-- != 0) {
                int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
                builder.append(ALPHA_NUMERIC_STRING.charAt(character));
            }
                serial.setText( builder.toString());
        });
        ///////////////////////////////
        CalendarBuilder();
        /////////////////////////////

        /////////////////////////////////////////
        Done.setOnClickListener(v -> {
            if(isCon()){
                pcInitialization();
                if(pc.pcValidation(editingLab,pcPos)==PC.SUCCESSFUL){
                    myRef.child(editingLab.getName()).child("pcList"). child("" + pcPos).setValue(pc);
                    onBackPressed();
                }
               else{
                    switch (pc.pcValidation(editingLab,pcPos)){
                        case PC.EMPTY_SERIAL:{
                            InputLayoutHandler.editTextHandler(serial,serialLayout,getString(R.string.cantbeempty));
                        }break;
                        case PC.EMPTY_NUMBER:{
                            InputLayoutHandler.editTextHandler(numberInLab,numberInLabLayout,getString(R.string.cantbeempty));
                        }break;
                        case PC.EMPTY_RAM:{
                            InputLayoutHandler.editTextHandler(ram,ramLayout,getString(R.string.cantbeempty));
                        }break;
                        case PC.EMPTY_HDD:{
                            InputLayoutHandler.editTextHandler(HDD,HDDLayout,getString(R.string.cantbeempty));
                        }break;
                        case PC.WRONG_HDD:{
                            InputLayoutHandler.editTextHandler(HDD,HDDLayout,getString(R.string.wrongHDD));
                        }break;
                        case PC.WRONG_RAM:{
                            InputLayoutHandler.editTextHandler(ram,ramLayout,getString(R.string.wrongRam));
                        }break;
                        case PC.NUMBER_OUT_OF_BOUNDARY:{
                            InputLayoutHandler.editTextHandler(numberInLabLayout,getString(R.string.numberBiggerCap));
                        }break;
                        case PC.REPEATED_NUMBER:{
                            InputLayoutHandler.editTextHandler(numberInLabLayout,getString(R.string.numberRepeated));
                        }break;
                    }
                }
            }else{
                Toast.makeText(this, getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        });
        //////////////////////////
    }
    public  void updateContent(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        serial.clearFocus();
        serial.setText(pc.getSerialNumber());
        ram.setText(""+pc.getRam());
        HDD.setText(""+pc.getHardDrive());
        mouse.setChecked(pc.isMouse());
        keyboard.setChecked(pc.isKeyBoard());
        gpu.setChecked(pc.isGpu());
        numberInLab.setText(pc.getNumberInLab());
//        bDate.setText(pc.getBuyingDate().getDate()+"/"+pc.getBuyingDate().getMonth()+"/"+pc.getBuyingDate().getYear());
      //  Toast.makeText(this, "Date "+pc.getBuyingDate().get(), Toast.LENGTH_SHORT).show();
        monitor.setChecked(pc.isMonitor());
        if(pc.getCpu().equals("core i3")){
            cpuList.setSelection(0);
        }
        else if(pc.getCpu().equals("core i5")){
            cpuList.setSelection(1);
        }
        else if(pc.getCpu().equals("core i7")){
            cpuList.setSelection(2);
        }
    }
    public void pcInitialization(){
        pc.setLabName(editingLab.getName());
        pc.setRam((ram.getText().toString().trim()));
        pc.setHardDrive((HDD.getText().toString().trim()));
        pc.setCpu(cpuList.getSelectedItem().toString());
        pc.setMouse(mouse.isChecked());
        pc.setKeyBoard(keyboard.isChecked());
        pc.setMonitor(monitor.isChecked());
        pc.setGpu(gpu.isChecked());
        pc.setBuyingDate(date);
        pc.setNumberInLab(numberInLab.getText().toString());
        pc.setSerialNumber(serial.getText().toString().trim());
    }
    public void editTextListeners(){
        InputLayoutHandler.editTextListeners(serial,serialLayout);
        InputLayoutHandler.editTextListeners(ram,ramLayout);
        InputLayoutHandler.editTextListeners(numberInLab,numberInLabLayout);
        InputLayoutHandler.editTextListeners(HDD,HDDLayout);
    }
    public void CalendarBuilder(){
        bDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year=cal.get(Calendar.YEAR);
            int month=cal.get(Calendar.MONTH);
            int day=cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(PcAddAndEdit.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onData,
                    year,month,day);
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        });
        onData = (view, year, month, dayOfMonth) -> {
            month=month+1;
            date = dayOfMonth+"/"+month+"/"+year;
            String date=dayOfMonth+"/"+month+"/"+year;
            bDate.setText(date);

        };
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateCard(this);
    }

    @Override
    public void updateLaps(ArrayList<Lab> LabsArrayList) {
        editingLab  = dataHandler.getLab(editingLab.getName());
        if (pcPos !=editingLab.getPcList().size()&& pcPos>-1){
            pc = editingLab.getPcList().get(pcPos);
            updateContent();
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
