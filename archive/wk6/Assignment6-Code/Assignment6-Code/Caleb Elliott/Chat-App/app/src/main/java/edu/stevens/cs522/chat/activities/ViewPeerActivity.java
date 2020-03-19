package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements IQueryListener<Message> {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter messageAdapter;

    private MessageManager messageManager;

    private ListView messageList;

    private TextView name;

    private TextView timestamp;

    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        TextView name = (TextView) findViewById(R.id.view_user_name);
        name.setText(peer.name);

        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());

        TextView address = (TextView) findViewById(R.id.view_address);
        address.setText(peer.address.toString());

        messageList = (ListView) findViewById(R.id.view_messages);

        fillData(null);

        messageManager = new MessageManager(this);
        messageManager.getMessagesByPeerAsync(peer, this);
    }

    @Override
    public void handleResults(TypedCursor<Message> results) {
        messageAdapter.swapCursor(results.getCursor());
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        messageAdapter.swapCursor(null);
    }

    private void fillData(Cursor c){
        String[] to = new String[]{MessageContract.MESSAGE_TEXT};
        int[] from = new int[]{android.R.id.text1};
        messageAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                c,
                to,
                from,
                0);

        ListView lv = (ListView) findViewById(R.id.view_messages);
        lv.setAdapter(messageAdapter);
    }

}
