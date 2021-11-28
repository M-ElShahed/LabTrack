package com.sci.labtrack.ServicesAndTools;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sci.labtrack.DatabaseHandler.FirebaseDataHandler;
import com.sci.labtrack.DatabaseHandler.LabAsyncTask;
import com.sci.labtrack.DatabaseHandler.LabDao;
import com.sci.labtrack.DatabaseHandler.LabTrackDatabase;
import com.sci.labtrack.DatabaseHandler.PcAsyncTask;
import com.sci.labtrack.DatabaseHandler.UserAsyncTask;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.LoginActivity;
import com.sci.labtrack.PC.PC;

import com.sci.labtrack.R;
import com.sci.labtrack.User.User;

import java.util.ArrayList;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NotificationService extends Service implements FirebaseDataHandler.firebaseDataListener {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("labs");
    ArrayList<Lab> labArrayList = new ArrayList<>();
    NotificationHelper helper;
    SharedPreferences myShared   ;
    SharedPreferences.Editor mySharedEditor ;
    UserAsyncTask userAsyncTask;
    LabAsyncTask labAsyncTask;
    PcAsyncTask pcAsyncTask;
    FirebaseDataHandler dataHandler;
    @Override
    public void onCreate() {
        myShared  = PreferenceManager.getDefaultSharedPreferences(this) ;
        mySharedEditor = myShared.edit();
        dataHandler = new FirebaseDataHandler(this);
        dataHandler.getData();
        //Toast.makeText(getApplicationContext(),"Service On Create ",Toast.LENGTH_SHORT).show();
        helper = new NotificationHelper(getApplicationContext());
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                        try{
                       labArrayList.add(ds.getValue(Lab.class));
                        } catch (Exception e) {
                            Toast.makeText(helper, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                needRepairChecker();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        Intent intent=  new Intent(NotificationService.this,NotificationService.class);
        startService(intent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void needRepairChecker(){
        int i = -1;
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("labs");
        for(Lab lab : labArrayList){
            i=0;
            for(PC pc: lab.getPcList()){
                if(pc.getRam().equals("0")){
                    pc.setRamWorking(false);
                }
                if(pc.getHardDrive().equals("0")){
                    pc.setHardDiskWorking(false);
                }
                if(!pc.isMouse()||!pc.isMonitor()||!pc.isKeyBoard()||!pc.isRamWorking()||!pc.isProcessorWorking()||!pc.isHardDiskWorking()||!pc.isOperatingSystemStatue()
                ||!pc.getMissingSoftware().equals("")||!pc.getProblemDescription().equals("")){
                    pc.setNeedRepair(true);
                    mySharedEditor.putBoolean("gotNotification",false);
                    mySharedEditor.commit();
                    //newRef.child(lab.getName()).child("pcList").child(""+i).setValue(pc);
                }else{
                    pc.setNeedRepair(false);
                  //  newRef.child(lab.getName()).child("pcList").child(""+i).setValue(pc);
                }
                if((pc.isNeedRepair()) && !pc.isNotificationSent()&& !myShared.getBoolean("gotNotification",true)){
                    pc.setNotificationSent(true);
                 //   newRef.child(lab.getName()).child("pcList").child(""+i).setValue(pc);
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        notificationBuilder(lab, pc, i);
                    }
                    else{
                        notificationBuilderOreo(lab,pc);
                    }
                    mySharedEditor.putBoolean("gotNotification",true);
                    mySharedEditor.commit();
                }
                newRef.child(lab.getName()).child("pcList").child(""+i).setValue(pc);
                i++;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notificationBuilder(Lab lab, PC pc , int id){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        String message ="The pc: "+pc.getSerialNumber()+" in the lab: "+ lab.getName()+" Have a new problem "+pc.getProblemDescription();
        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle("A new lab needs repairing")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_edit)
                .setSound(alarmSound)
                .setContentIntent(pIntent)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, noti);

    }
    //// android Oreo and above
    public void notificationBuilderOreo(Lab lab, PC pc ){
        String title= "A new pc needs repairing";
        String body = "The pc: "+pc.getSerialNumber()+" in the lab: "+ lab.getName()+" Have a new problem"+pc.getProblemDescription();
        Notification.Builder builder = helper.getChannelNotiication(title,body);
        helper.getManger().notify(new Random().nextInt(),builder.build());
    }

    @Override
    public void updateLaps(ArrayList<Lab> LabsArrayList) {
        labAsyncTask = new LabAsyncTask(null,this);
        pcAsyncTask = new PcAsyncTask(null,this);
        pcAsyncTask.execute();
        labAsyncTask.execute();
        for(Lab lab :LabsArrayList){
            labAsyncTask = new LabAsyncTask(lab,this);
            labAsyncTask.execute();
          //  Toast.makeText(helper, lab.getName()+lab.getPcList().size(), Toast.LENGTH_SHORT).show();
            for(PC pc :lab.getPcList()){
                pcAsyncTask = new PcAsyncTask(pc,this);
               // Toast.makeText(helper, "heoi", Toast.LENGTH_SHORT).show();
                pcAsyncTask.execute();
            }
        }

    }


    @Override
    public void getAllUsers(ArrayList<User> allUsers) {
         for(User user: allUsers){
             userAsyncTask = new UserAsyncTask(user,this);
             userAsyncTask.execute(user);
         }
    }
}
