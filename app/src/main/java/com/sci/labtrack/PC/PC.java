package com.sci.labtrack.PC;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.sci.labtrack.Lab.Lab;
import java.io.Serializable;
@Entity
public class PC implements Serializable {
    @Ignore
    public static final int SUCCESSFUL=-1,EMPTY_SERIAL=0,EMPTY_RAM=1,EMPTY_HDD=2,WRONG_RAM=3,WRONG_HDD=4,EMPTY_NUMBER=5,NUMBER_OUT_OF_BOUNDARY=6
            ,REPEATED_NUMBER=7;

    public PC(@NonNull String serialNumber, @NonNull String labName, String cpu, String ram, String hardDrive, String numberInLab, String operatingSystem, boolean gpu, boolean monitor, boolean mouse, boolean keyBoard, String problemDescription, boolean isHardDiskWorking, boolean isRamWorking, boolean isProcessorWorking) {
        this.serialNumber = serialNumber;
        this.labName = labName;
        this.cpu = cpu;
        this.ram = ram;
        this.hardDrive = hardDrive;
        this.numberInLab = numberInLab;
        this.operatingSystem = operatingSystem;
        this.gpu = gpu;
        this.monitor = monitor;
        this.mouse = mouse;
        this.keyBoard = keyBoard;
        this.problemDescription = problemDescription;
        this.isHardDiskWorking = isHardDiskWorking;
        this.isRamWorking = isRamWorking;
        this.isProcessorWorking = isProcessorWorking;
    }

    @PrimaryKey@NonNull
    private String serialNumber= "";
    @NonNull
    private String labName;
    private String cpu= "";
    private String ram,hardDrive;
    private String numberInLab;
    private String operatingSystem;
    private String buyingDate;
    private boolean gpu;
    private boolean monitor;
    private boolean mouse;
    private boolean keyBoard;
    private boolean operatingSystemStatue;
    @Ignore
    private boolean selection;
    private boolean needRepair;
    private String problemDescription;
    private String missingSoftware;
    private boolean notificationSent;
    private boolean isHardDiskWorking ;
    private boolean isRamWorking ;
    private boolean isProcessorWorking ;
    @Ignore
    public PC() {
        this.serialNumber = "serialNumber";
        this.labName = "";
        this.cpu = "cpu";
        this.ram = "ram";
        this.hardDrive = "hardDrive";
        this.numberInLab = "numberInLab";
        this.operatingSystem = "operatingSystem";
        this.gpu = false;
        this.monitor = false;
        this.mouse = false;
        this.keyBoard = false;
        this.selection = false;
        this.needRepair = false;
        this.problemDescription = "";
        this.notificationSent = false;
        this.isHardDiskWorking = true;
        this.isRamWorking = true;
        this.isProcessorWorking = true;
        this.operatingSystemStatue = true;
        this.missingSoftware = "";
    }

    //setters and getters
    public boolean isOperatingSystemStatue() {
        return operatingSystemStatue;
    }
    public void setOperatingSystemStatue(boolean operatingSystemStatue) {
        this.operatingSystemStatue = operatingSystemStatue;
    }
    public String getMissingSoftware() {
        return missingSoftware;
    }
    public void setMissingSoftware(String missingSoftware) {
        this.missingSoftware = missingSoftware;
    }
    @NonNull
    public String getLabName() {
        return labName;
    }
    public void setLabName(@NonNull String labName) {
        this.labName = labName;
    }
    public boolean isHardDiskWorking() {
        return isHardDiskWorking;
    }
    public void setHardDiskWorking(boolean hardDiskWorking) {
        isHardDiskWorking = hardDiskWorking;
    }
    public boolean isRamWorking() {
        return isRamWorking;
    }
    public void setRamWorking(boolean ramWorking) {
        isRamWorking = ramWorking;
    }
    public boolean isProcessorWorking() {
        return isProcessorWorking;
    }
    public void setProcessorWorking(boolean processorWorking) {
        isProcessorWorking = processorWorking;
    }
    public String getOperatingSystem() {
        return operatingSystem;
    }
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
    public boolean isNotificationSent() {
        return notificationSent;
    }
    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
    public String getBuyingDate() {
        return buyingDate;
    }
    public void setBuyingDate(String buyingDate) {
        this.buyingDate = buyingDate;
    }
    public String getNumberInLab() {
        return numberInLab;
    }
    public void setNumberInLab(String numberInLab) {
        this.numberInLab = numberInLab;
    }
    public boolean isNeedRepair() {
        return needRepair;
    }
    public void setNeedRepair(boolean needRepair) {
        this.needRepair = needRepair;
    }
    boolean isSelection() {
        return selection;
    }
    void setSelection(boolean selection) {
        this.selection = selection;
    }
    public String getHardDrive() {
        return hardDrive;
    }
    public void setHardDrive(String hardDrive) {
        this.hardDrive = hardDrive;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getCpu() {
        return cpu;
    }
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
    public String getRam() {
        return ram;
    }
    public void setRam(String ram) {
        this.ram = ram;
    }
    public boolean isGpu() {
        return gpu;
    }
    public void setGpu(boolean gpu) {
        this.gpu = gpu;
    }
    public boolean isMonitor() {
        return monitor;
    }
    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }
    public boolean isMouse() {
        return mouse;
    }
    public void setMouse(boolean mouse) {
        this.mouse = mouse;
    }
    public boolean isKeyBoard() {
        return keyBoard;
    }
    public void setKeyBoard(boolean keyBoard) {
        this.keyBoard = keyBoard;
    }
    public String getProblemDescription() {
        return problemDescription;
    }
    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }
    //////////////////////////////////////////////////////////////////////////
    @Ignore
    public Integer pcValidation(Lab lab, int pos){
        if(serialNumber.trim().isEmpty()){
            return EMPTY_SERIAL;
        }
        if(numberInLab.isEmpty()){
            return EMPTY_NUMBER;
        }
        if(ram.trim().isEmpty()){
            return EMPTY_RAM;
        }
        if(hardDrive.trim().isEmpty()){
            return EMPTY_HDD;
        }
        if(Integer.parseInt(hardDrive)>5000||Integer.parseInt(hardDrive)<0){
            return WRONG_HDD;
        }
        if(Integer.parseInt(ram)<0||Integer.parseInt(ram)>128){
            return WRONG_RAM;
        }
        if(Integer.parseInt(numberInLab)>lab.getMaxCap()||Integer.parseInt(numberInLab)<=0){
            return NUMBER_OUT_OF_BOUNDARY;
        }
        if(isRepeated(lab)){
            return REPEATED_NUMBER;
        }
        return SUCCESSFUL;
    }
    @Ignore
    private boolean isRepeated(Lab lab){
        for (PC pc:lab.getPcList()){
            if(pc.getNumberInLab().equals(numberInLab) && !pc.getSerialNumber().equals(serialNumber)){
                return true;
            }
        }
        return false;
    }

}
