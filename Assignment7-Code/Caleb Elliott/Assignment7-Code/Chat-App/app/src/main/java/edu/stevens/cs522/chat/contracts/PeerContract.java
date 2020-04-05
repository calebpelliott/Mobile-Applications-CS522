package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public final static String NAME = "NAME";

    public final static String TIMESTAMP = "TIMESTAMP";

    public static final String LATITUDE = "LATITUDE";

    public static final String LONGITUDE = "LONGITUDE";

    public static final String[] PROJECTION = {ID, NAME, TIMESTAMP, LATITUDE, LONGITUDE};

    private static int idColumn = -1;

    private static int nameColumn = -1;

    private static int timestampColumn = -1;

    private static int longitudeColumn = -1;

    private static int latitudeColumn = -1;

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

    public static String getName(Cursor cursor){
        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues out, String name){
        out.put(NAME, name);
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
