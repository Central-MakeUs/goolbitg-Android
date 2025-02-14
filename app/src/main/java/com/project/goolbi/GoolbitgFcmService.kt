package com.project.goolbi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GoolbitgFcmService: FirebaseMessagingService() {
    /**
     * FCM 등록 토큰이 새로 발급(혹은 갱신)되었을 때 호출되는 콜백
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 서버로 토큰 전송 등 토큰을 활용한 추가 작업을 수행
        Log.d("GoolbitgFcmService", "onNewToken: ${token}")
        sendTokenToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 푸시 메시지 수신 처리 (알림 표시 등)
        val title = message.notification?.title ?: "제목"
        val body = message.notification?.body ?: "내용"
        // ...
        showNotification(title, body)
    }

    private fun sendTokenToServer(token: String) {
        // 토큰을 서버에 저장하거나, 사용자 정보와 매핑하는 로직 등
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "FCM Default", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.project.presentation.R.drawable.ic_permission_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)

        manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
