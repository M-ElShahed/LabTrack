package com.sci.labtrack.DatabaseHandler;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.sci.labtrack.Lab.Lab;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface LabDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Lab lab);

     @Query("SELECT * FROM Lab")
    LiveData<List<Lab>>  getLabs();

     @Query("delete  from Lab")
    public void deleteAllLaps( );

}
