package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.notifications.cancelNotifications
import com.udacity.notifications.sendNotification
import kotlin.properties.Delegates
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Notifications
    private lateinit var notificationManager: NotificationManager
    private val RC_NOTIFICATION = 99
    private var notificationsAllowed by Delegates.notNull<Boolean>()

    // Downloads
    private lateinit var glide: DownloadEntity.Glide
    private lateinit var loadApp: DownloadEntity.LoadApp
    private lateinit var retrofit: DownloadEntity.Retrofit
    private var downloadID: Long = 0
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

        registerOnClickListener()

        requestNotificationPermission()
    }

    //
    // On Click Listener
    //
    private fun registerOnClickListener() {
            binding.contentMain.customButton.setOnClickListener {
            val customButton = binding.contentMain.customButton
            val notificationManager = getSystemService(NotificationManager::class.java)
            val checkedRadioButtonId = binding.contentMain.radioGroup.checkedRadioButtonId

            if (!notificationsAllowed) {
                showPleaseAllowNotificationsToast()
                customButton.cancelLoadingAnimation()
                return@setOnClickListener
            }

            notificationManager.cancelNotifications()

            downloadedEntity = when (checkedRadioButtonId) {
                R.id.radioGlide -> glide
                R.id.radioLoadApp -> loadApp
                R.id.radioRetrofit -> retrofit
                else -> {
                    Toast.makeText(this, getString(R.string.please_select_file), Toast.LENGTH_SHORT)
                        .show()
                    customButton.cancelLoadingAnimation()
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), RC_NOTIFICATION)
        } else {
            notificationsAllowed = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_NOTIFICATION) {
            notificationsAllowed = if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                showPleaseAllowNotificationsToast()
                false
            }
        }
    }

    private fun showPleaseAllowNotificationsToast() {
        Toast.makeText(this, getString(R.string.please_allow_notifications), Toast.LENGTH_SHORT)
            .show()
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