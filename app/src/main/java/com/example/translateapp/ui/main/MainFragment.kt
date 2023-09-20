package com.example.translateapp.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.translateapp.MainActivity
import com.example.translateapp.R
import com.example.translateapp.databinding.FragmentMainBinding
import java.util.Locale

class MainFragment : Fragment() {


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var isThereSecondCard = false
    private lateinit var textToSpeech: TextToSpeech



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainViewModel.isThereSecondCard.observe(viewLifecycleOwner) {
            if (!it) {
                binding.targetLayout.visibility = View.INVISIBLE
            } else {
                binding.targetLayout.visibility = View.VISIBLE
            }
        }


        binding.targetText.text = mainViewModel.text.value
        binding.textToTranslate.setText(mainViewModel.textToTranslate.value)


        binding.translate.setOnClickListener {
            mainViewModel.changeState()
            mainViewModel.translate(requireContext(), binding.textToTranslate.text.toString())
        }


        (requireActivity() as MainActivity).findViewById<LinearLayout>(R.id.bottom_nav_bar).visibility =
            View.VISIBLE


        // reading sound

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission(requireActivity() as MainActivity)
        } else {
            Log.e("hello", "click")
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(p0: Bundle?) {
                }

                override fun onBeginningOfSpeech() {
                    Log.e("hello", "start")
                }

                override fun onRmsChanged(p0: Float) {
                }

                override fun onBufferReceived(p0: ByteArray?) {
                }

                override fun onEndOfSpeech() {
                }

                override fun onError(p0: Int) {
                }

                override fun onResults(p0: Bundle?) {
                    val data = p0!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                    if (data?.get(0) != null) {
                        binding.textToTranslate.setText(data[0])
                        mainViewModel.translate(requireContext(), data[0])
                    }
                }

                override fun onPartialResults(p0: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onEvent(p0: Int, p1: Bundle?) {
                    TODO("Not yet implemented")
                }
            })
        }



        binding.microphone.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                Log.e("hello", "dd")
                speechRecognizer.startListening(speechRecognizerIntent)
            }
            false
        }




        // if cross is clicked then set translation text to zero
        binding.delete.setOnClickListener {
            binding.textToTranslate.setText("")
        }


        // sounding the text


        textToSpeech = TextToSpeech(
            requireContext()
        ) { i ->
            // if No error is found then only it will run
            if (i != TextToSpeech.ERROR) {
                // To Choose language of speech
                textToSpeech.language = Locale.forLanguageTag("eng")
            }
        }

        binding.firstSound.setOnClickListener {
            textToSpeech.speak(binding.textToTranslate.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }

        binding.targetSound.setOnClickListener {
            textToSpeech.speak(binding.textToTranslate.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }


        return root
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