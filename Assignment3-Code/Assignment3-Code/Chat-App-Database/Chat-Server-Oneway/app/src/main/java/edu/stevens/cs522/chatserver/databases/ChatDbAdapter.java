package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ChatDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "peers";

    private static final int DATABASE_VERSION = 2;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE_MESSAGE =
                "create table " + MESSAGE_TABLE + " ("
                + MessageContract._ID + " integer primary key, "
                + MessageContract.MESSAGE_TEXT + " text not null, "
                + MessageContract.TIMESTAMP + " text not null, "
                + MessageContract.SENDER + " text not null, "
                + MessageContract.SENDERID + " integer not null "
                + ")";
        private static final String DATABASE_CREATE_PEER =
                "create table " + PEER_TABLE + " ("
                        + PeerContract._ID + " integer primary key, "
                        + PeerContract.NAME + " text not null, "
                        + PeerContract.TIMESTAMP + " text not null, "
                        + PeerContract.ADDRESS + " text not null "
                        + ")";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
           db.execSQL(DATABASE_CREATE_MESSAGE);
           db.execSQL(DATABASE_CREATE_PEER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("ChatDbAdapter",
                  "Upgrading from version " + oldVersion + " to " + newVersion);

            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            onCreate(db);
        }
    }


    public ChatDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public Cursor fetchAllMessages() {
        Cursor c = db.query(MESSAGE_TABLE, new String[] {MessageContract._ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDERID},
                null, null, null, null, null);
        return c;
    }

    public Cursor fetchAllPeers() {
        // TODO
        return null;
    }

    public Peer fetchPeer(long peerId) {
        String[] projection = {PeerContract._ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.ADDRESS};
        String selection = MessageContract._ID + "=?";
        String[] selectionArgs = {Long.toString(peerId)};

        Cursor c = db.query(PEER_TABLE,
                            projection,
                            selection,
                            selectionArgs, null, null, null);


        return new Peer(c);
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        return null;
    }

    public long persist(Message message) throws SQLException {
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);

        return db.insert(MESSAGE_TABLE,
                 null,
                  cv);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     */
    public long persist(Peer peer) throws SQLException {
        // TODO
        throw new IllegalStateException("Unimplemented: persist peer");
    }

    public void close() {
        dbHelper.close();
    }
}