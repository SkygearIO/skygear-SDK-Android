package io.skygear.skygear;

/**
 * The WebSocket Client Interface.
 */
public interface WebSocketClient {
    /**
     * The Connect Function.
     */
    void connect();

    /**
     * Checks whether it is connected.
     *
     * @return the boolean indicating whether it is connected
     */
    boolean isOpen();

    /**
     * Sends a message.
     *
     * @param s the message
     */
    void send(String s);

    /**
     * The Message Receive Callback.
     *
     * @param message the message
     */
    void onMessage(String message);
}

