package com.example.translateapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.translateapp.MainActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class MainViewModel:ViewModel() {



    // is there a translated text? if yes show second cardview
    private var _isThereSecondCard = MutableLiveData<Boolean>().apply {
        value = false
    }
    var isThereSecondCard:LiveData<Boolean> = _isThereSecondCard

    fun changeState(){
        _isThereSecondCard.value = true
    }




    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.GERMAN)
        .build()
    private val englishGermanTranslator = Translation.getClient(options)

    private val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()



    private var _textToTranslate = MutableLiveData<String>().apply {
        value = ""
    }
    var textToTranslate: LiveData<String> = _textToTranslate







    // For translation

    private var _translatedText = MutableLiveData<String>().apply {
        value = "..."
    }
    var text: LiveData<String> = _translatedText


    fun translate(context: Context,text:String)
    {

        Toast.makeText(context,"Translate function is called!", Toast.LENGTH_SHORT).show()
        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                englishGermanTranslator.translate(text)
                    .addOnSuccessListener {
                        _translatedText.value = text
                    }
                    .addOnFailureListener {
                        Toast.makeText(context,"Impossible", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context,"Impossible", Toast.LENGTH_SHORT).show()
            }

    }


    // For sound


    fun sound(context: Context,activity: MainActivity){




    }


    private fun checkPermission(activity: MainActivity) {
        val recordAudioRequestCode = 1
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            recordAudioRequestCode
        )
    }











}