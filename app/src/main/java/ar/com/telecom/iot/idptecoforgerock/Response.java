package ar.com.telecom.iot.idptecoforgerock;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {
    @SerializedName("result")
    private List<Device> devices;
    @SerializedName("success")
    private boolean success;
    @SerializedName("t")
    private long timestamp;
    @SerializedName("tid")
    private String transactionId;


    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
