package com.project.data.remote.common

object BaseUrl {
    const val BASE_DOMAIN_URL = "https://api.goolbitg.site/"
    const val BASE_DOMAIN_URL_DEV = "https://dev.goolbitg.site"
    /**
     * STOMP-over-WebSocket 채팅 엔드포인트 (HTTPS → WSS).
     * 백엔드 endpoint 확정 시 정확한 path 로 조정 필요 (SockJS 여부에 따라 `/chat` 또는 `/chat/websocket`).
     */
    const val BASE_WS_URL = "ws://api.goolbitg.site/chat"
    const val BASE_WS_URL_DEV = "ws://dev.goolbitg.site/chat"
    const val TERMS_OF_SERVICES_URL = "https://deep-twine-18f.notion.site/18b5dccdfca280128c3cc79c516a5b9d/"
    const val PRIVACY_AND_POLICY_URL = "https://deep-twine-18f.notion.site/18b5dccdfca2805986d4ff2aa8eff647/"
    const val CS_URL = "https://deep-twine-18f.notion.site/18b5dccdfca280ad8c49c5a75766dc64"
}
