package com.sci.labtrack.User;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Entity
public class User {
    @PrimaryKey@NonNull@ColumnInfo(name = "UID")
    String UID;
    @NonNull@ColumnInfo(name = "name")
    String name ;
    @NonNull@ColumnInfo(name = "email")
    String email;
    @ColumnInfo(name = "gender")
    boolean gender;
    @ColumnInfo(name = "position")
    boolean position;
    @Ignore
    String password;
    @Ignore
    String confirmPassword ;
    @ColumnInfo(name = "phone")
    private String phone ;
    @Ignore
    public static final int SUCCESSFUL=-1,EMPTY_NAME=0,EMPTY_MAIL=1,EMPTY_PASSWORD=2,WRONG_CONFIRM=3,EMAIL_ALREADY_REGISTERED=4,
    EMAIL_WRONG_FORMAT=5,PASSWORD_LENGTH=6,EMAIL_NOT_REGISTERED=7,WRONG_PASSWORD=8,PHONE_LENGTH_ERROR=9,PHONE_WRONG_FORMAT=10,UNKNOWN_ERROR=100;
    @Ignore
    public User() {
        this.name = "NULL";
        this.password = "empty";
        this.email = "";
        this.gender = true;
        this.position = true;
        this.phone = "" ;
        this.UID = "a2faw2";
    }
    //Database Constructor
    public User(@NonNull String UID, @NonNull String name, @NonNull String email, boolean gender, boolean position, String phone) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.position = position;
        this.phone = phone;
    }

    @NonNull
    public String getUID() {
        return UID;
    }

    public void setUID(@NonNull String UID) {
        this.UID = UID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }
    @Ignore
    public String getPassword() {
        return password;
    }
    @Ignore
    public void setPassword(String password) {
        this.password = password;
    }
    @Ignore
     String getConfirmPassword() {
        return confirmPassword;
    }
    @Ignore
     void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Ignore
    public int userValidation(){
        String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(getEmail());
        if(getName().trim().isEmpty()){
            return EMPTY_NAME;
        }
        if(getEmail().trim().isEmpty() ){
            return EMPTY_MAIL;
        }
        if(getPassword().trim().isEmpty()){
            return EMPTY_PASSWORD;
        }
        if(!matcher.matches()){
            return EMAIL_WRONG_FORMAT;
        }
        if(getPassword().length() <9 ){
            return PASSWORD_LENGTH;
        }
        if(!getPassword().equals(confirmPassword)){
            return WRONG_CONFIRM;
        }
        if(phone.length() != 11&& phone.length()>0){
            return PHONE_LENGTH_ERROR;
        }
        if(phone.length()>3) {
            if (phone.charAt(0) !='0' ) {
                return PHONE_WRONG_FORMAT;
            }
            if(phone.charAt(1) != '1'){
                return PHONE_WRONG_FORMAT;
            }
            if(phone.charAt(2) != '0' && phone.charAt(2) != '1' && phone.charAt(2) != '2' &&phone.charAt(2) != '5'){
                return PHONE_WRONG_FORMAT;
            }

        }
        return SUCCESSFUL ;
    }
    @Ignore
    public int userLoginValidation(){
        String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(getEmail());
        if(getEmail().trim().isEmpty() ){
            return EMPTY_MAIL;
        }
        if(getPassword().trim().isEmpty()){
            return EMPTY_PASSWORD;
        }
        if(!matcher.matches()){
            return EMAIL_WRONG_FORMAT;
        }
        return SUCCESSFUL;
    }
}
