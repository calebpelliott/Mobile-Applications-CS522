package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final String TAG = MessageManager.class.getCanonicalName();

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllMessagesAsync(IQueryListener<ChatMessage> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(TAG, (Activity) context, MessageContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public Cursor getAllMessages(){
        return getSyncResolver().query(MessageContract.CONTENT_URI,
                MessageContract.PROJECTION,
                null,null,null);
    }

    public void persistAsync(ChatMessage message) {
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);
        getAsyncResolver().insertAsync(MessageContract.CONTENT_URI,
                cv,
                null);
    }

    public long persist(ChatMessage message) {
        // Synchronous version, executed on background thread
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);
        return MessageContract.getId(
                getSyncResolver().insert(MessageContract.CONTENT_URI, cv));
    }

    public long update(ChatMessage message, String selection, String[] selectionArgs){
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);

        return getSyncResolver().update(MessageContract.CONTENT_URI,
                        cv,
                        selection + "=?",
                        selectionArgs);

    }

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<ChatMessage> listener) {
        QueryBuilder.reexecuteQuery(TAG,
                (Activity) context,
                MessageContract.CONTENT_URI,
                MessageContract.PROJECTION,
                MessageContract.SENDER_ID + "=?",
                new String[]{Long.toString(peer.id)},
                null,
                LOADER_ID,
                creator,
                listener);
    }

}
