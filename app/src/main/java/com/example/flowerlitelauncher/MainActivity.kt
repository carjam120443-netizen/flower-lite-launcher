package com.example.flowerlitelauncher

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appSearch: EditText
    private lateinit var appRecyclerView: RecyclerView
    private lateinit var appAdapter: AppAdapter
    private lateinit var greetingText: TextView
    private lateinit var timeText: TextView
    private lateinit var folderSummary: TextView
    private lateinit var wallpaperPreview: LinearLayout
    private lateinit var contentRoot: View
    private lateinit var allAppsButton: MaterialButton
    private lateinit var favoritesButton: MaterialButton
    private lateinit var productivityButton: MaterialButton
    private lateinit var gestureDetector: GestureDetector

    private enum class AppMode { ALL, FAVORITES, PRODUCTIVITY }
    private var activeMode: AppMode = AppMode.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        appSearch = findViewById(R.id.appSearch)
        appRecyclerView = findViewById(R.id.appRecyclerView)
        greetingText = findViewById(R.id.greetingText)
        timeText = findViewById(R.id.timeText)
        folderSummary = findViewById(R.id.folderSummary)
        wallpaperPreview = findViewById(R.id.wallpaperPreview)
        contentRoot = findViewById(R.id.contentRoot)
        allAppsButton = findViewById(R.id.allAppsButton)
        favoritesButton = findViewById(R.id.favoritesButton)
        productivityButton = findViewById(R.id.productivityButton)

        val apps = loadApps()
        appAdapter = AppAdapter(apps) { launchApp(it) }
        appRecyclerView.layoutManager = GridLayoutManager(this, 4)
        appRecyclerView.adapter = appAdapter
        appRecyclerView.setHasFixedSize(true)

        setupGestureDetector()
        setupModeButtons()
        applyModeFilter()
        updateClock()

        wallpaperPreview.setOnClickListener {
            val nextBackground = when (contentRoot.background.constantState?.equals(getDrawable(R.drawable.wallpaper_night)?.constantState)) {
                true -> R.drawable.wallpaper_orchid
                false -> when (contentRoot.background.constantState?.equals(getDrawable(R.drawable.wallpaper_orchid)?.constantState)) {
                    true -> R.drawable.wallpaper_sunset
                    else -> R.drawable.wallpaper_night
                }
                else -> R.drawable.wallpaper_night
            }
            contentRoot.setBackgroundResource(nextBackground)
        }

        appSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                appAdapter.filter(query, activeMode)
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun setupModeButtons() {
        allAppsButton.setOnClickListener { setMode(AppMode.ALL) }
        favoritesButton.setOnClickListener { setMode(AppMode.FAVORITES) }
        productivityButton.setOnClickListener { setMode(AppMode.PRODUCTIVITY) }
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val deltaX = e2.x - e1.x
                if (kotlin.math.abs(deltaX) > 150) {
                    val nextMode = when (activeMode) {
                        AppMode.ALL -> AppMode.FAVORITES
                        AppMode.FAVORITES -> AppMode.PRODUCTIVITY
                        AppMode.PRODUCTIVITY -> AppMode.ALL
                    }
                    if (deltaX < 0) setMode(nextMode) else setMode(
                        when (activeMode) {
                            AppMode.ALL -> AppMode.PRODUCTIVITY
                            AppMode.FAVORITES -> AppMode.ALL
                            AppMode.PRODUCTIVITY -> AppMode.FAVORITES
                        }
                    )
                    return true
                }
                return false
            }
        })

        contentRoot.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    private fun setMode(mode: AppMode) {
        activeMode = mode
        allAppsButton.isChecked = mode == AppMode.ALL
        favoritesButton.isChecked = mode == AppMode.FAVORITES
        productivityButton.isChecked = mode == AppMode.PRODUCTIVITY
        applyModeFilter()
    }

    private fun applyModeFilter() {
        val query = appSearch.text?.toString()?.trim() ?: ""
        appAdapter.filter(query, activeMode)

        val modeText = when (activeMode) {
            AppMode.ALL -> "All apps • ${appAdapter.itemCount} items"
            AppMode.FAVORITES -> "Favorites • quick launch"
            AppMode.PRODUCTIVITY -> "Productivity • work mode"
        }
        folderSummary.text = modeText
    }

    private fun updateClock() {
        val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        val hour = Date().hours
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }

        greetingText.text = greeting
        timeText.text = timeFormat.format(Date())
    }

    private fun loadApps(): List<AppInfo> {
        val packageManager = packageManager
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo -> packageManager.getLaunchIntentForPackage(appInfo.packageName) != null }
            .map { appInfo ->
                val label = packageManager.getApplicationLabel(appInfo).toString()
                val icon = packageManager.getApplicationIcon(appInfo)
                AppInfo(appInfo.packageName, label, icon)
            }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    private fun launchApp(appInfo: AppInfo) {
        val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
        launchIntent?.let { startActivity(it) }
    }
}

private data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable
)

private class AppAdapter(
    private val allApps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private var filteredApps: List<AppInfo> = allApps
    private var activeMode: MainActivity.AppMode = MainActivity.AppMode.ALL

    fun filter(query: String, mode: MainActivity.AppMode) {
        activeMode = mode
        filteredApps = when (mode) {
            MainActivity.AppMode.ALL -> allApps
            MainActivity.AppMode.FAVORITES -> allApps.take(6)
            MainActivity.AppMode.PRODUCTIVITY -> allApps.filter { it.label.lowercase(Locale.getDefault()).contains("mail") || it.label.lowercase(Locale.getDefault()).contains("camera") || it.label.lowercase(Locale.getDefault()).contains("browser") || it.label.lowercase(Locale.getDefault()).contains("calendar") }
        }

        if (query.isNotEmpty()) {
            filteredApps = filteredApps.filter { it.label.contains(query, ignoreCase = true) }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(filteredApps[position], onAppClick)
    }

    override fun getItemCount(): Int = filteredApps.size

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appLabel: TextView = itemView.findViewById(R.id.appLabel)

        fun bind(app: AppInfo, onClick: (AppInfo) -> Unit) {
            appIcon.setImageDrawable(app.icon)
            appLabel.text = app.label
            itemView.setOnClickListener { onClick(app) }
        }
    }
}
