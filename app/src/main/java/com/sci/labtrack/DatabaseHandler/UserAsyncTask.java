package com.sci.labtrack.DatabaseHandler;

import android.content.Context;
import android.os.AsyncTask;
import com.sci.labtrack.User.User;

public class UserAsyncTask extends AsyncTask<User, Void, Void> {
    User asyncUser;
    LabTrackDatabase myDB ;
    UserDao userDao;
    Context mContext;
    public UserAsyncTask(User user, Context context) {
        myDB = LabTrackDatabase.getDatabase(context);
        userDao = myDB.userDao();
        asyncUser = user;
        mContext = context;
    }

    @Override
    protected Void doInBackground(User... users) {
            userDao.insert(asyncUser);
        return  null;
    }




}
