/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
     * Checks whether it is connecting
     *
     * @return the boolean indicating whether it is connecting
     */
    boolean isConnecting();

    /**
     * Sends a message.
     *
     * @param message the message
     * @throws NotYetConnectedException an exception indicating websocket not yet connected
     */
    void sendMessage(String message) throws NotYetConnectedException;

    /**
     * The Message Receive Callback.
     *
     * @param message the message
     */
    void onMessage(String message);

    /**
     * Cleans up the client.
     */
    void cleanup();

    /**
     * WebSocket Not Yet Connected Exception.
     */
    class NotYetConnectedException extends Exception {
        /**
         * Instantiates a new Not Yet Connected Exception.
         *
         * @param detailMessage the detail message
         */
        public NotYetConnectedException(String detailMessage) {
            super(detailMessage);
        }
    }
}

