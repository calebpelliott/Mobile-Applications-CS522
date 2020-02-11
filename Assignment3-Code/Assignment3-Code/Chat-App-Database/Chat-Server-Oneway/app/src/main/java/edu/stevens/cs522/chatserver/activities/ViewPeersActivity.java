package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ChatDbAdapter chatDbAdapter;

    private SimpleCursorAdapter peerAdapter;

    private ListView peerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        peerList = (ListView) findViewById(R.id.peer_list);
        chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();

        Cursor c = chatDbAdapter.fetchAllPeers();
        startManagingCursor(c);//deprecated
        peerAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                c,
                new String[] {PeerContract.NAME},
                new int[] {android.R.id.text1});//deprecated
        peerList.setAdapter(peerAdapter);
        peerAdapter.notifyDataSetChanged();

        peerList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, id);
        startActivity(intent);
    }
}
