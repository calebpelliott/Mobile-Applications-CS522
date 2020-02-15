package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer-id";

    private ChatDbAdapter chatDbAdapter;

    private ListView peerMessagesListView;

    private SimpleCursorAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);


        long peerId = getIntent().getLongExtra(PEER_ID_KEY, -1);
        if (peerId < 0) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }
        chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();

        peerMessagesListView = (ListView) findViewById(R.id.peer_message_list);

        Peer peer = chatDbAdapter.fetchPeer(peerId);
        Cursor c = chatDbAdapter.fetchMessagesFromPeer(peer);

        startManagingCursor(c);//deprecated
        messageAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                c,
                new String[] {MessageContract.MESSAGE_TEXT},
                new int[] {android.R.id.text1});//deprecated
        peerMessagesListView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

        TextView name = (TextView) findViewById(R.id.view_user_name);
        name.setText(peer.name);

        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());

        TextView address = (TextView) findViewById(R.id.view_address);
        address.setText(peer.address.toString());
    }

}
