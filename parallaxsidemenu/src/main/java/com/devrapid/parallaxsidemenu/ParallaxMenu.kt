package com.devrapid.parallaxsidemenu

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.devrapid.kotlinknifer.animatorListener
import kotlinx.android.synthetic.main.menu_left_side.view.ib_close
import kotlinx.android.synthetic.main.menu_left_side.view.iv_icon
import kotlinx.android.synthetic.main.menu_left_side.view.rv_menu
import kotlinx.android.synthetic.main.menu_left_side.view.tv_name

/**
 * @author  jieyi
 * @since   2/8/18
 */
@SuppressLint("CheckResult")
class ParallaxMenu @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    //region Customize variable
    var menuAlphaDuration = 450L
    var menuAlphaDelay = 150L
    var animDuration = 600L
    var animInterpolator = DecelerateInterpolator(3f)

    var isOpenMenu = false
        private set
    //endregion

    //region Private variable
    private lateinit var activity: Activity
    private lateinit var decorView: ViewGroup
    /* The main menu (the front activity) */
    private lateinit var mainView: ViewGroup

    private val ibClose by lazy { menuLayoutView.ib_close }
    private val ivIcon by lazy { menuLayoutView.iv_icon }
    private val tvName by lazy { menuLayoutView.tv_name }
    private val rvMenuHolder by lazy { menuLayoutView.rv_menu }
    private val menuLayoutView = LayoutInflater.from(context).inflate(R.layout.menu_left_side, this)
    private val linearLayoutManager by lazy { LinearLayoutManager(context) }

    private val openMenuAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(this@ParallaxMenu, "translationX", (-measuredWidth).toFloat(), 0f)
                             .animOptional(animDuration),
                         ObjectAnimator.ofFloat(this@ParallaxMenu, "alpha", 0f, 1f)
                             .animOptional(menuAlphaDuration, menuAlphaDelay))
            addListener(animatorListener { onAnimationEnd { this@ParallaxMenu.apply { x = 0.toFloat() } } })
        }
    }
    private val closeMenuAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(this@ParallaxMenu, "translationX", 0f, (-measuredWidth).toFloat())
                             .animOptional(animDuration),
                         ObjectAnimator.ofFloat(this@ParallaxMenu, "alpha", 1f, 0f)
                             .animOptional(animDuration))
            addListener(animatorListener { onAnimationEnd { x = (-measuredWidth).toFloat() } })
        }
    }
    private val openActivityAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(mainView, "translationX", 0f, (mainView.measuredWidth / 3).toFloat()),
                         ObjectAnimator.ofFloat(mainView, "translationY", 0f, 250f))
            addListener(animatorListener {
                onAnimationEnd {
                    mainView.apply {
                        x = (mainView.measuredWidth / 3).toFloat()
                        y = 250f
                    }
                }
            })
        }.animOptional(animDuration)
    }
    private val closeActivityAnim by lazy {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(mainView, "translationX", (mainView.measuredWidth / 3).toFloat(), 0f),
                         ObjectAnimator.ofFloat(mainView, "translationY", 250f, 0f))
            addListener(animatorListener {
                onAnimationEnd {
                    mainView.apply {
                        x = 0f
                        y = 0f
                    }
                }
            })
        }.animOptional(animDuration)
    }
    private val openMenu by lazy {
        AnimatorSet().apply {
            playTogether(openActivityAnim, openMenuAnim)
            addListener(animatorListener { onAnimationEnd { isOpenMenu = true } })
        }
    }
    private val closeMenu by lazy {
        AnimatorSet().apply {
            playTogether(closeActivityAnim, closeMenuAnim)
            addListener(animatorListener { onAnimationEnd { isOpenMenu = false } })
        }
    }
    private val menuItems by lazy { mutableListOf<SparseArray<Any>>() }
    //endregion

    init {
        // This is using RxJava.
//        ibClose.clicks()
//            .debounce(200, TimeUnit.MILLISECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { closeMenu() }
        ibClose.setOnClickListener { closeMenu() }
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
        decorView.background = background
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

        setOverlapMenuElements(menuItems)
    }

    fun setOverlapMenuElements(list: List<SparseArray<Any>>) {
        rvMenuHolder.apply {
            layoutManager = linearLayoutManager
            adapter = MenuRecyclerView(list)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class MenuRecyclerView(private val menuItems: List<SparseArray<Any>>) : RecyclerView.Adapter<MenuRecyclerView.MenuViewHolder>() {
        class MenuViewHolder(var menu: View) : RecyclerView.ViewHolder(menu)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false))

        override fun getItemCount() = menuItems.size

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            // Set the listener for changing the menu button text color.
//            if (menuItems[position][Int.MIN_VALUE].isNull()) menuItems[position].append(Int.MIN_VALUE, { index: Int ->
//                (holder.menu as TextView).textColor = holder.menu.getResColor(if (index == position) R.color.textColorPrimary else R.color.colorAccent)
//            })
//
//            ((holder.menu as TextView) to menuItems[position]).apply {
//                first.text = second[INDEX_NAME].toString()
//                // Check init menu text variable.
//                if (second[INDEX_CURRENT_MENU_FLAG].isNotNull()) {
//                    // The item position is matched, then set the clicked color.
//                    if (position == (second[INDEX_CURRENT_MENU_FLAG] as Int)) first.textColor = first.getResColor(R.color.textColorPrimary)
//                    // Remove the initial flag.
//                    second.remove(INDEX_CURRENT_MENU_FLAG)
//                }
//                first.clicks()
//                    .debounce(200, TimeUnit.MILLISECONDS)
//                    .doAfterNext {
//                        // Notify other menu items, which position I clicked.
//                        menuItems.forEach {
//                            ui { it[Int.MIN_VALUE].takeIf { it.isNotNull() }?.let { (it as (Int) -> Unit)(position) } }
//                        }
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe {
//                        // Transfer to the other activity.
//                        if (second[INDEX_CLICK_LISTENER].isNotNull())
//                            (second[INDEX_CLICK_LISTENER] as (View) -> Unit).invoke(first)
//                    }
//            }
        }
    }

    fun openMenu() {
        if (!isOpenMenu) openMenu.start()
    }

    fun closeMenu(afterAnimation: ((View) -> Unit)? = null) {
        if (isOpenMenu) closeMenu.apply {
            // A new listener is for triggering the close menu, and the maximum is two.
            if (null != afterAnimation && 2 > listeners.size) {
                addListener(animatorListener {
                    onAnimationStart { afterAnimation(this@ParallaxMenu) }
                    onAnimationEnd {
                        // After finishing the animation, reset the listener.
                        removeLastListener()
                    }
                })
            }
        }.start()
    }

    fun setBackground(@ColorInt color: Int) {
        setBackgroundColor(color)
        if (::decorView.isInitialized) decorView.background = background
    }

    fun setBackgroundColorRes(@ColorRes colorId: Int) = setBackground(ContextCompat.getColor(context, colorId))

    fun setBackgroundDrawableRes(@DrawableRes drawableId: Int) {
        background = ContextCompat.getDrawable(context, drawableId)
        if (::decorView.isInitialized) decorView.background = background
    }

    fun setName(name: String) {
        tvName.text = name
    }

    fun setIcon(uri: String) {}

    fun setIcon(@DrawableRes resId: Int) = ivIcon.setImageResource(resId)

    fun setIcon(bitmap: Bitmap) = ivIcon.setImageBitmap(bitmap)

    fun setIcon(drawable: Drawable) = ivIcon.setImageDrawable(drawable)

    fun setCloseIcon(uri: String) {}

    fun setCloseIcon(@DrawableRes resId: Int) = ibClose.setImageResource(resId)

    fun setCloseIcon(bitmap: Bitmap) = ibClose.setImageBitmap(bitmap)

    fun setCloseIcon(drawable: Drawable) = ibClose.setImageDrawable(drawable)

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