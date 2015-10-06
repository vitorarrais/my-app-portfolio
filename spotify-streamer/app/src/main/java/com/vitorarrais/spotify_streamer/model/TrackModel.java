package com.vitorarrais.spotify_streamer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.LinkedTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by vitor on 03/10/2015.
 */
public class TrackModel implements Serializable {

    private AlbumModel album;
    private Map<String, String> external_ids;
    private Integer popularity;

    private List<ArtistModel> artists;
    private List<String> available_markets;
    private Boolean is_playable;
    private LinkedTrackModel linked_from;
    private int disc_number;
    private long duration_ms;
    private boolean explicit;
    private Map<String, String> external_urls;
    private String href;
    private String id;
    private String name;
    private String preview_url;
    private int track_number;
    private String type;
    private String uri;

    public static TrackModel from(Track track){
        TrackModel model = new TrackModel();

        model.setAlbum(AlbumModel.from(track.album));
        model.setExternal_ids(track.external_ids);
        model.setPopularity(track.popularity);
        model.setArtists(track.artists);
        model.setAvailable_markets(track.available_markets);
        model.setIs_playable(track.is_playable);
        model.setLinked_from(track.linked_from);
        model.setDisc_number(track.disc_number);
        model.setDuration_ms(track.duration_ms);
        model.setExplicit(track.explicit);
        model.setExternal_urls(track.external_urls);
        model.setHref(track.href);
        model.setId(track.id);
        model.setName(track.name);
        model.setPreview_url(track.preview_url);
        model.setTrack_number(track.track_number);
        model.setType(track.type);
        model.setUri(track.uri);

        return model;
    }

    public AlbumModel getAlbum() {
        return album;
    }

    public void setAlbum(AlbumModel album) {
        this.album = album;
    }

    public Map<String, String> getExternal_ids() {
        return external_ids;
    }

    public void setExternal_ids(Map<String, String> external_ids) {
        this.external_ids = external_ids;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public List<ArtistModel> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistSimple> artists) {
        this.artists = new ArrayList<>();
        for (ArtistSimple a : artists){
            this.artists.add(ArtistModel.from(a));
        }
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
    }

    public Boolean getIs_playable() {
        return is_playable;
    }

    public void setIs_playable(Boolean is_playable) {
        this.is_playable = is_playable;
    }

    public LinkedTrackModel getLinked_from() {
        return linked_from;
    }

    public void setLinked_from(LinkedTrack linked_from) {
        this.linked_from = LinkedTrackModel.from(linked_from);
    }

    public int getDisc_number() {
        return disc_number;
    }

    public void setDisc_number(int disc_number) {
        this.disc_number = disc_number;
    }

    public long getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(long duration_ms) {
        this.duration_ms = duration_ms;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public int getTrack_number() {
        return track_number;
    }

    public void setTrack_number(int track_number) {
        this.track_number = track_number;
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
