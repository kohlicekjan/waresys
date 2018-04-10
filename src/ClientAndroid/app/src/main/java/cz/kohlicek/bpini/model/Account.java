package cz.kohlicek.bpini.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import cz.kohlicek.bpini.BuildConfig;

/**
 * Lokálně ukládá a načítá údaje o přihlášeném účtu
 */
public class Account {

    public static final String SP_ACCOUNT = "bpini_account";
    public static final String SP_VERSION = "bpini_version";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";


    private String host;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("username")
    @Expose
    private String username;

    private String password;

    @SerializedName("fullname")
    @Expose
    private String fullname;

    @SerializedName("roles")
    @Expose
    private List<String> roles;

    public static Account getLocalAccount(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (sp.getInt(SP_VERSION, 0) < BuildConfig.VERSION_CODE) {
            clearLocalAccount(context);
            return null;
        }

        String json = sp.getString(SP_ACCOUNT, null);
        Account account = new Gson().fromJson(json, Account.class);
        return account;
    }

    public static void clearLocalAccount(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().remove(SP_ACCOUNT).apply();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isRole(String role) {
        return roles.contains(role);
    }

    public String getName() {
        return fullname.length() > 0 ? fullname : username;
    }

    public void saveLocalAccount(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = new Gson().toJson(this);
        sp.edit().putString(SP_ACCOUNT, json).apply();

        sp.edit().putInt(SP_VERSION, BuildConfig.VERSION_CODE).apply();
    }
}
