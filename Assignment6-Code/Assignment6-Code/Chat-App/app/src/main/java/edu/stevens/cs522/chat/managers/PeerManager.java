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

    public void getPeerAsync(long id, IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
    }

    public void persistAsync(final Peer peer, final IContinue<Uri> callback) {
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

    public long persist(Peer peer) {
        // TODO synchronous version that executes on background thread (in service)
        ContentValues cv = new ContentValues();
        peer.writeToProvider(cv);
        long id = -1;

        Cursor cursor = getSyncResolver().query(PeerContract.CONTENT_URI,
                PeerContract.PROJECTION,
                PeerContract.NAME + "=?",
                new String[]{peer.name},
                null);

        if(cursor.moveToFirst()){
            Peer p = new Peer(cursor);
            id = p.id;
            getSyncResolver().update(PeerContract.CONTENT_URI,
                    cv,
                    PeerContract.NAME + "=?",
                    new String[]{peer.name});
        }
        else {
            id = PeerContract.getId(getSyncResolver().insert(PeerContract.CONTENT_URI, cv));
        }
        return id;
    }

}
