package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.net.InetAddress;
import java.util.Date;

import static edu.stevens.cs522.chatserver.contracts.BaseContract.withExtendedPath;

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

    public final static String ADDRESS = "ADDRESS";

    public static final String[] PROJECTION = {ID, NAME, TIMESTAMP, ADDRESS};

    private static int idColumn = -1;

    private static int nameColumn = -1;

    private static int timestampColumn = -1;

    private static int addressColumn = -1;

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

    public static InetAddress getAddress(Cursor cursor){
        if (addressColumn < 0) {
            addressColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }

        InetAddress address = null;
        try{
            String s = cursor.getString(addressColumn);
            if(s.equals("localhost/127.0.0.1") || s.equals("/127.0.0.1")){ //InetAddress isn't happy with this format
                s = "127.0.0.1";
            }
            address = InetAddress.getByName(s);
        }
        catch (Exception e){
            Log.w("PeerContract", "getAddress failed to getByName");
        }
        return address;
    }

    public static void putAddress(ContentValues out, InetAddress address){
        out.put(ADDRESS, address.toString());
    }

}
