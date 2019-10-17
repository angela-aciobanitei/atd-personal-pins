package com.ang.acb.personalpins.data.entitiy;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "boards")
public class Board {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "board_title")
    private String title;

    @ColumnInfo(name = "photo_cover_uri")
    private String photoCoverUri;

    @Ignore
    private List<Pin> pinList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoCoverUri() {
        return photoCoverUri;
    }

    public void setPhotoCoverUri(String photoCoverUri) {
        this.photoCoverUri = photoCoverUri;
    }

    public List<Pin> getPinList() {
        return pinList;
    }

    public void setPinList(List<Pin> pinList) {
        this.pinList = pinList;
    }
}
