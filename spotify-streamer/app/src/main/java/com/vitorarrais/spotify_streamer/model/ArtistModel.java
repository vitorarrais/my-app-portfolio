package com.vitorarrais.spotify_streamer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by vitor on 03/10/2015.
 */
public class ArtistModel implements Serializable {

    private FollowersModel followers;
    private List<String> genres;
    private List<ImageModel> images;
    private Integer popularity;
    private Map<String, String> external_urls;
    private String href;
    private String id;
    private String name;
    private String type;
    private String uri;

    public static ArtistModel from(Artist artist) {
        ArtistModel model = new ArtistModel();

        model.setFollowers(FollowersModel.from(artist.followers));
        model.setGenres(artist.genres);
        model.setImages(artist.images);
        model.setPopularity(artist.popularity);
        model.setExternal_urls(artist.external_urls);
        model.setHref(artist.href);
        model.setId(artist.id);
        model.setName(artist.name);
        model.setType(artist.type);
        model.setUri(artist.uri);

        return model;
    }

    public static ArtistModel from(ArtistSimple artist) {
        ArtistModel model = new ArtistModel();

        model.setExternal_urls(artist.external_urls);
        model.setHref(artist.href);
        model.setId(artist.id);
        model.setName(artist.name);
        model.setType(artist.type);
        model.setUri(artist.uri);

        return model;
    }

    public FollowersModel getFollowers() {
        return followers;
    }

    public void setFollowers(FollowersModel followers) {
        this.followers = followers;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = new ArrayList<>();
        for(Image image : images){
            this.images.add(ImageModel.from(image));
        }
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
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
