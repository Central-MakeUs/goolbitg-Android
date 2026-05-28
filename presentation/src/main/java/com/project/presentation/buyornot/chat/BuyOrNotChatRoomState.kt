package com.project.presentation.buyornot.chat

import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.ChatMessageModel

data class BuyOrNotChatRoomState(
    val isLoading: Boolean = false,
    val postId: Int = 0,
    /** 채팅 대상이 되는 살까말까 게시물 — 헤더 아래 고정 영역에 표시 */
    val posting: BuyOrNotPostingModel? = null,
    /** 현재 로그인 사용자 식별자 (서버 모델 호환을 위해 username 으로 비교) */
    val currentUsername: String? = null,
    val currentUserId: String? = null,
    val messages: List<ChatMessageModel> = emptyList(),
    /** 입력 중인 메시지 */
    val inputText: String = "",
    val errorMessage: String? = null
) {
    /** 작성자 본인이면 true — 헤더의 "나가기" 버튼은 false 일 때만, mini 카드의 "수정" 버튼은 true 일 때만 노출 */
    val isWriter: Boolean
        get() = posting?.writerId != null && currentUserId != null && posting.writerId == currentUserId

    /** 헤더 타이틀에 들어갈 작성자명 — posting 정보가 도착하기 전엔 빈 문자열 */
    val writerDisplayName: String
        get() = posting?.writerId ?: ""

    /**
     * 채팅 UI 를 정상 노출할 준비가 됐는지.
     * posting 정보(작성자/상품)와 currentUserId 가 모두 도착해야 isMine 판정/수정 버튼 노출이 일관됨.
     * 둘 중 하나라도 늦으면 내 메시지가 잠깐 남 메시지처럼 보이는 깜빡임이 생기므로 그 전까지는 스켈레톤.
     */
    val isReady: Boolean
        get() = posting != null && !currentUserId.isNullOrBlank()

    val canSend: Boolean
        get() = inputText.isNotBlank() && isReady
}
