package io.skygear.skygear;


public interface PubsubListener {
    void onOpen();
    void onClose();
    void onError(Exception e);
}
