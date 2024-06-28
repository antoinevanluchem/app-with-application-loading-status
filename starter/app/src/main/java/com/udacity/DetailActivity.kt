package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.notifications.Keys
import com.udacity.notifications.cancelNotifications
import com.udacity.notifications.getExtra

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.contentDetail.fileName = intent.getExtra(Keys.FileName)
        intent.getExtra(Keys.Status)?.let {
            binding.contentDetail.status = it.name

            binding.contentDetail.statusTextView.setTextColor(
                if (it == DownloadStatus.Fail) {
                    Color.RED
                } else {
                    Color.GREEN
                }
            )
        }

        binding.contentDetail.okButton.setOnClickListener {
            this.getSystemService(NotificationManager::class.java).cancelNotifications()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
