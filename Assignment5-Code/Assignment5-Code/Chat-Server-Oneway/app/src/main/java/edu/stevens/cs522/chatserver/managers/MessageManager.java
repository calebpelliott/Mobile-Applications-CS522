package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;


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
                MessageContract.SENDER + "=?",
                new String[]{peer.name},
                null,
                LOADER_ID,
                creator,
                listener);
    }

    public void persistAsync(final Message message) {
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);
        getAsyncResolver().insertAsync(MessageContract.CONTENT_URI,
                cv,
                null);
    }

}
