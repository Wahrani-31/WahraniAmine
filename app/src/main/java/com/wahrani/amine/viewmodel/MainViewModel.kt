package com.wahrani.amine.viewmodel

import android.app.Application
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

    var favoriteUrls by mutableStateOf<Set<String>>(emptySet())
        private set

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            isLoading = true
            try {
                val content = withContext(Dispatchers.IO) {
                    getApplication<Application>().assets.open("playlist.m3u")
                        .bufferedReader()
                        .use { it.readText() }
                }
                val parsed = M3UParser.parse(content)
                channels = parsed
                categories = parsed.map { it.groupTitle }.distinct().sorted()
                applyFilters()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
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
