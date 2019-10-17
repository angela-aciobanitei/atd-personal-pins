package com.ang.acb.personalpins.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Immutable model class for a tag. Since zero or more instances of a Tag can
 * be linked to a single instance of a Pin through the "pin_id" foreign key,
 * this models a one-to-many relationship between a Tag entity and a Pin entity.
 *
 * See: https://developer.android.com/training/data-storage/room/relationships#one-to-many
 * See: https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
 */
@Entity(tableName = "tag",
        foreignKeys = @ForeignKey(entity = Pin.class,
                                  parentColumns = "id",
                                  childColumns = "pin_id",
                                  onDelete = CASCADE,
                                  onUpdate = CASCADE),
        indices = {@Index(value = {"pin_id"})}
)
public class Tag {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "pin_id")
    private long pinId;

    @ColumnInfo(name = "tag_text")
    private String tagText;

    public Tag(long id, long pinId, String tagText) {
        this.id = id;
        this.pinId = pinId;
        this.tagText = tagText;
    }

    @Ignore
    public Tag(long pinId, String tagText) {
        this.pinId = pinId;
        this.tagText = tagText;
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

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }
}
