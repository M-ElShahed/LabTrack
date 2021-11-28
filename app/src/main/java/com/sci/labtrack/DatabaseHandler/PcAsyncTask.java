package com.sci.labtrack.DatabaseHandler;

import android.content.Context;
import android.os.AsyncTask;
import com.sci.labtrack.PC.PC;
public class PcAsyncTask extends AsyncTask<PC,Void,Void> {
    PC pc ;
    Context context ;
    LabTrackDatabase myDB;
    PcDao pcDao ;
    public PcAsyncTask(PC pc, Context context){
        myDB = LabTrackDatabase.getDatabase(context);
        pcDao = myDB.pcDao();
        this.pc = pc;
    }
    @Override
    protected Void doInBackground(PC... pcs) {
        if(pc == null){
            pcDao.DeleteAllPc();
        }else
        pcDao.insert(pc);
        return null;
    }
}
