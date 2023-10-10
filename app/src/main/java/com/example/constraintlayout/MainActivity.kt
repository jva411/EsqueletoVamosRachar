package com.example.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var conta = 0.0f
    private var pessoas = 0
    private var error = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, this)

        findViewById<EditText>(R.id.inputConta).addTextChangedListener {
             updateConta(true, it.toString())
        }
        findViewById<EditText>(R.id.inputPessoas).addTextChangedListener {
            updateConta(false, it.toString())
        }

    }

    private fun updateConta(isConta: Boolean, value: String) {
        try {
            if (isConta) {
                this.conta = value.toFloat()
            } else {
                this.pessoas = value.toInt()
            }
            this.error = false
        } catch (ex: Exception) {
            this.error = true
        } finally {
            val preco = this.conta / this.pessoas
            findViewById<TextView>(R.id.tvConta).text = if(isSomeInputEmpty()) "R$0,00" else "R$.2f".format(preco)
        }
    }

    private fun isSomeInputEmpty(): Boolean {
        return findViewById<EditText>(R.id.inputPessoas).text.isEmpty() ||
                findViewById<EditText>(R.id.inputConta).text.isEmpty()
    }

    fun clickFalar(v: View){
        tts.speak("Oi Sumido", TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onDestroy() {
            // Release TTS engine resources
            tts.stop()
            tts.shutdown()
            super.onDestroy()
        }

    override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is initialized successfully
                tts.language = Locale.getDefault()
                Log.d("PDM23","Sucesso na Inicialização")
            } else {
                // TTS engine failed to initialize
                Log.e("PDM23", "Failed to initialize TTS engine.")
            }
        }


}

