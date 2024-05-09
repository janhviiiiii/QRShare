//connector for HISTORY FRAGMENT

package com.example.qrshare;

public class QRCodeInfo {
    private String content;
    private String type;
    private String timestamp;
    private byte[] imageByteArray; // Add imageByteArray field

    public QRCodeInfo(String content, String type, String timestamp, byte[] imageByteArray) {
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.imageByteArray = imageByteArray;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Getter for type
    public String getType() {
        return type;
    }

    // Getter for timestamp
    public String getTimestamp() {
        return timestamp;
    }

    // Getter for imageByteArray
    public byte[] getImageByteArray() {
        return imageByteArray;
    }
}
