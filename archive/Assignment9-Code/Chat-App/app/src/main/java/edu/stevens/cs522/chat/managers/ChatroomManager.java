package edu.stevens.cs522.chat.managers;

import android.content.Context;
import android.database.Cursor;

import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.contracts.ChatroomContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.ChatRoom;
import edu.stevens.cs522.chat.async.IQueryListener;

/**
 * Created by dduggan.
 */

public class ChatroomManager extends Manager<ChatRoom> {

    private static final int LOADER_ID = 3;

    private static final IEntityCreator<ChatRoom> creator = new IEntityCreator<ChatRoom>() {
        @Override
        public ChatRoom create(Cursor cursor) {
            return new ChatRoom(cursor);
        }
    };

    public ChatroomManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllChatroomsAsync(IQueryListener<ChatRoom> listener) {
        executeQuery(ChatroomContract.CONTENT_URI, listener);
    }


}
