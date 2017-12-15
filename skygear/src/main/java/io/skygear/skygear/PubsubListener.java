package io.skygear.skygear;


public interface PubsubListener {
    void onConnectionChanged(boolean isConnected);
    void onConnectionError(Exception e);
}
