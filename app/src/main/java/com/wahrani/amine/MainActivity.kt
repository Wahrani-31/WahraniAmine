package com.wahrani.amine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wahrani.amine.model.Channel
import com.wahrani.amine.ui.screens.HomeScreen
import com.wahrani.amine.ui.screens.PlayerScreen
import com.wahrani.amine.ui.theme.WahraniAmineTheme
import com.wahrani.amine.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WahraniAmineTheme {
                val viewModel: MainViewModel = viewModel()
                var currentChannel by remember { mutableStateOf<Channel?>(null) }

                if (currentChannel == null) {
                    HomeScreen(
                        viewModel = viewModel,
                        onChannelClick = { channel ->
                            currentChannel = channel
                        }
                    )
                } else {
                    PlayerScreen(
                        channel = currentChannel!!,
                        channels = viewModel.channels,
                        onBack = { currentChannel = null },
                        onChannelSelected = { channel ->
                            currentChannel = channel
                        }
                    )
                }
            }
        }
    }
}
