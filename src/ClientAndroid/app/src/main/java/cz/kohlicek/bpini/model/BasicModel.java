package cz.kohlicek.bpini.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BasicModel {

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

    public Date getCreated() {
        Calendar cal = new GregorianCalendar();
        int mGMTOffset = cal.getTimeZone().getRawOffset();

        cal.setTime(this.created);
        cal.add(Calendar.HOUR_OF_DAY, mGMTOffset);

        return cal.getTime();
    }

    public String getCreatedFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(created);
    }

    public Date getUpdated() {
        Calendar cal = new GregorianCalendar();
        int mGMTOffset = cal.getTimeZone().getRawOffset();

        cal.setTime(this.updated);
        cal.add(Calendar.HOUR_OF_DAY, mGMTOffset);
        return cal.getTime();
    }

    public String getUpdatedFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(updated);
    }

}
