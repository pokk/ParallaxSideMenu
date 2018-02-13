package com.devrapid.parallaxsidemenu

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.menu_left_side.view.ib_close
import kotlinx.android.synthetic.main.menu_left_side.view.iv_icon
import kotlinx.android.synthetic.main.menu_left_side.view.ll_menu
import kotlinx.android.synthetic.main.menu_left_side.view.tv_name

/**
 * @author  jieyi
 * @since   02/08/18
 */
open class ParallaxMenu @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    //region Customize variable
    var menuAlphaDuration = 450L
    var menuAlphaDelay = 150L
    var animDuration = 600L
    var animInterpolator = DecelerateInterpolator(3f)
    var shiftX = .5f
    var shiftY = .1f

    var bkgDrawableRes = -1
    var bkgColor = -1
    var bkgColorRes = 0
        set(value) {
            field = value
            bkgColor = ContextCompat.getColor(context, value)
        }

    var isOpenMenu = false
        private set
    val closeButton by lazy { menuLayoutView.ib_close }
    val imageIcon by lazy { menuLayoutView.iv_icon }
    val nameTextView by lazy { menuLayoutView.tv_name }
    //endregion

    //region Private variable
    private lateinit var activity: Activity
    private lateinit var decorView: ViewGroup
    /* The main menu (the front activity) */
    private lateinit var mainView: ViewGroup

    private val rvMenuHolder by lazy { menuLayoutView.ll_menu }
    private val menuLayoutView = LayoutInflater.from(context).inflate(R.layout.menu_left_side, this)

    private val openMenuAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(this@ParallaxMenu, "translationX", (-measuredWidth).toFloat(), 0f)
                             .animOptional(animDuration),
                         ObjectAnimator.ofFloat(this@ParallaxMenu, "alpha", 0f, 1f)
                             .animOptional(menuAlphaDuration, menuAlphaDelay))
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    this@ParallaxMenu.apply { x = 0.toFloat() }
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            })
        }
    }
    private val closeMenuAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(this@ParallaxMenu, "translationX", 0f, (-measuredWidth).toFloat())
                             .animOptional(animDuration),
                         ObjectAnimator.ofFloat(this@ParallaxMenu, "alpha", 1f, 0f)
                             .animOptional(animDuration))
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    x = (-measuredWidth).toFloat()
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            })
        }
    }
    private val openActivityAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(mainView, "translationX", 0f, mainView.measuredWidth * shiftX),
                         ObjectAnimator.ofFloat(mainView, "translationY", 0f, mainView.measuredHeight * shiftY))
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    mainView.apply {
                        x = mainView.measuredWidth * shiftX
                        y = mainView.measuredHeight * shiftY
                    }
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            })
        }.animOptional(animDuration)
    }
    private val closeActivityAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(mainView, "translationX", mainView.measuredWidth * shiftX, 0f),
                         ObjectAnimator.ofFloat(mainView, "translationY", mainView.measuredHeight * shiftY, 0f))
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    mainView.apply {
                        x = 0f
                        y = 0f
                    }
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            })
        }.animOptional(animDuration)
    }
    private val openMenu by lazy {
        AnimatorSet().apply {
            playTogether(openActivityAnim, openMenuAnim)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    isOpenMenu = true
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            })
        }
    }
    private val closeMenu by lazy {
        AnimatorSet().apply {
            playTogether(closeActivityAnim, closeMenuAnim)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    isOpenMenu = false
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            })
        }
    }
    private val menuItems by lazy { mutableListOf<SparseArray<Any>>() }
    //endregion

    init {
        //region This is using RxJava.
//        closeButton.clicks()
//            .debounce(200, TimeUnit.MILLISECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { closeMenu() }
        //endregion
        closeButton.setOnClickListener { closeMenu() }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // For hiding the menu view.
        x = -right.toFloat()
    }

    fun attachToActivity(activity: Activity) {
        this.activity = activity
        decorView = activity.window.decorView as ViewGroup
        // Set the same background
        when {
            0 < bkgColor -> decorView.setBackgroundColor(bkgColor)
            0 < bkgDrawableRes -> decorView.background = ContextCompat.getDrawable(context, bkgDrawableRes)
        }
        // Set the first view from the activity into a new view group (showing view).
        mainView = ParallaxMain(context).apply {
            // Get the activity's first view.
            realActivity = decorView.getChildAt(0).apply {
                // Remove the first view from the activity.
                decorView.removeViewAt(0)
            }
        }
        // Insert the showing view (the front view) into basic view group.
        decorView.addView(mainView)
        // Insert the menu view into basic view group for hiding itself.
        decorView.addView(this, 0)
    }

    fun setOverlapMenuElements(list: List<TextView>) {
        list.forEach {
            // Removed itself from the parent group view.
            if (null != it.parent) (it.parent as ViewGroup).removeView(it)
            rvMenuHolder.addView(it)
        }
    }

    fun openMenu() {
        if (!isOpenMenu) openMenu.start()
    }

    fun closeMenu(afterAnimation: ((View) -> Unit)? = null) {
        if (isOpenMenu) closeMenu.apply {
            // A new listener is for triggering the close menu, and the maximum is two.
            if (null != afterAnimation && 2 > listeners.size) {
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {
                        // After finishing the animation, reset the listener.
                        removeLastListener()
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        afterAnimation(this@ParallaxMenu)
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                })
            }
        }.start()
    }

    private fun ObjectAnimator.animOptional(duration: Long = 600, delay: Long = 0) = apply {
        interpolator = animInterpolator
        startDelay = delay
        this.duration = duration
    }

    private fun AnimatorSet.animOptional(duration: Long = 600, delay: Long = 0) = apply {
        interpolator = animInterpolator
        startDelay = delay
        this.duration = duration
    }

    private fun Animator.removeLastListener() =
        listeners.lastOrNull()?.let { removeListener(it) }
}