package com.sci.labtrack.PC;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sci.labtrack.R;
import java.util.Objects;

public class ReportProblemDialogue extends DialogFragment {
    RadioButton hardwareRadio;
    RadioButton softwareRadio;
    RadioButton otherRadio;
    CheckBox cpuCheck;
    CheckBox ramCheck;
    CheckBox hddCheck;
    CheckBox mouseCheck;
    CheckBox keyboardCheck;
    CheckBox monitorCheck;
    CheckBox windowsCheck;
    CheckBox programsCheck;
    RelativeLayout softwareList;
    ConstraintLayout hardwareList;
    Button cancelButton;
    Button saveButton;
    EditText problemDescription;
    EditText missingPrograms;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("labs");
    PC pcReport;
    int posReport;

    public ReportProblemDialogue(PC pcReport, int posReport) {
        this.pcReport = pcReport;
        this.posReport = posReport;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alertdialogue_report, null);
        hardwareRadio = view.findViewById(R.id.hardwareRadio);
        cpuCheck = view.findViewById(R.id.cpuCheck);
        ramCheck = view.findViewById(R.id.ramCheck);
        hddCheck = view.findViewById(R.id.hddCheck);
        mouseCheck = view.findViewById(R.id.mouseCheck);
        keyboardCheck = view.findViewById(R.id.keyboardCheck);
        monitorCheck = view.findViewById(R.id.monitorCheck);
        windowsCheck = view.findViewById(R.id.windowsCheck);
        programsCheck = view.findViewById(R.id.programsCheck);
        hardwareList= view.findViewById(R.id.hardwareList);
        softwareList = view.findViewById(R.id.softwareList);
        saveButton = view.findViewById(R.id.saveButton);
        softwareRadio = view.findViewById(R.id.softwareRadio);
        otherRadio = view.findViewById(R.id.otherRadio);
        cancelButton = view.findViewById(R.id.cancelButton);
        missingPrograms = view.findViewById(R.id.missingSoftware);
        problemDescription = view.findViewById(R.id.problemDescription);
        initializeData();
        if (!windowsCheck.isChecked()){
            programsCheck.setEnabled(false);
            programsCheck.setChecked(false);
        }
        windowsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!windowsCheck.isChecked()){
                programsCheck.setEnabled(false);
                programsCheck.setChecked(false);
                missingPrograms.setVisibility(View.GONE);
            }else{
                programsCheck.setEnabled(true);
            }
            if(!programsCheck.isChecked()&&programsCheck.isEnabled()){
                missingPrograms.setVisibility(View.VISIBLE);
            }
        });
        hardwareRadio.setOnClickListener(v ->{
            missingPrograms.setVisibility(View.GONE);
            hardwareList.setVisibility(View.VISIBLE);
            problemDescription.setVisibility(View.GONE);
            softwareList.setVisibility(View.GONE);
        } );
        programsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(programsCheck.isChecked()){
                missingPrograms.setVisibility(View.GONE);
            }else{
                missingPrograms.setVisibility(View.VISIBLE);
            }
        });
        softwareRadio.setOnClickListener(v->{
           softwareList.setVisibility(View.VISIBLE);
            hardwareList.setVisibility(View.GONE);
            problemDescription.setVisibility(View.GONE);
            if(programsCheck.isChecked()){
                missingPrograms.setVisibility(View.GONE);
            }else if(!programsCheck.isChecked()&&programsCheck.isEnabled()){
                missingPrograms.setVisibility(View.VISIBLE);
            }

        } );
        otherRadio.setOnClickListener(v ->{
            missingPrograms.setVisibility(View.GONE);
            hardwareList.setVisibility(View.GONE);
            problemDescription.setVisibility(View.VISIBLE);
            softwareList.setVisibility(View.GONE);
        } );
        cancelButton.setOnClickListener(v -> {
            getDialog().dismiss();
        });
        saveButton.setOnClickListener(v -> {
            if(isCon()) {
                pcReport.setProcessorWorking(cpuCheck.isChecked());
                pcReport.setRamWorking(ramCheck.isChecked());
                pcReport.setHardDiskWorking(hddCheck.isChecked());
                pcReport.setMouse(mouseCheck.isChecked());
                pcReport.setKeyBoard(keyboardCheck.isChecked());
                pcReport.setMonitor(monitorCheck.isChecked());
                pcReport.setProblemDescription(problemDescription.getText().toString());
                pcReport.setOperatingSystemStatue(windowsCheck.isChecked());
                pcReport.setMissingSoftware(missingPrograms.getText().toString());
                myRef.child(pcReport.getLabName()).child("pcList").child("" + posReport).setValue(pcReport);
                getDialog().dismiss();
            }else{
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        });

        getDialog().setCancelable(false);
        return view;
    }
    public void initializeData(){
        windowsCheck.setChecked(pcReport.isOperatingSystemStatue());
        if(pcReport.getMissingSoftware().equals("")){
            programsCheck.setChecked(true);
        }
        else{
            programsCheck.setChecked(false);
            missingPrograms.setText(pcReport.getMissingSoftware());
        }
        cpuCheck.setChecked(pcReport.isProcessorWorking());
        ramCheck.setChecked(pcReport.isRamWorking());
        hddCheck.setChecked(pcReport.isHardDiskWorking());
        mouseCheck.setChecked(pcReport.isMouse());
        keyboardCheck.setChecked(pcReport.isKeyBoard());
        monitorCheck.setChecked(pcReport.isMonitor());
        problemDescription.setText(pcReport.getProblemDescription());
        // initialize colors
        if(!windowsCheck.isChecked()||!programsCheck.isChecked()){
            softwareRadio.setTextColor(getResources().getColor(R.color.colorAccent));
        }else{
            softwareRadio.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if(!ramCheck.isChecked()||!cpuCheck.isChecked()||!hddCheck.isChecked()||!mouseCheck.isChecked()||!monitorCheck.isChecked()||!keyboardCheck.isChecked()){
            hardwareRadio.setTextColor(getResources().getColor(R.color.colorAccent));
        }else{
            hardwareRadio.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if(!problemDescription.getText().toString().isEmpty()){
            otherRadio.setTextColor(getResources().getColor(R.color.colorAccent));
        }else{
            otherRadio.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        // check boxes
        if(windowsCheck.isChecked()){
            windowsCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
            windowsCheck.setHighlightColor(getResources().getColor(R.color.colorPrimary));
        }else{
            windowsCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(programsCheck.isChecked()){
            programsCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            programsCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(cpuCheck.isChecked()){
            cpuCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            cpuCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(ramCheck.isChecked()){
            ramCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            ramCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(hddCheck.isChecked()){
            hddCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            hddCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(mouseCheck.isChecked()){
            mouseCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            mouseCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(keyboardCheck.isChecked()){
            keyboardCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            keyboardCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if(monitorCheck.isChecked()){
            monitorCheck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            monitorCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        }

    }
    public  boolean  isCon(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }
}
