package com.kothein.mmpoetry.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TableData implements Parcelable {
    private List<List<String>> data;

    public TableData(List<List<String>> data) {
        this.data = data;
    }

    // Parcelable constructor
    protected TableData(Parcel in) {
        int numRows = in.readInt();
        data = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<String> row = new ArrayList<>();
            in.readStringList(row);
            data.add(row);
        }
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write the number of rows
        dest.writeInt(data.size());

        // Write each row as a String list
        for (List<String> row : data) {
            dest.writeStringList(row);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TableData> CREATOR = new Creator<TableData>() {
        @Override
        public TableData createFromParcel(Parcel in) {
            return new TableData(in);
        }

        @Override
        public TableData[] newArray(int size) {
            return new TableData[size];
        }
    };
}

