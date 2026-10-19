package asuka.jyuku.nihongocards

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import asuka.jyuku.nihongocards.classes.AppPreferences
import asuka.jyuku.nihongocards.classes.MusicPlayer

class NihongoCardsApp : Application() {

    val preferences: AppPreferences by lazy { AppPreferences(this) }
    val musicPlayer: MusicPlayer by lazy { MusicPlayer(this, preferences) }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(musicPlayer)
    }
}

val Fragment.nihongoApp: NihongoCardsApp
    get() = requireActivity().application as NihongoCardsApp
