package it.tejp.codeit.common.network;

/**
 * Created by kerp on 19/10/15.
 */
public enum Message {

    REQUEST_SOURCES("request_sources"), REQUEST_RATING("request_rating"),
    BAD_TEAMNAME("bad_teamname"), GOOD_TEAMNAME("good_teamname");

    public String message;

    Message(String message) {
        this.message = message;
    }
}
