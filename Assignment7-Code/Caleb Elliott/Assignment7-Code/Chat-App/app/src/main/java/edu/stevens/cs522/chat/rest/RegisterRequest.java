package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.Peer;

/**
 * Created by dduggan.
 */

public class RegisterRequest extends Request {

    public String chatname;

    public RegisterRequest(String chatname, UUID clientID) {
        super(0, clientID);
        this.chatname = chatname;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<>();
        // TODO add headers
        headers = super.getRequestHeaders();
        return headers;
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new RegisterResponse(connection);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(chatname);
    }

    public Peer getPeerFromRequest(){
        Peer p = new Peer();
        p.name = this.chatname;
        p.timestamp = super.timestamp;
        p.latitude = super.latitude;
        p.longitude = super.longitude;

        return p;
    }

    public RegisterRequest(Parcel in) {
        super(in);
        this.chatname = in.readString();
    }

    public static Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel source) {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };

}
