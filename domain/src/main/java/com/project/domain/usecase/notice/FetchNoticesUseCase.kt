package com.project.domain.usecase.notice

import com.project.domain.repository.NoticeRepository
import javax.inject.Inject

class FetchNoticesUseCase @Inject constructor(
    private val noticeRepository: NoticeRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        type: String?
    ) = noticeRepository.fetchNotices(
        page = page,
        size = size,
        type = type
    )
}
