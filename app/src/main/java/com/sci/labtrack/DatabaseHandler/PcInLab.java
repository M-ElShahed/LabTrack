package com.sci.labtrack.DatabaseHandler;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.sci.labtrack.Lab.Lab;
import com.sci.labtrack.PC.PC;
import java.util.ArrayList;
public class PcInLab {
    @Embedded public Lab lab;
    @Relation(
            parentColumn = "name",
            entityColumn = "labName"
    )
    public ArrayList<PC> pcArrayList;
}
