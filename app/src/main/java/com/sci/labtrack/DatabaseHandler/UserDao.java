package com.sci.labtrack.DatabaseHandler;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.sci.labtrack.User.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Query("SELECT * from User")
    List<User> userArrayList();

    @Query("select * from User where UID = :userUID" )
    LiveData<User> getCurrentUser(String userUID);
}
