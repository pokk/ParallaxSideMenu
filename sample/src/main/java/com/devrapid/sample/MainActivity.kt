package com.devrapid.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.devrapid.parallaxsidemenu.ParallaxMenu
import com.devrapid.parallaxsidemenu.R
import kotlinx.android.synthetic.main.activity_main.btn

class MainActivity : AppCompatActivity() {
    private val menu by lazy { ParallaxMenu(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu.apply {
            setBackground(Color.WHITE)
            attachToActivity(this@MainActivity)
        }

        btn.setOnClickListener {
            menu.openMenu()
        }
    }
}
