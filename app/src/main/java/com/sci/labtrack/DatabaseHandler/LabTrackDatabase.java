package com.sci.labtrack.DatabaseHandler;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.PC.PC;
import com.sci.labtrack.User.User;

@Database(entities = {User.class, Lab.class,PC.class}, version = 1)
public abstract class LabTrackDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract LabDao labDao();
    public abstract PcDao pcDao();
    public static volatile LabTrackDatabase labTrackInstance;
    public static LabTrackDatabase getDatabase(final Context context){
        if(labTrackInstance == null){
            synchronized (LabTrackDatabase.class){
                if(labTrackInstance == null ){
                    labTrackInstance = Room.databaseBuilder(context.getApplicationContext(),LabTrackDatabase.class,"LabTrackDatabase").build();
                }
            }
        }
        return labTrackInstance;
    }
  //  static final Migration

}
