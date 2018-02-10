package com.devrapid.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.TextView
import com.devrapid.parallaxsidemenu.ParallaxMenu
import com.devrapid.parallaxsidemenu.R
import kotlinx.android.synthetic.main.activity_main.btn
import kotlinx.android.synthetic.main.activity_main.ib_menu

class MainActivity : AppCompatActivity() {
    private val menu by lazy { ParallaxMenu(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu.apply {
            bkgDrawableRes = R.drawable.bkg_gradient_1
            attachToActivity(this@MainActivity)
            setOverlapMenuElements(listOf(createMenu { text = "Foo" },
                                          createMenu { text = "Hello" },
                                          createMenu { text = "Test" },
                                          createMenu { text = "Jieyi" },
                                          createMenu { text = "World" },
                                          createMenu { text = "Bye Bye" }))
        }

        btn.setOnClickListener {
            menu.openMenu()
        }
        ib_menu.setOnClickListener {
            menu.openMenu()
        }
    }

    private fun createMenu(block: TextView.() -> Unit) =
        (LayoutInflater.from(this).inflate(R.layout.part_menu_item, null) as TextView).apply(block)
}
