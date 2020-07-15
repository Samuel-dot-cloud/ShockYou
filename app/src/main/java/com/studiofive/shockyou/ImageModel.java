package com.studiofive.shockyou;

import java.io.Serializable;
import java.util.Objects;

public class ImageModel implements Serializable {
    int id;
    String imgFilename;
    boolean isAsset;

    public ImageModel(int id, String imgFilename, boolean isAsset) {
        this.id = id;
        this.imgFilename = imgFilename;
        this.isAsset = isAsset;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgFilename() {
        return imgFilename;
    }

    public void setImgFilename(String imgFilename) {
        this.imgFilename = imgFilename;
    }

    public boolean isAsset() {
        return isAsset;
    }

    public void setAsset(boolean asset) {
        isAsset = asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageModel that = (ImageModel) o;
        return id == that.id &&
                isAsset == that.isAsset &&
                Objects.equals(imgFilename, that.imgFilename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imgFilename, isAsset);
    }
}
