package com.vitorarrais.spotify_streamer.model;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by vitor on 03/10/2015.
 */
public class ImageModel implements Serializable {

    private Integer width;
    private Integer height;
    private String url;

    public static ImageModel from(Image image){
        ImageModel model = new ImageModel();

        model.setHeight(image.height);
        model.setUrl(image.url);
        model.setWidth(image.width);

        return model;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
