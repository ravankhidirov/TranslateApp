package com.example.translateapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import com.example.translateapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    private lateinit var chatFragmentIcon:LinearLayout
    private lateinit var cameraFragmentIcon:LinearLayout
    private lateinit var mainFragmentIcon:ConstraintLayout
    private lateinit var historyFragmentIcon:LinearLayout
    private lateinit var favouriteFragmentIcon:LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController



        chatFragmentIcon = findViewById(R.id.chatLinearLayout)
        cameraFragmentIcon = findViewById(R.id.cameraLinearLayout)
        mainFragmentIcon = findViewById(R.id.mainConstraintLayout)
        historyFragmentIcon = findViewById(R.id.historyLinearLayout)
        favouriteFragmentIcon = findViewById(R.id.favouriteLinearLayout)


        chatFragmentIcon.setOnClickListener {
            navController.navigate(R.id.chatFragment)
        }
        cameraFragmentIcon.setOnClickListener {
            navController.navigate(R.id.cameraFragment)
        }
        mainFragmentIcon.setOnClickListener {
            navController.navigate(R.id.mainFragment)
        }
        historyFragmentIcon.setOnClickListener {
            navController.navigate(R.id.historyFragment)
        }
        favouriteFragmentIcon.setOnClickListener {
            navController.navigate(R.id.favouriteFragment)
        }


    }
}