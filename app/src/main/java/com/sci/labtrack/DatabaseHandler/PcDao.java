package com.sci.labtrack.DatabaseHandler;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.sci.labtrack.PC.PC;
import java.util.List;

@Dao
public interface PcDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE )
    void insert(PC pc);

    @Query("select * from PC")
    LiveData<List<PC>> getPcList();
    @Query("select * from PC where labName = :labName ORDER BY case :order when 'numberInLab' then numberInLab when 'needRepair' then needRepair  end asc ")
    LiveData<List<PC>> getPcList(String labName,String order);
    @Query("select * from PC where labName = :labName ORDER BY case :order when 'numberInLab' then numberInLab when 'needrepair' then needRepair  end desc ")
    LiveData<List<PC>> getPcListDesc(String labName,String order);
    @Query("Delete from pc")
    void DeleteAllPc();

}
