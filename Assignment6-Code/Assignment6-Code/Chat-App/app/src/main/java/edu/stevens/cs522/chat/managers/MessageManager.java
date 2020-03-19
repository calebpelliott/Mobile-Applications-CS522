package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

    private static final String TAG = MessageManager.class.getCanonicalName();

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };


    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
        QueryBuilder.executeQuery(TAG, (Activity) context, MessageContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<Message> listener) {
        QueryBuilder.reexecuteQuery(TAG,
                (Activity) context,
                MessageContract.CONTENT_URI,
                MessageContract.PROJECTION,
                MessageContract.SENDERID + "=?",
                new String[]{Long.toString(peer.id)},
                null,
                LOADER_ID,
                creator,
                listener);
    }

    public void persistAsync(Message message) {
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);
        getAsyncResolver().insertAsync(MessageContract.CONTENT_URI,
                cv,
                null);
    }

    public long persist(Message message) {
        // Synchronous version, executed on background thread
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);
        return MessageContract.getId(
                getSyncResolver().insert(MessageContract.CONTENT_URI, cv));
    }


}
