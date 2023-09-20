package com.example.translateapp.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.example.translateapp.MainActivity
import com.example.translateapp.R

import com.example.translateapp.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {


    lateinit var binding: FragmentSplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(inflater)


        (requireActivity() as MainActivity).findViewById<LinearLayout>(R.id.bottom_nav_bar).visibility = View.INVISIBLE

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())
        }, 2000)

        return binding.root
    }


}