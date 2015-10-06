package com.vitorarrais.spotify_streamer.model;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Followers;

/**
 * Created by vitor on 03/10/2015.
 */
public class FollowersModel implements Serializable {

    private String href;
    private int total;

    public static FollowersModel from(Followers followers){
        FollowersModel model = new FollowersModel();

        model.setHref(followers.href);
        model.setTotal(followers.total);

        return model;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
