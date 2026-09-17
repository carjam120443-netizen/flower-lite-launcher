package com.example.flowerlitelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var appSearch: EditText
    private lateinit var appRecyclerView: RecyclerView
    private lateinit var appAdapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appSearch = findViewById(R.id.appSearch)
        appRecyclerView = findViewById(R.id.appRecyclerView)

        val apps = loadApps()
        appAdapter = AppAdapter(apps) { launchApp(it) }
        appRecyclerView.layoutManager = GridLayoutManager(this, 4)
        appRecyclerView.adapter = appAdapter

        appSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                appAdapter.filter(query)
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun loadApps(): List<AppInfo> {
        val packageManager = packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo ->
                packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
            }
            .map { appInfo ->
                val label = packageManager.getApplicationLabel(appInfo).toString()
                val icon = packageManager.getApplicationIcon(appInfo)
                AppInfo(appInfo.packageName, label, icon)
            }
            .sortedBy { it.label.lowercase() }

        return apps
    }

    private fun launchApp(appInfo: AppInfo) {
        val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        }
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

    fun filter(query: String) {
        filteredApps = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.label.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_item, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = filteredApps[position]
        holder.bind(app, onAppClick)
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
