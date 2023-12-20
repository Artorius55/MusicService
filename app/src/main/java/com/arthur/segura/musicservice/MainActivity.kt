package com.arthur.segura.musicservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.arthur.segura.musicservice.databinding.ActivityMainBinding
import com.arthur.segura.musicservice.services.MusicPlayerService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var musicService: MusicPlayerService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicPlayerService.MusicBinder
            binder?.let {
                musicService = it.getService()
                isBound = true
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        setupViews()
        bindService()
    }

    private fun setupViews() {
        binding.btnStart.setOnClickListener {
            musicService?.startMusic()
        }
        binding.btnStop.setOnClickListener {
            musicService?.pauseMusic()
        }
    }

    private fun bindService() {
        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}