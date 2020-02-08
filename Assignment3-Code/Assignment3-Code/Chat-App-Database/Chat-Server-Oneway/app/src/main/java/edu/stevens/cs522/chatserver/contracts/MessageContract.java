package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class MessageContract implements BaseColumns {

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    public static final String SENDERID = "senderId";

    private static int messageTextColumn = -1;

    private static int idColumn = -1;
    private static int timestampColumn = -1;
    private static int senderColumn = -1;
    private static int senderIdColumn = -1;

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

    public static void putidColumn(ContentValues out, long id) {
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
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDERID);
        }
        return cursor.getLong(senderIdColumn);
    }

    public static void putSenderId(ContentValues out, long senderId) {
        out.put(SENDERID, senderId);
    }
}
