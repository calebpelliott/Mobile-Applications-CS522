package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.ResultReceiver;

import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private MessageManager messageManager;

    private PeerManager peerManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        this.messageManager = new MessageManager(context);
        this.peerManager = new PeerManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        Response response = restMethod.perform(request);
        if (response instanceof RegisterResponse) {
            // TODO update the user name and sender id in settings, updated peer record PK
            Settings.saveChatName(context, request.chatname);
            Settings.saveSenderId(context, ((RegisterResponse) response).getSenderId());

            Peer peer = request.getPeerFromRequest();
            peerManager.persist(peer);
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        // TODO insert the message into the local database
        ChatMessage message = request.getChatMessageFromRequest();
        message.sender = Settings.getChatName(context);
        messageManager.persist(message);

        Response response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            // TODO update the message in the database with the sequence number
            message.seqNum = ((PostMessageResponse) response).getMessageId();
            messageManager.update(message, MessageContract.TIMESTAMP, new String[]{Long.toString(message.timestamp.getTime())});
        }
        return response;
    }

}
