package com.ang.acb.personalpins.data.entity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

/**
 * Immutable model class for a pin. Note: Since a pin can have zero or more
 * tags, we need to define a one-to-many relationship between these two entities.
 * Also, since a pin can have zero or more comments associated with it, we need
 * to define a one-to-many relationship between them too.
 */
@Entity(tableName = "pin")
public class Pin {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "pin_title")
    private String title;

    @Nullable
    @ColumnInfo(name = "photo_uri")
    private String photoUri;

    @Nullable
    @ColumnInfo(name = "video_uri")
    private String videoUri;

    @Ignore
    private List<Tag> tagList;

    @Ignore
    private List<Comment> commentList;

    public Pin(long id, String title, @Nullable String photoUri, @Nullable String videoUri) {
        this.id = id;
        this.title = title;
        this.photoUri = photoUri;
        this.videoUri = videoUri;
    }

    @Ignore
    public Pin(String title, @Nullable String photoUri, @Nullable String videoUri) {
        this.title = title;
        this.photoUri = photoUri;
        this.videoUri = videoUri;
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

    @Nullable
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(@Nullable String photoUri) {
        this.photoUri = photoUri;
    }

    @Nullable
    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(@Nullable String videoUri) {
        this.videoUri = videoUri;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
