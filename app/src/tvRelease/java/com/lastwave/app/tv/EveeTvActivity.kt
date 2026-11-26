package com.lastwave.app.tv

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lastwave.app.playback.AndroidAutoMediaLibrary
import com.lastwave.app.playback.MusicPlaybackService
import com.lastwave.app.playback.MusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EveeTvActivity : ComponentActivity() {
    @Inject lateinit var musicPlayer: MusicPlayer
    @Inject lateinit var mediaLibrary: AndroidAutoMediaLibrary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = android.media.AudioManager.STREAM_MUSIC
        setContent {
            val state by musicPlayer.state.collectAsStateWithLifecycle()
            EveeTvTheme {
                EveeTvHome(
                    state = state,
                    mediaLibrary = mediaLibrary,
                    onPlayItem = { item ->
                        lifecycleScope.launch {
                            if (mediaLibrary.playMediaId(item.description.mediaId.orEmpty(), musicPlayer)) {
                                startPlaybackService()
                            }
                        }
                    },
                    onPrevious = { musicPlayer.previous() },
                    onPlayPause = { musicPlayer.togglePlayPause() },
                    onNext = { musicPlayer.next() },
                    onStartPlaybackService = ::startPlaybackService,
                )
            }
        }
    }

    private fun startPlaybackService() {
        runCatching {
            ContextCompat.startForegroundService(
                this,
                Intent(this, MusicPlaybackService::class.java),
            )
        }.onFailure {
            android.util.Log.w("EveeTvActivity", "Could not start playback service", it)
        }
    }
}

@Composable
private fun EveeTvTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Color(0xFFB78BFF),
        onPrimary = Color(0xFF170D25),
        background = Color(0xFF08060D),
        onBackground = Color(0xFFF5F0FF),
        surface = Color(0xFF17121F),
        onSurface = Color(0xFFF5F0FF),
        secondary = Color(0xFF8E65D6),
    )
    MaterialTheme(colorScheme = colors, content = content)
}

@Composable
private fun EveeTvHome(
    state: com.lastwave.app.playback.MusicPlayerState,
    mediaLibrary: AndroidAutoMediaLibrary,
    onPlayItem: (MediaBrowserCompat.MediaItem) -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onStartPlaybackService: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var parentId by remember { mutableStateOf(AndroidAutoMediaLibrary.ROOT_ID) }
    var parentTitle by remember { mutableStateOf("Your music") }
    var searchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<MediaBrowserCompat.MediaItem>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(parentId, state.current, state.queue, searchMode) {
        if (!searchMode) {
            loading = true
            error = null
            runCatching { mediaLibrary.loadChildren(parentId, state) }
                .onSuccess { items = it }
                .onFailure { error = "Could not load this section. Try again." }
            loading = false
        }
    }

    BackHandler(enabled = searchMode || parentId != AndroidAutoMediaLibrary.ROOT_ID) {
        if (searchMode) {
            searchMode = false
            parentId = AndroidAutoMediaLibrary.ROOT_ID
            parentTitle = "Your music"
        } else {
            parentId = AndroidAutoMediaLibrary.ROOT_ID
            parentTitle = "Your music"
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 52.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        "EVEE TUNE",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                    )
                    Text(
                        "ANDROID TV EDITION",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                    )
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    searchMode = false
                    parentId = AndroidAutoMediaLibrary.ROOT_ID
                    parentTitle = "Your music"
                }) { Text("HOME") }
                Spacer(Modifier.width(10.dp))
                Button(onClick = {
                    searchMode = false
                    parentId = AndroidAutoMediaLibrary.QUEUE_ID
                    parentTitle = "Up next"
                }) { Text("UP NEXT") }
            }

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1.05f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        if (searchMode) "SEARCH RESULTS" else parentTitle.uppercase(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("Search songs or playlists") },
                        )
                        Button(
                            enabled = searchQuery.isNotBlank(),
                            onClick = {
                                scope.launch {
                                    loading = true
                                    error = null
                                    runCatching { mediaLibrary.search(searchQuery) }
                                        .onSuccess {
                                            items = it
                                            searchMode = true
                                            parentTitle = "Search results"
                                        }
                                        .onFailure { error = "Search failed. Please try again." }
                                    loading = false
                                }
                            },
                        ) { Text("SEARCH") }
                    }

                    when {
                        loading -> Text("Loading…", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        error != null -> Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
                        items.isEmpty() -> Text(
                            if (searchMode) "No matching music found." else "Nothing here yet.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                        else -> LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(items, key = { it.description.mediaId.orEmpty() }) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Button(
                                        modifier = Modifier.weight(1f).height(62.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                        ),
                                        onClick = {
                                            if (item.isBrowsable) {
                                                parentId = item.description.mediaId.orEmpty()
                                                parentTitle = item.description.title?.toString().orEmpty()
                                                searchMode = false
                                            } else if (item.isPlayable) {
                                                onPlayItem(item)
                                                onStartPlaybackService()
                                            }
                                        },
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.Start,
                                        ) {
                                            Text(
                                                item.description.title?.toString().orEmpty(),
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                            )
                                            Text(
                                                item.description.subtitle?.toString().orEmpty(),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                    if (item.isPlayable && item.isBrowsable) {
                                        Button(
                                            modifier = Modifier.height(62.dp),
                                            onClick = {
                                                onPlayItem(item)
                                                onStartPlaybackService()
                                            },
                                        ) { Text("PLAY") }
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(0.85f).background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(24.dp),
                    ).padding(26.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "NOW PLAYING",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        state.current?.title ?: "Nothing playing",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.current?.artist ?: "Choose a track from your library",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                        maxLines = 2,
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(onClick = onPrevious) { Text("PREV") }
                        Button(
                            onClick = {
                                onPlayPause()
                                onStartPlaybackService()
                            },
                            enabled = state.current != null,
                        ) { Text(if (state.isPlaying) "PAUSE" else "PLAY") }
                        Button(onClick = onNext, enabled = state.queue.size > 1) { Text("NEXT") }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        if (state.isBuffering) "BUFFERING" else if (state.isPlaying) "PLAYING" else "PAUSED",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                    )
                }
            }
            Text(
                "Use your TV remote to browse playlists, downloads and your queue.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 12.sp,
            )
        }
    }
}
