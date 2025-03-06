package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.notice.FetchNoticesModel
import kotlinx.coroutines.flow.Flow

interface NoticeRepository {
    suspend fun fetchNotices(page: Int, size: Int, type: String?): Flow<DataState<FetchNoticesModel>>
    suspend fun postReadNotice(noticeId: Int): Flow<DataState<Boolean>>
}
