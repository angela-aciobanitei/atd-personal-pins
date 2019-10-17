package com.ang.acb.personalpins.data.entitiy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Since a board can have any number of pins, and a pin can be included in any
 * number of boards, we need to define a many-to-many relationship between them.
 *
 * See: https://developer.android.com/training/data-storage/room/relationships#many-to-many
 * See: https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
 */
@Entity(tableName = "boardPin",
        primaryKeys = {"boardId", "pinId" },
        foreignKeys = {@ForeignKey(entity = Board.class,
                                   parentColumns = "id",
                                   childColumns = "boardId",
                                   onDelete = CASCADE,
                                   onUpdate = CASCADE),
                       @ForeignKey(entity = Pin.class,
                                   parentColumns = "id",
                                   childColumns = "pinId",
                                   onDelete = CASCADE,
                                   onUpdate = CASCADE)
        }
)
public class BoardPin {

    // Create index to cover this column to handle this Room warning:
    // "Warning: boardId column references a foreign key but it is not part
    // of an index. This may trigger full table scans whenever parent table
    // is modified so you are highly advised to create an index that covers
    // this column."
    @ColumnInfo(index = true)
    private long boardId;

    @ColumnInfo(index = true)
    private long pinId;

    public BoardPin(long boardId, long pinId) {
        this.boardId = boardId;
        this.pinId = pinId;
    }

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public long getPinId() {
        return pinId;
    }

    public void setPinId(long pinId) {
        this.pinId = pinId;
    }
}
