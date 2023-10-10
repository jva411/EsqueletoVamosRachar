package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        findViewById<FloatingActionButton>(R.id.btnPlay).setOnClickListener {
            val preco = this.conta / this.pessoas
            val reais = preco.toInt()
            val centavos = ((preco - reais) * 100).toInt()

            val texto = when {
                isSomeInputEmpty() -> "Preencha todos os campos"
                this.error -> "Algum erro aconteceu durante os cálculos"
                centavos == 0 -> "Cada um deve pagar $reais reais"
                else -> "Cada um deve pagar $reais reais e $centavos centavos"
            }
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
        }
        findViewById<FloatingActionButton>(R.id.btnShare).setOnClickListener {
            if (isSomeInputEmpty()) {
                tts.speak("Preencha todos os campos", TextToSpeech.QUEUE_FLUSH, null, null)
                return@setOnClickListener
            } else if (this.error) {
                tts.speak("Algum erro aconteceu durante os cálculos", TextToSpeech.QUEUE_FLUSH, null, null)
                return@setOnClickListener
            }

            val preco = this.conta / this.pessoas
            val texto = getString(R.string.app_name) + "\n" +
                    "Cada um deve pagar R$%.2f".format(preco)

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, texto)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
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
            if (this.pessoas == 0) {
                this.error = true
                findViewById<TextView>(R.id.tvConta).text = "R$0,00"
                return
            }

            val preco = this.conta / this.pessoas
            findViewById<TextView>(R.id.tvConta).text = if(isSomeInputEmpty()) "R$0,00" else "R$%.2f".format(preco)
        }
    }

    private fun isSomeInputEmpty(): Boolean {
        return findViewById<EditText>(R.id.inputPessoas).text.isEmpty() ||
                findViewById<EditText>(R.id.inputConta).text.isEmpty()
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

