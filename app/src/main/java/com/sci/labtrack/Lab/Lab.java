package com.sci.labtrack.Lab;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sci.labtrack.PC.PC;
import com.sci.labtrack.User.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Lab implements Serializable {
    @PrimaryKey@NonNull
    public String name ;
    @Ignore
    List<User> admins = new ArrayList<>();
    @Ignore
    List<PC> PcList = new ArrayList<>();
    public  int maxCap=0;
    String location;
    @Ignore
    public Lab (){}
    public Lab(@NonNull String name, int maxCap, String location) {
        this.name = name;
        this.maxCap = maxCap;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public List<User> getAdmins() {
        return admins;
    }
    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }
    public int getMaxCap() {
        return maxCap;
    }
    public void setMaxCap(int maxCap) {
        this.maxCap = maxCap;
    }
    public void addNewPc(PC pc){
        PcList.add(pc);
    }
    public void addNewAdmins(User admin){
        admins.add(admin);
    }
    public String getName() {
        return name;
    }
    public List<PC> getPcList() {
        return PcList;
    }
    public void setPcList(List<PC> pcList) {
        PcList = pcList;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Lab lab){
        if (name.equals(lab.getName())){
            return true;
        }
        return false;
    }
}
