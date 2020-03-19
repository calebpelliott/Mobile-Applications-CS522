package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Peer;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 4;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE_PEER =
                "create table " + PEERS_TABLE + " ("
                        + PeerContract._ID + " integer primary key, "
                        + PeerContract.NAME + " text not null, "
                        + PeerContract.TIMESTAMP + " text not null, "
                        + PeerContract.ADDRESS + " text not null "
                        + ")";
        private static final String DATABASE_CREATE_MESSAGE =
                "create table " + MESSAGES_TABLE + " ("
                        + MessageContract._ID + " integer primary key, "
                        + MessageContract.MESSAGE_TEXT + " text not null, "
                        + MessageContract.TIMESTAMP + " text not null, "
                        + MessageContract.SENDER + " text not null, "
                        + MessageContract.SENDERID + " integer not null, "
                        + "foreign key(" + MessageContract.SENDERID + ") references " + PEERS_TABLE + "(" + PeerContract._ID + ") "
                        + ")";

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_PEER);
            db.execSQL(DATABASE_CREATE_MESSAGE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("ChatDbAdapter",
                    "Upgrading from version " + oldVersion + " to " + newVersion);

            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return MessageContract.contentType("message");
            case MESSAGES_SINGLE_ROW:
                return MessageContract.contentItemType("message");
            case PEERS_ALL_ROWS:
                return PeerContract.contentType("peer");
            case PEERS_SINGLE_ROW:
                return PeerContract.contentItemType("peer");
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                long row = db.insert(MESSAGES_TABLE, null, values);
                if(row > 0){
                    Uri instanceUri = MessageContract.CONTENT_URI(row);

                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                    return instanceUri;
                }
            case PEERS_ALL_ROWS:
                String name = values.getAsString(PeerContract.NAME);
                Cursor c = db.query(PEERS_TABLE,
                        PeerContract.PROJECTION,
                        PeerContract.NAME + "=?",
                        new String[] {name},
                        null, null, null);

                if(c.moveToFirst()){
                    Peer p = new Peer(c);
                    row = p.id;

                    db.update(PEERS_TABLE, values,
                            PeerContract.NAME + "=?",
                            new String[]{name});
                }
                else {
                    row = db.insert(PEERS_TABLE, null, values);
                }
                if(row > 0){
                    Uri instanceUri = PeerContract.CONTENT_URI(row);

                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                    return instanceUri;
                }
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PEERS_ALL_ROWS:
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MESSAGES_SINGLE_ROW:
                selection = MessageContract._ID;
                selectionArgs = new String[]{Long.toString(MessageContract.getId(uri))};
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PEERS_SINGLE_ROW:
                selection = PeerContract._ID;
                selectionArgs = new String[]{Long.toString(PeerContract.getId(uri))};
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalStateException("query: bad case");
        }
        ContentResolver cr = getContext().getContentResolver();
        cursor.setNotificationUri(cr, uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return db.update(MESSAGES_TABLE, values, selection, selectionArgs);
            case PEERS_ALL_ROWS:
                return db.update(PEERS_TABLE, values, selection, selectionArgs);
            default:
                throw new IllegalStateException("update: bad case");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return db.delete(MESSAGES_TABLE, selection, selectionArgs);
            case PEERS_ALL_ROWS:
                return db.delete(PEERS_TABLE, selection, selectionArgs);
            default:
                throw new IllegalStateException("delete: bad case");
        }
    }

}
