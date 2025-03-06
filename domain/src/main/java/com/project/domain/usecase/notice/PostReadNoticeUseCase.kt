package com.project.domain.usecase.notice

import com.project.domain.repository.NoticeRepository
import javax.inject.Inject

class PostReadNoticeUseCase @Inject constructor(
    private val noticeRepository: NoticeRepository
) {
    suspend operator fun invoke(noticeId: Int) = noticeRepository.postReadNotice(noticeId = noticeId)
}
