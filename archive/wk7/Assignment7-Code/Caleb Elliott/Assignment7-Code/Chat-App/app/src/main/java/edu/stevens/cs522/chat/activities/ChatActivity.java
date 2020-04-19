/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.messages);

        messageList = (ListView) findViewById(R.id.message_list);
        chatRoomName = (EditText) findViewById(R.id.chat_room);
        messageText =     (EditText) findViewById(R.id.message_text);
        sendButton =      (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        // TODO use SimpleCursorAdapter to display the messages received.
        fillData(null);
        // TODO create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        peerManager = new PeerManager(this);
        messageManager.getAllMessagesAsync(this);
        // TODO instantiate helper for service
        helper = new ChatHelper(this);
        // TODO initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());
        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            // Launch registration activity
            Settings.getAppId(this);
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                Intent peerIntent = new Intent(this, ViewPeersActivity.class);
                startActivity(peerIntent);
                break;
            case R.id.register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String chatRoom = chatRoomName.getText().toString();

            String message = messageText.getText().toString();

            // TODO use helper to post a message
            helper.postMessage(chatRoom, message, sendResultReceiver);

            // End todo

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        /*
         * RequestService calls back with result of Web service request
         */
        Context context = getApplicationContext();
        String toastText = "";
        int duration = Toast.LENGTH_SHORT;

        switch (resultCode) {
            case RESULT_OK:
                toastText = "Message: SUCCESS";
                break;
            default:
                toastText = "Message: FAILED";
                break;
        }

        Toast toast = Toast.makeText(context, toastText, duration);
        toast.show();
    }

    private void fillData(Cursor c){
        String[] to = new String[]{MessageContract.SENDER,
                MessageContract.MESSAGE_TEXT};
        int[] from = new int[]{android.R.id.text1, android.R.id.text2};
        messagesAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                c,
                to,
                from,
                0);

        ListView lv = (ListView) findViewById(R.id.message_list);
        lv.setAdapter(messagesAdapter);
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        messagesAdapter.swapCursor(null);
    }

}