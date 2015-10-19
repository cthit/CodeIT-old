package it.tejp.codeit.common.network;

/**
 * Created by kerp on 19/10/15.
 */
public enum Message {

    REQUEST_SOURCES("request_sources");

    public String message;

    Message(String message) {
        this.message = message;
    }
}
