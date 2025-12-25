package com.ext.coordinator_tablayout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout

class CoordinatorTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private val appBarLayout: AppBarLayout
    private val collapsingToolbar: CollapsingToolbarLayout
    private val headerImageView: ImageView
    private val tabLayout: TabLayout
    private val viewPager: ViewPager
    private val toolbar: androidx.appcompat.widget.Toolbar

    private var imageArray: IntArray? = null
    private var colorArray: IntArray? = null
    private var onImageLoadListener: OnImageLoadListener? = null
    private var onTabSelectedListener: OnTabSelectedListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.coordinator_tab_layout, this, true)

        appBarLayout = findViewById(R.id.appBarLayout)
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        headerImageView = findViewById(R.id.headerImageView)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        toolbar = findViewById(R.id.toolbar)
        collapsingToolbar.isTitleEnabled = true
        appBarLayout.setBackgroundColor(Color.TRANSPARENT)
        collapsingToolbar.setContentScrim(null)
        collapsingToolbar.setStatusBarScrim(null)
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        tabLayout.setBackgroundColor(Color.RED)

        appBarLayout.apply {
            setBackgroundColor(Color.TRANSPARENT)
            background = null
            elevation = 0f
            stateListAnimator = null
            outlineProvider = null
            isLiftOnScroll = false
        }

        collapsingToolbar.apply {
            setContentScrim(null)
            setStatusBarScrim(null)
            isTitleEnabled = true
        }

        toolbar.apply {
            setBackgroundColor(Color.TRANSPARENT)
            background = null
            elevation = 0f
        }

        tabLayout.apply {
            setBackgroundColor(Color.TRANSPARENT)
            background = null
            elevation = 0f
            stateListAnimator = null
        }



        // Load custom attributes
        context.theme.obtainStyledAttributes(attrs, R.styleable.CoordinatorTabLayout, 0, 0).apply {
            try {
                // Tab indicator customization
                val indicatorColor = getColor(R.styleable.CoordinatorTabLayout_tabIndicatorColor, Color.WHITE)
                val indicatorHeight = getDimensionPixelSize(R.styleable.CoordinatorTabLayout_tabIndicatorHeight, 4)

                tabLayout.setSelectedTabIndicatorColor(indicatorColor)
                tabLayout.setSelectedTabIndicatorHeight(indicatorHeight)

                // Tab text colors
                val tabTextColor = getColor(R.styleable.CoordinatorTabLayout_tabTextColor, Color.WHITE)
                val tabSelectedTextColor = getColor(R.styleable.CoordinatorTabLayout_tabSelectedTextColor, Color.WHITE)
                tabLayout.setTabTextColors(tabTextColor, tabSelectedTextColor)

                // Tab text size
                val tabTextSize = getDimensionPixelSize(R.styleable.CoordinatorTabLayout_tabTextSize, -1)
                if (tabTextSize > 0) {
                    val tabTextSizeSp = tabTextSize / resources.displayMetrics.scaledDensity
                    // Applied via tabTextAppearance style
                }

                // Content background color
                val contentBackgroundColor = getColor(R.styleable.CoordinatorTabLayout_contentBackgroundColor, Color.WHITE)
//                contentContainer.setBackgroundColor(contentBackgroundColor)

                // Content scrim color
                val contentScrimColor = getColor(R.styleable.CoordinatorTabLayout_contentScrimColor,
                    ContextCompat.getColor(context, android.R.color.transparent))
                collapsingToolbar.setContentScrimColor(contentScrimColor)

                // Expanded title color
                val expandedTitleColor = getColor(R.styleable.CoordinatorTabLayout_expandedTitleColor, Color.WHITE)
                collapsingToolbar.setExpandedTitleColor(expandedTitleColor)

                // Collapsed title color
                val collapsedTitleColor = getColor(R.styleable.CoordinatorTabLayout_collapsedTitleColor, Color.WHITE)
                collapsingToolbar.setCollapsedTitleTextColor(collapsedTitleColor)

                // Toolbar height
                val toolbarHeight = getDimensionPixelSize(R.styleable.CoordinatorTabLayout_toolbarHeight, -1)
                if (toolbarHeight > 0) {
                    val params = collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
                    params.height = toolbarHeight
                    collapsingToolbar.layoutParams = params
                }

            } finally {
                recycle()
            }
        }

        setupViewPagerListener()
    }

    private fun setupViewPagerListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                updateHeaderForPosition(position)
                onTabSelectedListener?.onTabSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    updateHeaderForPosition(position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateHeaderForPosition(position: Int) {
        // Update image
        imageArray?.let {
            if (position < it.size) {
                val imageRes = it[position]
                onImageLoadListener?.onLoadImage(headerImageView, imageRes)
                    ?: headerImageView.setImageResource(imageRes)

                headerImageView.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction {
                        headerImageView.setImageResource(imageRes)
                        headerImageView.animate()
                            .alpha(1f)
                            .setDuration(150)
                            .start()
                    }
                    .start()
            }
        }

        // Update scrim color
        colorArray?.let {
            if (position < it.size) {
                collapsingToolbar.setContentScrimColor(it[position])
            }
        }
    }


    /**
     * Setup the ViewPager with an adapter
     */
    fun setupWithViewPager(adapter: androidx.viewpager.widget.PagerAdapter): CoordinatorTabLayout {
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        return this
    }

    /**
     * Get the Toolbar instance
     */
    fun getToolbar(): androidx.appcompat.widget.Toolbar = toolbar

    /**
     * Set header images for each tab
     */
    fun setImageArray(images: IntArray): CoordinatorTabLayout {
        this.imageArray = images
        if (images.isNotEmpty()) {
            updateHeaderForPosition(0)
        }
        return this
    }

    /**
     * Set content scrim colors for each tab
     */
    fun setColorArray(colors: IntArray): CoordinatorTabLayout {
        this.colorArray = colors
        if (colors.isNotEmpty()) {
            updateHeaderForPosition(0)
        }
        return this
    }

    /**
     * Set tab indicator color
     */
    fun setTabIndicatorColor(color: Int): CoordinatorTabLayout {
        tabLayout.setSelectedTabIndicatorColor(color)
        return this
    }

    /**
     * Set tab indicator height
     */
    fun setTabIndicatorHeight(height: Int): CoordinatorTabLayout {
        tabLayout.setSelectedTabIndicatorHeight(height)
        return this
    }

    /**
     * Set tab text colors
     */
    fun setTabTextColors(normalColor: Int, selectedColor: Int): CoordinatorTabLayout {
        tabLayout.setTabTextColors(normalColor, selectedColor)
        return this
    }

    /**
     * Set tab text size
     */
    fun setTabTextSize(sizeSp: Float): CoordinatorTabLayout {
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                applyTabTextSize(sizeSp)
            }
        })
        // Apply immediately if tabs already exist
        post {
            applyTabTextSize(sizeSp)
        }
        return this
    }

    private fun applyTabTextSize(sizeSp: Float) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val tabView = tab?.view
            tabView?.let { view ->
                // Find TextView in the tab view hierarchy
                findTextView(view)?.textSize = sizeSp
            }
        }
    }

    private fun findTextView(view: View): android.widget.TextView? {
        if (view is android.widget.TextView) {
            return view
        }
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val textView = findTextView(view.getChildAt(i))
                if (textView != null) {
                    return textView
                }
            }
        }
        return null
    }

    /**
     * Set tab padding
     */
    fun setTabPadding(paddingDp: Int): CoordinatorTabLayout {
        val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()
        post {
            for (i in 0 until tabLayout.tabCount) {
                val tab = tabLayout.getTabAt(i)
                val tabView = tab?.view
                tabView?.setPadding(paddingPx, tabView.paddingTop, paddingPx, tabView.paddingBottom)
            }
        }
        return this
    }

    /**
     * Set content scrim color
     */
    fun setContentScrimColor(color: Int): CoordinatorTabLayout {
        collapsingToolbar.setContentScrimColor(color)
        return this
    }

    /**
     * Set title
     */
    fun setTitle(title: String): CoordinatorTabLayout {
        collapsingToolbar.title = title
        return this
    }

    /**
     * Set expanded title color
     */
    fun setExpandedTitleColor(color: Int): CoordinatorTabLayout {
        collapsingToolbar.setExpandedTitleColor(color)
        return this
    }

    /**
     * Set collapsed title color
     */
    fun setCollapsedTitleColor(color: Int): CoordinatorTabLayout {
        collapsingToolbar.setCollapsedTitleTextColor(color)
        return this
    }

    /**
     * Set image load listener for custom image loading (e.g., Glide, Picasso)
     */
    fun setOnImageLoadListener(listener: OnImageLoadListener): CoordinatorTabLayout {
        this.onImageLoadListener = listener
        return this
    }

    /**
     * Set tab selection listener
     */
    fun setOnTabSelectedListener(listener: OnTabSelectedListener): CoordinatorTabLayout {
        this.onTabSelectedListener = listener
        return this
    }

    /**
     * Get the ViewPager instance
     */
    fun getViewPager(): ViewPager = viewPager

    /**
     * Get the TabLayout instance
     */
    fun getTabLayout(): TabLayout = tabLayout

    /**
     * Get the CollapsingToolbarLayout instance
     */
    fun getCollapsingToolbar(): CollapsingToolbarLayout = collapsingToolbar

    /**
     * Get the header ImageView
     */
    fun getHeaderImageView(): ImageView = headerImageView

    /**
     * Get the AppBarLayout instance
     */
    fun getAppBarLayout(): AppBarLayout = appBarLayout

    /**
     * Set current tab
     */
    fun setCurrentTab(position: Int, smoothScroll: Boolean = true) {
        viewPager.setCurrentItem(position, smoothScroll)
    }

    /**
     * Interface for custom image loading
     */
    interface OnImageLoadListener {
        fun onLoadImage(imageView: ImageView, imageRes: Int)
    }

    /**
     * Interface for tab selection events
     */
    interface OnTabSelectedListener {
        fun onTabSelected(position: Int)
    }
}