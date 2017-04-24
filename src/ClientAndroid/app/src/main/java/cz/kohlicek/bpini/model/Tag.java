package cz.kohlicek.bpini.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tag extends BasicModel {

    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_MODE = "mode";
    public static final String TYPE_ITEM = "item";

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("item")
    @Expose
    private Item item;


    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public Item getItem() {
        return item;
    }


}
