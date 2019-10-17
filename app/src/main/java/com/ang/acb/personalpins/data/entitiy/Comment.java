package com.ang.acb.personalpins.data.entitiy;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Immutable model class for a comment. Since zero or more instances of a Comment
 * can be linked to a single instance of a Pin through the "pin_id" foreign key,
 * this models a one-to-many relationship between Comment entity and Pin entity.
 *
 * See: https://developer.android.com/training/data-storage/room/relationships#one-to-many
 * See: https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
 */
@Entity(tableName = "comment",
        foreignKeys = @ForeignKey(entity = Pin.class,
                                  parentColumns = "id",
                                  childColumns = "pin_id",
                                  onDelete = CASCADE,
                                  onUpdate = CASCADE),
        indices = {@Index(value = {"pin_id"})}
)
public class Comment {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "pin_id")
    private long pinId;

    @ColumnInfo(name = "comment_text")
    private String text;

    public Comment(long id, long pinId, String text) {
        this.id = id;
        this.pinId = pinId;
        this.text = text;
    }

    @Ignore
    public Comment(long pinId, String text) {
        this.pinId = pinId;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPinId() {
        return pinId;
    }

    public void setPinId(long pinId) {
        this.pinId = pinId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
