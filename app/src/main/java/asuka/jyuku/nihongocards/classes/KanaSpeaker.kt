package asuka.jyuku.nihongocards.classes

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Locale

/**
 * Reads kana aloud with the device's Japanese text-to-speech voice, so the app does not need
 * to ship audio files. The background music is ducked while a kana is spoken.
 *
 * Call [shutdown] when the screen that owns it goes away.
 */
class KanaSpeaker(context: Context) {

    enum class Status {
        /** The speech engine is still starting. */
        LOADING,
        READY,
        /** The engine knows Japanese but its voice is not downloaded yet: see [installVoiceIntent]. */
        MISSING_VOICE,
        /** No speech engine, or none that speaks Japanese. */
        UNAVAILABLE
    }

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    // Transient focus that lets the system lower the music while we speak.
    private val focusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(audioAttributes)
            .build()

    private val _status = MutableLiveData(Status.LOADING)
    val status: LiveData<Status> get() = _status

    private var tts: TextToSpeech? = null
    private var engine: String? = null
    private var isShutDown = false

    init {
        connect(engine = null)
    }

    fun speak(kana: Kana) {
        val tts = tts ?: return
        if (_status.value != Status.READY) return
        tts.speak(kana.character, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    /** Checks again for a voice that was missing, e.g. after the user downloaded it. */
    fun recheckVoice() {
        val tts = tts ?: return
        if (_status.value == Status.MISSING_VOICE) _status.value = useJapanese(tts)
    }

    /** Opens the engine's screen to download its voices. */
    fun installVoiceIntent(): Intent =
        Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            .setPackage(engine)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    fun shutdown() {
        isShutDown = true
        tts?.run {
            stop()
            shutdown()
        }
        tts = null
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    /** Starts [engine], or the device default when it is null. */
    private fun connect(engine: String?) {
        tts = TextToSpeech(appContext, { result -> onInit(result, engine) }, engine)
    }

    private fun onInit(result: Int, requestedEngine: String?) {
        if (isShutDown) return
        // Without any engine this runs inside the constructor, before [tts] is even set.
        if (result != TextToSpeech.SUCCESS) {
            _status.postValue(Status.UNAVAILABLE)
            return
        }
        val tts = tts ?: return
        val status = useJapanese(tts)

        // Some phones default to their own engine without Japanese: try Google's if installed.
        if (status != Status.READY && requestedEngine == null &&
            tts.defaultEngine != GOOGLE_TTS && tts.engines.any { it.name == GOOGLE_TTS }
        ) {
            tts.shutdown()
            connect(GOOGLE_TTS)
            return
        }

        engine = requestedEngine ?: tts.defaultEngine
        _status.postValue(status)
    }

    private fun useJapanese(tts: TextToSpeech): Status =
        when (tts.setLanguage(Locale.JAPAN)) {
            TextToSpeech.LANG_MISSING_DATA -> Status.MISSING_VOICE
            TextToSpeech.LANG_NOT_SUPPORTED -> Status.UNAVAILABLE
            else -> {
                tts.setAudioAttributes(audioAttributes)
                tts.setSpeechRate(SPEECH_RATE)
                tts.setOnUtteranceProgressListener(focusListener)
                Status.READY
            }
        }

    // Focus is taken when the speech really starts, so a flushed utterance giving it back
    // can't undo the focus of the one replacing it.
    private val focusListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            audioManager.requestAudioFocus(focusRequest)
        }

        override fun onDone(utteranceId: String?) = releaseFocus()

        override fun onStop(utteranceId: String?, interrupted: Boolean) = releaseFocus()

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) = releaseFocus()

        override fun onError(utteranceId: String?, errorCode: Int) = releaseFocus()

        private fun releaseFocus() {
            audioManager.abandonAudioFocusRequest(focusRequest)
        }
    }

    private companion object {
        const val GOOGLE_TTS = "com.google.android.tts"
        const val UTTERANCE_ID = "kana"

        // A bit slower than normal, which is easier to follow while learning.
        const val SPEECH_RATE = 0.85f
    }
}
