package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.CursorAdapter;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final String TAG = PeerManager.class.getCanonicalName();

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        QueryBuilder.executeQuery(TAG, (Activity) context, PeerContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void persistAsync(final Peer peer, final IContinue<Uri> callback) { //changed callback to return uri
        ContentValues cv = new ContentValues();
        peer.writeToProvider(cv);

        getAsyncResolver().queryAsync(PeerContract.CONTENT_URI,
                PeerContract.PROJECTION,
                PeerContract.NAME + "=?",
                new String[]{peer.name},
                null,
                new IContinue<Cursor>() {
                    @Override
                    public void kontinue(Cursor value) {
                        ContentValues cv = new ContentValues();
                        peer.writeToProvider(cv);

                        if (value.moveToFirst()){
                            getAsyncResolver().updateAsync(PeerContract.CONTENT_URI,
                                    cv,
                                    PeerContract.NAME + "=?",
                                    new String[]{peer.name});
                            callback.kontinue(PeerContract.CONTENT_URI(PeerContract.getId(value)));
                        }
                        else{
                            getAsyncResolver().insertAsync(PeerContract.CONTENT_URI,
                                    cv,
                                    callback);
                        }
                    }
                });
    }

}
