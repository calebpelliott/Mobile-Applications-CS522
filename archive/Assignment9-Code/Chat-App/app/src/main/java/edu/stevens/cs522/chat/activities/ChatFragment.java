/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.ChatRoom;
import edu.stevens.cs522.chat.managers.TypedCursor;

public class ChatFragment extends Fragment implements OnClickListener, IQueryListener<ChatMessage> {

    private final static String TAG = ChatFragment.class.getCanonicalName();

    public final static String CHATROOM_KEY = "chatroom";

    public interface IChatListener {

        public void getMessages(ChatRoom chatroom);

        public void addMessageDialog(ChatRoom chatroom);

    }

    private IChatListener listener;

    private Context context;

    private ChatRoom chatroom;
		
    /*
     * UI for displaying received messages
     */
	private ListView messageList;

    private View sendButton;

    private SimpleCursorAdapter messagesAdapter;


    public ChatFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof IChatListener) {
            listener = (IChatListener) context;
        } else {
            throw new IllegalStateException("Activity must implement IChatListener!");
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // chatroom = getArguments().getParcelable(CHATROOM_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages, container, false);

        /*
         * Use the floating action button to produce the dialog for sending a message
         */
        sendButton = rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        // TODO use SimpleCursorAdapter to display the messages received.


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

	public void onResume() {
        super.onResume();

        if (chatroom != null) {
            // Initiate a query for all messages in the activity
            // This should be done in the parent activity
            listener.getMessages(chatroom);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    public void setChatroom(ChatRoom chatroom) {
        this.chatroom = chatroom;

        // Initiate a query for all messages in the activity
        // This should be done in the parent activity
        listener.getMessages(chatroom);
    }

    /*
     * The parent activity will relay callbacks from background querying to these calls in the fragment.
     */
    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        // TODO
    }

    @Override
    public void closeResults() {
        // TODO
    }

    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {

        if (chatroom != null) {
            listener.addMessageDialog(chatroom);
        }

    }

}