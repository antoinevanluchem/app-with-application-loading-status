package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Entity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.notifications.cancelNotifications
import com.udacity.notifications.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager

    private var downloadID: Long = 0

    private lateinit var glide: DownloadEntity.Glide
    private lateinit var loadApp: DownloadEntity.LoadApp
    private lateinit var retrofit: DownloadEntity.Retrofit

    private var downloadedEntity: DownloadEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        notificationManager = this.getSystemService(NotificationManager::class.java)

        // Download entities
        glide = DownloadEntity.Glide(GLIDE_URL, getString(R.string.glide))
        loadApp = DownloadEntity.LoadApp(LOAD_APP_URL, getString(R.string.load_app))
        retrofit = DownloadEntity.Retrofit(RETROFIT_URL, getString(R.string.retrofit))

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentMain.customButton.setOnClickListener {
            this.getSystemService(NotificationManager::class.java).cancelNotifications()
            downloadedEntity = when (binding.contentMain.radioGroup.checkedRadioButtonId) {
                R.id.radioGlide -> glide
                R.id.radioLoadApp -> loadApp
                R.id.radioRetrofit -> retrofit
                else -> {
                    Toast.makeText(this, getString(R.string.please_select_file), Toast.LENGTH_SHORT)
                        .show()
                    binding.contentMain.customButton.cancelLoadingAnimation()
                    return@setOnClickListener
                }
            }
            download()
        }
    }

    //
    // Notifications
    //
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(true)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            downloadedEntity?.let {
                binding.contentMain.customButton.finishLoadingAnimation()

                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                val notificationManager = ContextCompat.getSystemService(
                    applicationContext,
                    NotificationManager::class.java
                ) as NotificationManager

                if (downloadID == id) {
                    notificationManager.sendNotification(
                        it.fileName,
                        DownloadStatus.Success,
                        applicationContext
                    )
                } else {
                    notificationManager.sendNotification(
                        it.fileName,
                        DownloadStatus.Fail,
                        applicationContext
                    )
                }
            }
        }
    }


    //
    // Download
    //

    private fun download() {
        downloadedEntity?.let {
            val request =
                DownloadManager.Request(Uri.parse(it.url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    //
    // Download Entities
    //
    sealed class DownloadEntity {
        abstract val url: String
        abstract val fileName: String

        data class Glide(override val url: String, override val fileName: String) : DownloadEntity()
        data class LoadApp(override val url: String, override val fileName: String) :
            DownloadEntity()

        data class Retrofit(override val url: String, override val fileName: String) :
            DownloadEntity()
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/trunk.zip"
    }
}