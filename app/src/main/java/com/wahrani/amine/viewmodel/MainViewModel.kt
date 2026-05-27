package com.wahrani.amine.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahrani.amine.model.Channel
import com.wahrani.amine.parser.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var channels by mutableStateOf<List<Channel>>(emptyList())
        private set

    var filteredChannels by mutableStateOf<List<Channel>>(emptyList())
        private set

    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedCategory by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var favoriteUrls by mutableStateOf<Set<String>>(emptySet())
        private set

    var playlistUrl by mutableStateOf("")
        private set

    private val prefs = application.getSharedPreferences("wahrani_dev_pro", Context.MODE_PRIVATE)
    private val defaultUrl = "http://atlan2025.me:80/get.php?username=Rochdi70sam&password=d3hm7lsqrh&type=m3u_plus"

    init {
        playlistUrl = prefs.getString("playlist_url", defaultUrl) ?: defaultUrl
        loadPlaylist()
    }

    fun updatePlaylistUrl(newUrl: String) {
        playlistUrl = newUrl
        prefs.edit().putString("playlist_url", newUrl).apply()
        loadPlaylist()
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val content = withContext(Dispatchers.IO) {
                    if (playlistUrl.startsWith("http")) {
                        downloadUrl(playlistUrl)
                    } else {
                        getApplication<Application>().assets.open("playlist.m3u")
                            .bufferedReader()
                            .use { it.readText() }
                    }
                }
                val parsed = M3UParser.parse(content)
                channels = parsed
                categories = parsed.map { it.groupTitle }.distinct().sorted()
                applyFilters()
                if (parsed.isEmpty()) {
                    errorMessage = "No channels found in playlist"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load playlist"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun downloadUrl(urlString: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 30000
        conn.instanceFollowRedirects = true
        conn.setRequestProperty("User-Agent", "VLC/3.0.20 LibVLC/3.0.20")
        return conn.inputStream.bufferedReader().use { it.readText() }
    }

    fun selectCategory(category: String?) {
        selectedCategory = if (category == selectedCategory) null else category
        applyFilters()
    }

    fun updateSearch(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun toggleFavorite(channel: Channel) {
        favoriteUrls = if (channel.url in favoriteUrls) {
            favoriteUrls - channel.url
        } else {
            favoriteUrls + channel.url
        }
    }

    private fun applyFilters() {
        filteredChannels = channels.filter { channel ->
            val matchesCategory = selectedCategory == null ||
                    channel.groupTitle == selectedCategory ||
                    (selectedCategory == "Favorites" && channel.url in favoriteUrls)

            val matchesSearch = searchQuery.isBlank() ||
                    channel.name.contains(searchQuery, ignoreCase = true) ||
                    channel.groupTitle.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }
}
