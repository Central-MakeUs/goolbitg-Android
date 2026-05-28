package com.project.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GoolbitgDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
}
