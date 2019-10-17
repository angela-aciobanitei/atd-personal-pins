package com.ang.acb.personalpins.data.entitiy;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
