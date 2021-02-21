package com.ben.tribunewsdemo.view.ui.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.view.ui.fragment.UploadPhotoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navigationController = findNavController(R.id.fragment)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.uploadPhotoFragment, R.id.galleryFragment))
        setupActionBarWithNavController(navigationController, appBarConfiguration)

        bottomNavView.setupWithNavController(navigationController)

        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        requestPermissions(permissions, UploadPhotoFragment.PERMISSION_CODE)
    }
}