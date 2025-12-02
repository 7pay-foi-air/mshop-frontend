// SpeechToTextManagerSingle.kt
package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechToTextManagerSingle(
    private val context: Context,
    private val onPartialResult: (String) -> Unit = {},
    private val onResult: (String) -> Unit = {},
    private val onError: (String) -> Unit = {}
) {
    private val TAG = "STT_SINGLE"
    private var recognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    @Volatile var isListening: Boolean = false
        private set

    private val intent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            // force Croatian - change if you want english
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("hr", "HR"))
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hr-HR")

            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

            // more tolerant to silence so it won't cut too fast
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000L)
        }
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech")
            // waiting for results callback
        }

        override fun onError(error: Int) {
            val msg = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
                SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
                SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
                SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
                SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
                else -> "ERROR_UNKNOWN: $error"
            }
            Log.e(TAG, "onError: $msg")
            mainHandler.post {
                isListening = false
                onError(msg)
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull() ?: ""
            Log.d(TAG, "onResults: $text")
            mainHandler.post {
                isListening = false
                onResult(text)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
            if (partial.isNotEmpty()) {
                Log.d(TAG, "onPartialResults: $partial")
                mainHandler.post { onPartialResult(partial) }
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun ensureRecognizer() {
        if (recognizer == null) {
            try {
                recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                recognizer?.setRecognitionListener(listener)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't create SpeechRecognizer: ${e.message}")
                mainHandler.post { onError("Couldn't create SpeechRecognizer: ${e.message}") }
            }
        }
    }

    /** Start a single-shot listening session. */
    fun startListeningOnce() {
        if (isListening) return
        ensureRecognizer()
        try {
            isListening = true
            recognizer?.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            Log.e(TAG, "startListening failed: ${e.message}")
            mainHandler.post { onError("startListening failed: ${e.message}") }
        }
    }

    /** Stop listening (if currently listening). */
    fun stopListening() {
        try {
            recognizer?.stopListening()
            recognizer?.cancel()
        } catch (e: Exception) {
            // ignore
        } finally {
            isListening = false
        }
    }

    fun destroy() {
        try {
            recognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {}
                override fun onResults(results: Bundle?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            recognizer?.destroy()
        } catch (e: Exception) {
            // ignore
        } finally {
            recognizer = null
            isListening = false
        }
    }
}
