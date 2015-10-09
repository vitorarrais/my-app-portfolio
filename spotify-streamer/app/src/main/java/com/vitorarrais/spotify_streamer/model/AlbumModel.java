package com.vitorarrais.spotify_streamer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by vitor on 03/10/2015.
 */
public class AlbumModel implements Serializable {

    private String album_type;
    private List<String> available_markets;
    private Map<String, String> external_urls;
    private String href;
    private String id;
    private List<ImageModel> images;
    private String name;
    private String type;
    private String uri;

    public static AlbumModel from(AlbumSimple album){
        AlbumModel model = new AlbumModel();

        model.setAlbum_type(album.album_type);
        model.setAvailable_markets(album.available_markets);
        model.setExternal_urls(album.external_urls);
        model.setHref(album.href);
        model.setId(album.id);
        model.setImages(album.images);
        model.setName(album.name);
        model.setType(album.type);
        model.setUri(album.uri);

        return model;
    }

    public String getAlbum_type() {
        return album_type;
    }

    public void setAlbum_type(String album_type) {
        this.album_type = album_type;
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
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

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = new ArrayList<>();
        for (Image image : images){
            this.images.add(ImageModel.from(image));
        }
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
