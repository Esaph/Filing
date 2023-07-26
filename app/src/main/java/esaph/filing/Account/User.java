package esaph.filing.Account;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import androidx.annotation.Nullable;

import java.io.Serializable;

import esaph.elib.esaphcommunicationservices.PipeData;

public class User extends PipeData<User> implements Serializable
{
    private String Uid;
    private String Username;
    private String GivenName;
    private String FamilyName;
    private String Email;
    private String FcmToken;

    public User(String uid, String username, String givenName, String familyName, String email, String FcmToken)
    {
        Uid = uid;
        Username = username;
        GivenName = givenName;
        FamilyName = familyName;
        Email = email;
        this.FcmToken = FcmToken;
    }

    public User()
    {
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Uid);
        stringBuilder.append(" : ");
        stringBuilder.append(Username);
        stringBuilder.append(" : ");
        stringBuilder.append(GivenName);
        stringBuilder.append(" : ");
        stringBuilder.append(FamilyName);
        stringBuilder.append(" : ");
        stringBuilder.append(Email);
        return stringBuilder.toString();
    }

    public String getFcmToken() {
        return FcmToken;
    }

    public void setFcmToken(String fcmToken) {
        FcmToken = fcmToken;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getGivenName() {
        return GivenName;
    }

    public void setGivenName(String givenName) {
        GivenName = givenName;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String familyName) {
        FamilyName = familyName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }



    @Override
    public boolean equals(@Nullable Object obj)
    {
        if(obj instanceof User)
        {
            User userProfile = (User) obj;
            return userProfile.getUid().equals(this.Uid);
        }
        else if(obj instanceof String)
        {
            String id = (String) obj;
            return id.equals(this.Uid);
        }

        return false;
    }
}