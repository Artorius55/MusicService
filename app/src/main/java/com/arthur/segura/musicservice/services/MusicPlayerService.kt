package com.arthur.segura.musicservice.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.arthur.segura.musicservice.R

class MusicPlayerService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = MusicBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.folk_ai)
        mediaPlayer.isLooping = true

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_NOT_STICKY
    }

    fun startMusic() {
        if(!mediaPlayer.isPlaying)
            mediaPlayer.start()
    }

    fun pauseMusic() {
        if(mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification() : Notification {
        val pendingIntent = Intent(this, MusicPlayerService::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)

        return notificationBuilder.build()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Player Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    private companion object {
        const val CHANNEL_ID = "MusicPlayerServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}