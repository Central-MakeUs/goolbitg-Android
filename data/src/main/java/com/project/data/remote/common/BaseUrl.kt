package com.project.data.remote.common

object BaseUrl {
    const val BASE_DOMAIN_URL = "https://api.goolbitg.site/"
    const val BASE_DOMAIN_URL_DEV = "https://dev.goolbitg.site"
    /**
     * STOMP-over-WebSocket 채팅 엔드포인트 (HTTPS → WSS).
     * Cloudflare 뒤에 있어 평문 ws:// (port 80) 으로는 Upgrade 가 거절된다 (HTTP 400).
     * 반드시 wss:// (TLS, port 443) 로 연결해야 함.
     */
    const val BASE_WS_URL = "wss://api.goolbitg.site/chat"
    const val BASE_WS_URL_DEV = "wss://dev.goolbitg.site/chat"
    const val TERMS_OF_SERVICES_URL = "https://deep-twine-18f.notion.site/18b5dccdfca280128c3cc79c516a5b9d/"
    const val PRIVACY_AND_POLICY_URL = "https://deep-twine-18f.notion.site/18b5dccdfca2805986d4ff2aa8eff647/"
    const val CS_URL = "https://deep-twine-18f.notion.site/18b5dccdfca280ad8c49c5a75766dc64"
}
