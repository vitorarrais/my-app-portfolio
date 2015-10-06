package com.vitorarrais.spotify_streamer.model;

import java.io.Serializable;
import java.util.Map;

import kaaes.spotify.webapi.android.models.LinkedTrack;

/**
 * Created by vitor on 03/10/2015.
 */
public class LinkedTrackModel implements Serializable {

    private Map<String, String> external_urls;
    private String href;
    private String id;
    private String type;
    private String uri;

    public static LinkedTrackModel from(LinkedTrack linkedTrack){
        LinkedTrackModel model = new LinkedTrackModel();

        if (linkedTrack!=null) {
            model.setExternal_urls(linkedTrack.external_urls);
            model.setHref(linkedTrack.href);
            model.setId(linkedTrack.id);
            model.setType(linkedTrack.type);
            model.setUri(linkedTrack.uri);
        }

        return model;
    }

    public Map<String, String> getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(Map<String, String> external_urls) {
        this.external_urls = external_urls;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

