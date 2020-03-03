/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.providers.ChatProvider;

public class ChatServer extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	final static public String TAG = ChatServer.class.getCanonicalName();
		
	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSendReceive serverSocket;
//  private DatagramSocket serverSocket;

	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true; 

    /*
     * UI for displayed received messages
     */
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private Button next;

    static final private int LOADER_ID = 1;

	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*
             * Get port information from the resources.
             */
            int port = getResources().getInteger(R.integer.app_port);

            // serverSocket = new DatagramSocket(port);

            serverSocket = new DatagramSendReceive(port);

        } catch (Exception e) {
            throw new IllegalStateException("Cannot open socket", e);
        }

        setContentView(R.layout.messages);

        messageList = (ListView) findViewById(R.id.message_list);
        next = (Button) findViewById(R.id.next);

        fillData(null);

        next.setOnClickListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);
	}



    public void onClick(View v) {
		
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			
			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);
			
			String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            final Message message = new Message();
            message.sender = msgContents[0];
            message.timestamp = new Date(Long.parseLong(msgContents[1]));
            message.messageText = msgContents[2];

			Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

            // For this assignment, OK to do CP insertion on the main thread.
            final Peer peer = new Peer();
            peer.name = msgContents[0];
            peer.timestamp = new Date(Long.parseLong(msgContents[1]));
            peer.address = receivePacket.getAddress();
            ContentValues cv = new ContentValues();
            peer.writeToProvider(cv);

            Cursor c = getContentResolver().query(PeerContract.CONTENT_URI,
                    null,
                    PeerContract.NAME + "=?",
                    new String[]{peer.name},
                    null);

            if(c.moveToFirst()){
                Peer p = new Peer(c);
                getContentResolver().update(PeerContract.CONTENT_URI, cv, PeerContract.NAME + "=?", new String[]{p.name});
                message.senderId = p.id;
            }
            else{
                Uri peerUri = getContentResolver().insert(PeerContract.CONTENT_URI, cv);
                message.senderId = PeerContract.getId(peerUri);
            }
            ContentValues message_cv = new ContentValues();
            message.writeToProvider(message_cv);
            getContentResolver().insert(MessageContract.CONTENT_URI, message_cv);
            //this.messagesAdapter.notifyDataSetChanged();
            getLoaderManager().restartLoader(LOADER_ID, null, this);



        } catch (Exception e) {
			
			Log.e(TAG, "Problems receiving packet: " + e.getMessage(), e);
			socketOK = false;
		} 

	}

	/*
	 * Close the socket before exiting application
	 */
    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }
	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
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
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this, MessageContract.CONTENT_URI,
                        new String[] {MessageContract._ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDERID},
                        null, null, null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        this.messagesAdapter.swapCursor(data);
        this.messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        this.messagesAdapter.swapCursor(null);
    }

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
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
                Intent intent = new Intent(this, ViewPeersActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }


}