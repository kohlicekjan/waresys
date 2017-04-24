package cz.kohlicek.bpini.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Device extends BasicModel {

    public static final String STATUS_UNKNOWN = "unknown";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_ERROR = "error";

    @SerializedName("device_id")
    @Expose
    private String deviceId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("client_id")
    @Expose
    private String clientId;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("allowed")
    @Expose
    private boolean allowed;

    @SerializedName("serial_number")
    @Expose
    private String serialNumber;

    @SerializedName("ip_address")
    @Expose
    private String ipAddress;

    @SerializedName("metadata")
    @Expose
    private Map<String, String> metadata;


    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public String getClientId() {
        return clientId;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
