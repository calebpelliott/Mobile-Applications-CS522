package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeSerializable(timestamp); //Avoid using serializable
        out.writeSerializable(address);


        /*Replace with this if above does not work. Serializing is costly
        out.writeLong(timestamp.getTime());
        out.writeString(address.toString());
         */
    }

    public Peer() {}

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = (Date) in.readSerializable();
        address = (InetAddress) in.readSerializable();

        /*
        timestamp = new Long(in.readLong());
        address = new Long(in.readLong());
        */
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }

    };
}
