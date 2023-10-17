package com.kothein.mmpoetry.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class FormatElement implements Parcelable {
    private String type;
    private String content;
    private String color;
    private List<String> style;
    private int size;

    public FormatElement(String type, String content, String color, List<String> style, int size) {
        this.type = type;
        this.content = content;
        this.color = color;
        this.style = style;
        this.size = size;
    }

    // Parcelable constructor
    protected FormatElement(Parcel in) {
        type = in.readString();
        content = in.readString();
        color = in.readString();
        style = in.createStringArrayList();
        size = in.readInt();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getStyle() {
        return style;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(content);
        dest.writeString(color);
        dest.writeStringList(style);
        dest.writeInt(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FormatElement> CREATOR = new Creator<FormatElement>() {
        @Override
        public FormatElement createFromParcel(Parcel in) {
            return new FormatElement(in);
        }

        @Override
        public FormatElement[] newArray(int size) {
            return new FormatElement[size];
        }
    };
}
