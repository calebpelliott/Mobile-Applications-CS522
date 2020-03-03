package edu.stevens.cs522.chat.managers;

import android.content.Context;
import android.database.Cursor;

import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.entities.Message;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

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
        // TODO use QueryBuilder to complete this
    }

    public void persistAsync(Message Message) {
        // TODO
    }

    public long persist(Message message) {
        // Synchronous version, executed on background thread
        throw new UnsupportedOperationException("persist not implemented");
    }


}
