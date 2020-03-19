package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Peer;


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

    public void getPeerAsync(long id, final IContinue<Peer> callback) {
        getAsyncResolver().queryAsync(PeerContract.CONTENT_URI,
                PeerContract.PROJECTION,
                PeerContract.ID + "=?",
                new String[]{Long.toString(id)},
                null,
                new IContinue<Cursor>() {
                    @Override
                    public void kontinue(Cursor value) {
                        Peer p = null;
                        if (value.moveToFirst()){
                            p = new Peer(value);
                        }

                        callback.kontinue(p);
                    }
                });
    }

    public void persistAsync(final Peer peer, final IContinue<Uri> callback) {
        ContentValues cv = new ContentValues();
        peer.writeToProvider(cv);

        getAsyncResolver().insertAsync(PeerContract.CONTENT_URI,
                cv,
                callback);

    }

    public long persist(Peer peer) {
        ContentValues cv = new ContentValues();
        peer.writeToProvider(cv);
        long id = -1;

        Uri uri = getSyncResolver().insert(PeerContract.CONTENT_URI, cv);
        id = PeerContract.getId(uri);
        return id;
    }

}
