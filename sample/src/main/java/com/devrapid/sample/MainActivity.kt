package com.devrapid.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import com.devrapid.parallaxsidemenu.ParallaxMenu
import com.devrapid.parallaxsidemenu.R
import kotlinx.android.synthetic.main.activity_main.btn

class MainActivity : AppCompatActivity() {
    private val menu by lazy { ParallaxMenu(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu.apply {
            bkgDrawableRes = R.drawable.bkg_gradient
            attachToActivity(this@MainActivity)
            setOverlapMenuElements(listOf(SparseArray<Any>().apply { append(0, "Hello") },
                                          SparseArray<Any>().apply { append(0, "World") },
                                          SparseArray<Any>().apply { append(0, "App") },
                                          SparseArray<Any>().apply { append(0, "Parallax") },
                                          SparseArray<Any>().apply { append(0, "Jieyi") },
                                          SparseArray<Any>().apply { append(0, "Test") },
                                          SparseArray<Any>().apply { append(0, "Exit") }))
        }

        btn.setOnClickListener {
            menu.openMenu()
        }
    }
}
