package com.ext.coordinatortablayout

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.coordinator_tablayout.CoordinatorTabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var coordinatorTabLayout: CoordinatorTabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        coordinatorTabLayout = findViewById(R.id.coordinatorTabLayout)

        // Setup ViewPager with adapter
        val titles = arrayOf("Home", "Photos", "Videos", "Music", "Settings")
        val adapter = SamplePagerAdapter(
            supportFragmentManager,
            titles
        )

        // Define header images for each tab
        val images = intArrayOf(
            R.drawable.sample_image_1,
            R.drawable.sample_image_2,
            R.drawable.sample_image_1,
            R.drawable.sample_image_2,
            R.drawable.sample_image_2
        )

        // Define scrim colors for each tab
        val colors = intArrayOf(
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FF5722"), // Red
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#FF9800")  // Orange
        )
        setSupportActionBar(coordinatorTabLayout.getToolbar())

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

// Let CollapsingToolbarLayout handle the title
        coordinatorTabLayout.setTitle("Demo")


        // Configure the CoordinatorTabLayout with fluent API
        coordinatorTabLayout
            .setupWithViewPager(adapter)
            .setImageArray(images)
            .setColorArray(colors)
            .setTitle("Demo")
            .setTabIndicatorColor(Color.WHITE)
            .setTabIndicatorHeight(resources.getDimensionPixelSize(R.dimen.tab_indicator_height))
            .setTabTextColors(Color.WHITE, Color.WHITE)
            .setExpandedTitleColor(Color.WHITE)
            .setCollapsedTitleColor(Color.WHITE)
            .setTabTextSize(14f)
            .setOnImageLoadListener(object : CoordinatorTabLayout.OnImageLoadListener {
                override fun onLoadImage(imageView: ImageView, imageRes: Int) {
                    // Use this for custom image loading with Glide, Picasso, etc.
                    // Example with Glide:
                    // Glide.with(this@MainActivity).load(imageRes).into(imageView)

                    // For now, using default ImageView loading
                    imageView.setImageResource(imageRes)
                }
            })
            .setOnTabSelectedListener(object : CoordinatorTabLayout.OnTabSelectedListener {
                override fun onTabSelected(position: Int) {
                }
            })

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(coordinatorTabLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            coordinatorTabLayout.getAppBarLayout().setPadding(0, systemBars.top, 0, 0)
            insets
        }
    }
}

