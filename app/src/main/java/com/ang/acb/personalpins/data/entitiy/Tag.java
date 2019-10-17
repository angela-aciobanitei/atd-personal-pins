package com.ang.acb.personalpins.data.entitiy;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tags")
public class Tag {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "pin_id", index = true)
    private long pinId;

    @ColumnInfo(name = "tag_text")
    private String tagText;
}
