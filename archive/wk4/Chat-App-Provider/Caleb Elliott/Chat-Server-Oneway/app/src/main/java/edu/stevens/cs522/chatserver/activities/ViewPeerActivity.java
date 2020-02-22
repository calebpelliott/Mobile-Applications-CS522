package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String PEER_KEY = "peer";
    private ListView messageList;
    private SimpleCursorAdapter messageAdapter;
    private long peerId;

    private TextView name;

    private TextView timestamp;

    private TextView address;

    static final private int LOADER_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }
        peerId = peer.id;

        TextView name = (TextView) findViewById(R.id.view_user_name);
        name.setText(peer.name);

        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());

        TextView address = (TextView) findViewById(R.id.view_address);
        address.setText(peer.address.toString());

        messageList = (ListView) findViewById(R.id.view_messages);

        fillData(null);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this, MessageContract.CONTENT_URI,
                        new String[] {MessageContract._ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDERID},
                        MessageContract.SENDERID + "=?",
                        new String[]{Long.toString(peerId)}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        this.messageAdapter.swapCursor(data);
        this.messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        this.messageAdapter.swapCursor(null);
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
