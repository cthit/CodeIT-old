package it.chalmers.digit.codeit.common.network;

import com.esotericsoftware.kryo.Kryo;

/**
 * Created by kerp on 19/10/15.
 */
public enum Message {

    REQUEST_SOURCES("request_sources"), TRANSFER_SOURCES("transfer_sources"),
    BAD_TEAMNAME("bad_teamname"), GOOD_TEAMNAME("good_teamname"), NEW_TEAMNAME("new_teamname"),
    CHUNKED_TRANSFER("chunked_transfer"), CHUNK("chunk"), TRANSFER_ERROR("transfer_error"),
    REQUEST_RATINGS("request_ratings"), TRANSFER_RATINGS("transfer_ratings");

    private String message;

    Message(String message) {
        this.message = message;
    }

    public static void register(Kryo k) {
        k.register(Message.class);
    }
}
