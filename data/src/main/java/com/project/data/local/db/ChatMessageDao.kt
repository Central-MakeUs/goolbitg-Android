package com.project.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    /** 특정 게시물의 채팅 메시지를 시간 오름차순으로 관찰 (UI Flow) */
    @Query("SELECT * FROM chat_messages WHERE buyOrNotId = :postId ORDER BY sentDateTime ASC, id ASC")
    fun observeByPost(postId: Int): Flow<List<ChatMessageEntity>>

    /** REST/STOMP 양쪽에서 받은 메시지 일괄 upsert */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(messages: List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(message: ChatMessageEntity)

    /** 페이지네이션용 — 가장 오래된 메시지 id (chatLastId 다음 페이지 fetch 시 사용) */
    @Query("SELECT MIN(id) FROM chat_messages WHERE buyOrNotId = :postId")
    suspend fun oldestId(postId: Int): Int?
}
