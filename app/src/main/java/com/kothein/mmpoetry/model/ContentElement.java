package com.kothein.mmpoetry.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ContentElement implements Parcelable {
    private String type;
    private String text;
    private String color;
    private List<FormatElement> format;
    private List<String> list;
    private TableData tableData; // Change the field name to tableData
    private String url;
    private String caption;

    public ContentElement(String type, String text, String color, List<FormatElement> format, List<String> list, TableData tableData, String url, String caption) {
        this.type = type;
        this.text = text;
        this.color = color;
        this.format = format;
        this.list = list;
        this.tableData = tableData;
        this.url = url;
        this.caption = caption;
    }

    // Parcelable constructor
    protected ContentElement(Parcel in) {
        type = in.readString();
        text = in.readString();
        color = in.readString();
        format = in.createTypedArrayList(FormatElement.CREATOR);
        list = in.createStringArrayList();
        tableData = in.readParcelable(TableData.class.getClassLoader()); // Read the tableData field from the parcel
        url = in.readString();
        caption = in.readString();
    }

    // Getter and setter methods...

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<FormatElement> getFormat() {
        return format;
    }

    public void setFormat(List<FormatElement> format) {
        this.format = format;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(text);
        dest.writeString(color);
        dest.writeTypedList(format);
        dest.writeStringList(list);
        dest.writeParcelable(tableData, flags); // Write the tableData field to the parcel
        dest.writeString(url);
        dest.writeString(caption);
    }

    public static final Creator<ContentElement> CREATOR = new Creator<ContentElement>() {
        @Override
        public ContentElement createFromParcel(Parcel in) {
            return new ContentElement(in);
        }

        @Override
        public ContentElement[] newArray(int size) {
            return new ContentElement[size];
        }
    };
}
