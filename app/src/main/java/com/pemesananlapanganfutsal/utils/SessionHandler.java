package com.pemesananlapanganfutsal.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pemesananlapanganfutsal.model.User;

public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ID = "id_pemesan";
    private static final String KEY_NAMA = "nama_pemesan";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    public void loginUser(String id_penyewa, String nama_penyewa) {
        mEditor.putString(KEY_ID, id_penyewa);
        mEditor.putString(KEY_NAMA, nama_penyewa);
        mEditor.commit();
    }

    public User getUserDetails() {
        User user = new User(mPreferences.getString(KEY_ID, KEY_EMPTY), mPreferences.getString(KEY_NAMA, KEY_EMPTY));
        return user;
    }

    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();
    }

}
