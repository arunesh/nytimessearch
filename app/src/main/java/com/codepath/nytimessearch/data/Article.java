package com.codepath.nytimessearch.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by arunesh on 10/23/16.
 */

public class Article implements Parcelable {
    private static final String NYTIMES_BASE = "http://www.nytimes.com/";
    private static final String TAG = "nytimes";

    String webUrl;
    String headline;
    String thumbNail;

    Article(JSONObject jsonObject) {
        try {
            webUrl = jsonObject.getString("web_url");
            headline = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                thumbNail = NYTIMES_BASE + multimedia.getJSONObject(0).getString("url");
            } else {
                thumbNail = "";

            }
        } catch(JSONException e){
            Log.e(TAG, "Exception parsing article:" + jsonObject.toString());
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Article> articles = new ArrayList<Article>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            articles.add(new Article(jsonArray.getJSONObject(i)));
        }
        return articles;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webUrl);
        dest.writeString(this.headline);
        dest.writeString(this.thumbNail);
    }

    protected Article(Parcel in) {
        this.webUrl = in.readString();
        this.headline = in.readString();
        this.thumbNail = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
