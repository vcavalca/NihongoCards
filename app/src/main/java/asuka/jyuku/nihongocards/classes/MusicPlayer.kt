package asuka.jyuku.nihongocards.classes

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import asuka.jyuku.nihongocards.R

/**
 * Background music for the whole app.
 *
 * Lives as long as the process and observes ProcessLifecycleOwner, so it pauses when the
 * app goes to the background and is not restarted when an Activity is recreated
 * (e.g. on rotation). It also respects audio focus (calls, other music apps).
 */
class MusicPlayer(
    context: Context,
    private val preferences: AppPreferences
) : DefaultLifecycleObserver {

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(audioAttributes)
        .setOnAudioFocusChangeListener(
            { change -> onAudioFocusChange(change) },
            Handler(Looper.getMainLooper())
        )
        .build()

    private var player: MediaPlayer? = null
    private var isPrepared = false
    private var isInForeground = false
    private var hasAudioFocus = false

    val isEnabled: Boolean get() = preferences.musicEnabled
    val volume: Int get() = preferences.musicVolume

    fun setEnabled(enabled: Boolean) {
        preferences.musicEnabled = enabled
        updatePlayback()
    }

    fun setVolume(volume: Int) {
        preferences.musicVolume = volume
        applyVolume()
    }

    override fun onStart(owner: LifecycleOwner) {
        isInForeground = true
        updatePlayback()
    }

    override fun onStop(owner: LifecycleOwner) {
        isInForeground = false
        updatePlayback()
    }

    private fun updatePlayback() {
        if (preferences.musicEnabled && isInForeground) play() else stop()
    }

    private fun play() {
        if (!hasAudioFocus) {
            hasAudioFocus = audioManager.requestAudioFocus(focusRequest) ==
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            if (!hasAudioFocus) return
        }
        val current = player
        when {
            current == null -> preparePlayer() // starts once prepared
            isPrepared && !current.isPlaying -> current.start()
        }
    }

    /** Pauses (keeping the position) and gives the audio focus back. */
    private fun stop() {
        pausePlayer()
        if (!preferences.musicEnabled) releasePlayer()
        if (hasAudioFocus) {
            audioManager.abandonAudioFocusRequest(focusRequest)
            hasAudioFocus = false
        }
    }

    private fun pausePlayer() {
        player?.let { if (isPrepared && it.isPlaying) it.pause() }
    }

    private fun onAudioFocusChange(change: Int) {
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                hasAudioFocus = true
                updatePlayback()
            }
            // Another app took the focus for good (e.g. a music app): stop until we are
            // brought back to the foreground.
            AudioManager.AUDIOFOCUS_LOSS -> {
                hasAudioFocus = false
                pausePlayer()
                audioManager.abandonAudioFocusRequest(focusRequest)
            }
            // Temporary loss (e.g. a phone call): we get AUDIOFOCUS_GAIN back afterwards.
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pausePlayer()
        }
    }

    private fun preparePlayer() {
        val afd = appContext.resources.openRawResourceFd(R.raw.soudtrack) ?: return
        player = MediaPlayer().apply {
            setAudioAttributes(audioAttributes)
            afd.use { setDataSource(it.fileDescriptor, it.startOffset, it.length) }
            isLooping = true
            setOnPreparedListener {
                isPrepared = true
                applyVolume()
                updatePlayback()
            }
            setOnErrorListener { _, _, _ ->
                releasePlayer()
                true
            }
            prepareAsync()
        }
    }

    private fun applyVolume() {
        val level = preferences.musicVolume / 100f
        if (isPrepared) player?.setVolume(level, level)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        isPrepared = false
    }
}
