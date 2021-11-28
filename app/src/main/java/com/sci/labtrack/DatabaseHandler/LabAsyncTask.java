package com.sci.labtrack.DatabaseHandler;

import android.content.Context;
import android.os.AsyncTask;
import com.sci.labtrack.DatabaseHandler.LabTrackDatabase;
import com.sci.labtrack.Lab.Lab;

public class LabAsyncTask extends AsyncTask<Lab,Void,Void> {
    Lab lab;
    Context context;
    LabTrackDatabase myDB;
    LabDao labDao;
    public LabAsyncTask(Lab lab, Context context){
        myDB = LabTrackDatabase.getDatabase(context);
        labDao = myDB.labDao();
        this.lab = lab;
        this.context = context;
    }
    @Override
    protected Void doInBackground(Lab... labs) {
        if (lab == null){
            labDao.deleteAllLaps();
        }else {
            labDao.insert(lab);
        }
        return null;
    }
}
