package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.NoticeDataSource
import com.project.domain.model.DataState
import com.project.domain.model.notice.FetchNoticesModel
import com.project.domain.repository.NoticeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(
    private val noticeDataSource: NoticeDataSource
): NoticeRepository {
    override suspend fun fetchNotices(page: Int, size: Int, type: String?): Flow<DataState<FetchNoticesModel>> {
        return NetworkUtils.handleApi(
            execute = {
                noticeDataSource.fetchNotices(pages = page, size = size, type = type)
            },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun postReadNotice(noticeId: Int): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                noticeDataSource.postReadPost(noticeId = noticeId)
            },
            mapper = { true }
        )
    }
}
