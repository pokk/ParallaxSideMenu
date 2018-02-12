package com.devrapid.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.devrapid.kotlinknifer.getResColor
import com.devrapid.parallaxsidemenu.ParallaxMenu
import com.devrapid.parallaxsidemenu.R
import kotlinx.android.synthetic.main.activity_main.ib_menu
import org.jetbrains.anko.textColor

class MainActivity : AppCompatActivity() {
    private val menu by lazy { ParallaxMenu(this) }
    private val list by lazy {
        listOf(
            createMenu {
                text = "Foo"
                setOnClickListener(::clickListener)
            },
            createMenu {
                text = "Hello"
                setOnClickListener(::clickListener)
            },
            createMenu {
                text = "Test"
                setOnClickListener(::clickListener)
            },
            createMenu {
                text = "Jieyi"
                setOnClickListener(::clickListener)
            },
            createMenu {
                text = "World"
                setOnClickListener(::clickListener)
            },
            createMenu {
                text = "Bye Bye"
                setOnClickListener(::clickListener)
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu.apply {
            nameTextView.apply {
                text = "Jieyi Wu"
                textColor = Color.WHITE
//                typeface = resources.getFont(R.font)
            }
            bkgDrawableRes = R.drawable.bkg_gradient_1
            attachToActivity(this@MainActivity)
            setOverlapMenuElements(list)
        }

        ib_menu.setOnClickListener { menu.openMenu() }
    }

    private fun createMenu(block: TextView.() -> Unit) =
        (LayoutInflater.from(this).inflate(R.layout.part_menu_item, null) as TextView).apply(block)

    private fun clickListener(v: View) {
        v as TextView
        list.forEach {
            it.textColor = this@MainActivity.getResColor(if (it != v) R.color.colorText else R.color.colorClickedText)
        }
    }
}
