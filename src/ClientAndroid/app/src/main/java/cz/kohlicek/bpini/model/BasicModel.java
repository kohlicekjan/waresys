package cz.kohlicek.bpini.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Základ pro který mají všechny modely
 */
public abstract class BasicModel {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("created")
    @Expose
    private Date created;

    @SerializedName("updated")
    @Expose
    private Date updated;


    public String getId() {
        return id;
    }

    /**
     * Převede datum výtvoření na požadovaný formát
     * @param format
     * @return datum v požadovaném formátu
     */
    public String getCreatedFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(created);
    }

    /**
     * Převede datum úpravy na požadovaný formát
     * @param format
     * @return datum v požadovaném formátu
     */
    public String getUpdatedFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(updated);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BasicModel)
            return this.getId().equals(((BasicModel) obj).getId());
        else
            return super.equals(obj);
    }
}
