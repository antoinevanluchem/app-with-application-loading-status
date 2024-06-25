package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
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

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        createChannel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_name))
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Unable to use DataBinding, no idea why
        val customButton = findViewById<LoadingButton>(R.id.customButton)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        customButton.setOnClickListener {
            when(radioGroup.checkedRadioButtonId){
                R.id.radioGlide -> download(GLIDE_URL)
                R.id.radioLoadApp -> download(LOAD_APP_URL)
                R.id.radioRetrofit -> download(RETROFIT_URL)
                else -> Toast.makeText(this,getString(R.string.please_select_file), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //
    // Notifications
    //
    private fun createChannel(channelId: String, channelName: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                    setShowBadge(true)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            findViewById<LoadingButton>(R.id.customButton).finishLoadingAnimation()

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val notificationManager = ContextCompat.getSystemService(applicationContext,
                NotificationManager::class.java) as NotificationManager

            notificationManager.cancelNotifications()
            if (downloadID == id) {
                notificationManager.sendNotification("Download successful!", applicationContext)
            }
            else{
                notificationManager.sendNotification("Download failed!", applicationContext)
            }
        }
    }


    //
    // Download
    //

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
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