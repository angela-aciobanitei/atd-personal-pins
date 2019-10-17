package com.ang.acb.personalpins.data.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * Immutable model class for a board.
 */
@Entity(tableName = "board")
public class Board {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "board_title")
    private String title;

    @ColumnInfo(name = "photo_cover_uri")
    private String photoCoverUri;

    public Board(long id, String title, String photoCoverUri) {
        this.id = id;
        this.title = title;
        this.photoCoverUri = photoCoverUri;
    }

    @Ignore
    public Board(String title, String photoCoverUri) {
        this.title = title;
        this.photoCoverUri = photoCoverUri;
    }

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
}
