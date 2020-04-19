package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String SEQUENCE_NUMBER = "sequence_number";

    public static final String MESSAGE_TEXT = "message_text";

    public static final String CHAT_ROOM = "chat_room";

    public static final String TIMESTAMP = "timestamp";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String SENDER = "sender";

    public static final String SENDER_ID = "sender_id";

    public static final String[] PROJECTION = {ID, SEQUENCE_NUMBER, MESSAGE_TEXT, CHAT_ROOM, TIMESTAMP, LATITUDE, LONGITUDE, SENDER, SENDER_ID};

    // TODO remaining columns in Messages table
    private static int messageTextColumn = -1;

    private static int idColumn = -1;

    private static int timestampColumn = -1;

    private static int senderColumn = -1;

    private static int senderIdColumn = -1;

    private static int sequenceNumberColumn = -1;

    private static int chatRoomColumn = -1;

    private static int longitudeColumn = -1;

    private static int latitudeColumn = -1;

    public static long getSequenceNumber(Cursor cursor) {
        if (sequenceNumberColumn < 0) {
            sequenceNumberColumn = cursor.getColumnIndexOrThrow(SEQUENCE_NUMBER);
        }
        return cursor.getLong(sequenceNumberColumn);
    }

    public static void putSequenceNumber(ContentValues out, long sequenceNumber) {
        out.put(SEQUENCE_NUMBER, sequenceNumber);
    }


    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODO remaining getter and putter operations for other columns

    public static long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(_ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putId(ContentValues out, long id) {
        out.put(_ID, id);
    }

    public static Date getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return new Date(Long.parseLong(cursor.getString(timestampColumn)));
    }

    public static void putTimestamp(ContentValues out, Date timestamp) {
        out.put(TIMESTAMP, Long.toString(timestamp.getTime()));
    }

    public static String getSender(Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

    public static long getSenderId(Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        return cursor.getLong(senderIdColumn);
    }

    public static void putSenderId(ContentValues out, long senderId) {
        out.put(SENDER_ID, senderId);
    }

    public static String getChatRoomName(Cursor cursor) {
        if (chatRoomColumn < 0) {
            chatRoomColumn = cursor.getColumnIndexOrThrow(CHAT_ROOM);
        }
        return cursor.getString(chatRoomColumn);
    }

    public static void putChatRoomName(ContentValues out, String chatRoomName) {
        out.put(CHAT_ROOM, chatRoomName);
    }

    public static double getLongitude(Cursor cursor) {
        if (longitudeColumn < 0) {
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return cursor.getDouble(longitudeColumn);
    }

    public static void putLongitude(ContentValues out, double longitude) {
        out.put(LONGITUDE, longitude);
    }

    public static double getLatitude(Cursor cursor) {
        if (latitudeColumn < 0) {
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }
        return cursor.getDouble(latitudeColumn);
    }

    public static void putLatitude(ContentValues out, double latitude) {
        out.put(LATITUDE, latitude);
    }
}
