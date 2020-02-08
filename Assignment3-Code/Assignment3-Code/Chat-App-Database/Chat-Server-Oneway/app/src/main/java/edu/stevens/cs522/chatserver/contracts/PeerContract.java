package edu.stevens.cs522.chatserver.contracts;

import android.provider.BaseColumns;

import java.net.InetAddress;

/**
 * Created by dduggan.
 */

public class PeerContract implements BaseColumns {

    // TODO define column names, getters for cursors, setters for contentvalues
    public final static String NAME = "NAME";

    public final static String TIMESTAMP = "TIMESTAMP";

    public final static String ADDRESS = "ADDRESS";
}
