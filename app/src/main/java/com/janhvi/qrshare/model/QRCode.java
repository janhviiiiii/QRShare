package com.janhvi.qrshare.model;

import java.io.Serializable;
import java.sql.Blob;

public class QRCode implements Serializable {
    private long qid;
    private String content, type, date, time;
    private byte[] image;
    private int isFavorite;

    public QRCode() {
    }

    public QRCode(String content, String type, String date, String time, byte[] image, int isFavorite) {
        this.content = content;
        this.type = type;
        this.date = date;
        this.time = time;
        this.image = image;
        this.isFavorite = isFavorite;
    }


    public long getQid() {
        return qid;
    }

    public void setQid(long qid) {
        this.qid = qid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
