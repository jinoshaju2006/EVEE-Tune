package com.lastwave.app.ui.settings

import androidx.compose.material3.HorizontalDivider

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Vibration
import com.lastwave.app.data.local.CELLULAR_QUALITY_AUTO
import com.lastwave.app.data.local.musicHapticsConfig
import com.lastwave.app.playback.haptics.MusicHapticsConfig
import com.lastwave.app.playback.haptics.MusicHapticsEngineMode
import com.lastwave.app.playback.haptics.MusicHapticsIntensity
import com.lastwave.app.playback.haptics.MusicHapticsMode
import com.lastwave.app.ui.player.QualityGlyphs
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.FolderOpen
import com.lastwave.app.ui.jam.LastWaveJamSheet
import com.lastwave.app.ui.jam.LastWaveJamViewModel
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import com.lastwave.app.ui.common.HeaderActionIcon
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.SignalCellularAlt
import com.lastwave.app.util.BatteryOptimizationHelper
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterTiltShift
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Waves
import com.lastwave.app.data.local.LyricsProvider
import com.lastwave.app.data.local.RecommendationSource
import com.lastwave.app.ui.player.LyricsOffsetDialog
import com.lastwave.app.ui.player.LyricsSizeDialog
import androidx.compose.material.icons.filled.FormatSize
import kotlin.math.roundToInt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.SpeakerPhone
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.draw.blur
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.lastwave.app.ui.common.GlassHeaderLayout
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.lastwave.app.data.local.AppLanguage
import com.lastwave.app.data.local.nativeDisplayName
import com.lastwave.app.data.lyrics.TRANSLATION_LANGUAGES
import com.lastwave.app.data.lyrics.translationLanguageName
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import java.util.Locale

import com.lastwave.app.ui.player.LocalMiniPlayerScrollClearance
import com.lastwave.app.R
import com.lastwave.app.data.local.AccentMode
import com.lastwave.app.data.local.ThemeMode
import com.lastwave.app.data.local.AppIcon
import com.lastwave.app.data.local.EQ_BAND_FREQS_HZ
import com.lastwave.app.data.local.EQ_MAX_GAIN_DB
import com.lastwave.app.data.local.EqualizerPresets
import com.lastwave.app.data.local.EqualizerSettings
import com.lastwave.app.playback.ClarityPresets
import com.lastwave.app.playback.LoudnessMode
import com.lastwave.app.data.local.eqBandLabel
import com.lastwave.app.ui.common.ConnectedButtonGroup
import com.lastwave.app.ui.common.ConnectedButtonItem
import com.lastwave.app.ui.common.ExpressiveHeader
import com.lastwave.app.ui.common.safeDrawingBottomPadding
import com.lastwave.app.ui.common.safeHorizontalContentPadding
import com.lastwave.app.ui.common.adaptiveContentWidth
import com.lastwave.app.ui.theme.ExpressivePillShape
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import kotlin.math.roundToInt
import kotlin.math.roundToLong



/**
 * Faithful port of settings.js (par 8): Last.fm account management, appearance
 * (AMOLED / Dynamic Color / Monochrome / accent presets / custom color
 * wheel), iTunes/ListenBrainz artwork toggles, data management (clear
 * recommendation exclusions, clear all data), backup & restore, and app info.
 *
 * Visuals only: restyled into a Material 3 Expressive presentation
 * (larger touch targets, per-row cards, tonal icon badges, spring-based
 * press feedback). Every setting, callback, and piece of state below is
 * unchanged from the original implementation.
 */

enum class SettingsTab(
    @androidx.annotation.StringRes val titleRes: Int,
    @androidx.annotation.StringRes val subtitleRes: Int,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    AUDIO(
        titleRes = R.string.set_cat_audio,
        subtitleRes = R.string.set_cat_audio_sub,
        title = "Audio",
        subtitle = "Streaming quality, Audio engine, Equalizer, Output & Loudness",
        icon = Icons.Filled.GraphicEq,
    ),
    APPEARANCE(
        titleRes = R.string.set_cat_appear,
        subtitleRes = R.string.set_cat_appear_sub,
        title = "Appearance",
        subtitle = "Themes, Colors, Artwork, Lyrics",
        icon = Icons.Filled.Palette,
    ),
    YOUTUBE(
        titleRes = R.string.set_cat_yt,
        subtitleRes = R.string.set_cat_yt_sub,
        title = "YouTube",
        subtitle = "Account connection, 24/7 Library sync, Channels, History",
        icon = Icons.Filled.CloudSync,
    ),
    SPOTIFY(
        titleRes = R.string.set_cat_spotify,
        subtitleRes = R.string.set_cat_spotify_sub,
        title = "Spotify",
        subtitle = "Account connection, Canvas, Playlist imports & Liked Songs sync",
        icon = Icons.Filled.PlayCircle,
    ),
    LAST_FM(
        titleRes = R.string.set_cat_lfm,
        subtitleRes = R.string.set_cat_lfm_sub,
        title = "Last.fm",
        subtitle = "Account connection, Scrobbling sync & API credentials",
        icon = Icons.Filled.Album,
    ),
    LIBRARY(
        titleRes = R.string.set_cat_lib,
        subtitleRes = R.string.set_cat_lib_sub,
        title = "Library",
        subtitle = "Home layout, Playlist imports, Downloads, Exclusions",
        icon = Icons.Filled.QueueMusic,
    ),
    DATA_BACKUP(
        titleRes = R.string.set_cat_data,
        subtitleRes = R.string.set_cat_data_sub,
        title = "Data",
        subtitle = "Cache cleanup, Export & Import settings, Database tools",
        icon = Icons.Filled.Backup,
    ),
    ABOUT(
        titleRes = R.string.set_cat_about,
        subtitleRes = R.string.set_cat_about_sub,
        title = "About",
        subtitle = "Version info, Telegram community, GitHub repository",
        icon = Icons.Filled.Info,
    ),
}

private fun streamingQualityLabel(quality: Int): String = when (quality) {
    CELLULAR_QUALITY_AUTO -> "Auto"
    28 -> "Dolby Atmos"
    27, 7 -> "Hi-Res Lossless"
    6, 5 -> "Standard (320 kbps)"
    4 -> "Data Saver"
    -1 -> "YouTube Music"
    else -> "Hi-Res Lossless"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    onOpenDownloads: () -> Unit = {},
    onOpenStorage: () -> Unit = {},
    onOpenModules: () -> Unit = {},
    onOpenHomeSections: () -> Unit = {},
    onOpenExcludedSongs: () -> Unit = {},
    onOpenYouTubeImport: () -> Unit = {},
    onOpenYouTubeLogin: () -> Unit = {},
    onOpenExternalImport: () -> Unit = {},
    onOpenSpotifyLogin: () -> Unit = {},
    onOpenSpotifyTokenImport: () -> Unit = {},
    onOpenJam: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    jamViewModel: LastWaveJamViewModel = hiltViewModel(),
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val jamUiState by jamViewModel.uiState.collectAsStateWithLifecycle()
    val avatarUrl by viewModel.avatarUrl.collectAsStateWithLifecycle()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val misc by viewModel.misc.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val downloadCount by viewModel.downloadCount.collectAsStateWithLifecycle()
    val downloadTotalBytes by viewModel.downloadTotalBytes.collectAsStateWithLifecycle()
    val ytConnection by viewModel.ytConnection.collectAsStateWithLifecycle()
    val ytSyncEnabled by viewModel.ytSyncEnabled.collectAsStateWithLifecycle()
    val ytHistorySyncEnabled by viewModel.ytHistorySyncEnabled.collectAsStateWithLifecycle()
    val ytSyncState by viewModel.ytSyncState.collectAsStateWithLifecycle()
    val ytLastSyncAt by viewModel.ytLastSyncAt.collectAsStateWithLifecycle()
    val syncedPlaylistIds by viewModel.syncedPlaylistIds.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val ytAccountPlaylists by viewModel.ytAccountPlaylists.collectAsStateWithLifecycle()
    val hiddenYtLibraryPlaylistIds by viewModel.hiddenYtLibraryPlaylistIds.collectAsStateWithLifecycle()
    val ytChannels by viewModel.ytChannels.collectAsStateWithLifecycle()
    val ytChannelsLoading by viewModel.ytChannelsLoading.collectAsStateWithLifecycle()
    val spotifyConnected by viewModel.spotifyConnected.collectAsStateWithLifecycle()
    val eq by viewModel.equalizer.collectAsStateWithLifecycle()
    val usbExclusiveEnabled by viewModel.usbExclusiveEnabled.collectAsStateWithLifecycle()
    val internalDirectEnabled by viewModel.internalDirectEnabled.collectAsStateWithLifecycle()
    val forceSoftwareVolume by viewModel.forceSoftwareVolume.collectAsStateWithLifecycle()
    val internalDirectAvailability by viewModel.internalDirectAvailability.collectAsStateWithLifecycle()
    val loudness by viewModel.loudness.collectAsStateWithLifecycle()
    val discordPresenceEnabled by viewModel.discordPresenceEnabled.collectAsStateWithLifecycle()
    val isLastFmConnected by viewModel.isLastFmConnected.collectAsStateWithLifecycle()
    val hasApiKey by viewModel.hasApiKey.collectAsStateWithLifecycle()
    val lastFmAuthUrl by viewModel.lastFmAuthUrl.collectAsStateWithLifecycle()
    val lastFmConnecting by viewModel.lastFmConnecting.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Last.fm web auth (Settings → Integrations): open the auth URL in Custom
    // Tabs the moment SettingsViewModel produces one.
    androidx.compose.runtime.LaunchedEffect(lastFmAuthUrl) {
        lastFmAuthUrl?.let { url ->
            runCatching {
                androidx.browser.customtabs.CustomTabsIntent.Builder().build()
                    .launchUrl(context, android.net.Uri.parse(url))
            }.onFailure {
                viewModel.showToast(context.getString(R.string.toast_no_browser_lfm))
                viewModel.cancelLastFmConnect()
            }
        }
    }
    var showQualityDialog by remember { mutableStateOf(false) }
    var editingCellularQuality by remember { mutableStateOf(false) }
    var showDownloadQualityDialog by remember { mutableStateOf(false) }
    var showEqSheet by remember { mutableStateOf(false) }
    var showAutoEq by remember { mutableStateOf(false) }
    var showLyricsProviderDialog by remember { mutableStateOf(false) }
    var showLyricsOffsetDialog by remember { mutableStateOf(false) }
    var showLyricsSizeDialog by remember { mutableStateOf(false) }
    var showLyricsAutoHideDialog by remember { mutableStateOf(false) }
    var showTranslationLanguageDialog by remember { mutableStateOf(false) }
    var showLoudnessDialog by remember { mutableStateOf(false) }
    var showClarityPresetDialog by remember { mutableStateOf(false) }
    var showSyncPlaylistsSheet by remember { mutableStateOf(false) }
    var showYtLibraryVisibilitySheet by remember { mutableStateOf(false) }
    var showYtChannelSheet by remember { mutableStateOf(false) }
    var showYtDisconnectConfirm by remember { mutableStateOf(false) }
    var showSpotifyDisconnectConfirm by remember { mutableStateOf(false) }
    var showRecommendationSourceDialog by remember { mutableStateOf(false) }
    var showCookieLoginDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showJamSheet by remember { mutableStateOf(false) }
    var activeToggleDetail by remember { mutableStateOf<ToggleDetailInfo?>(null) }
    val currentLanguage = remember(misc.appLanguageTag) { AppLanguage.fromTag(misc.appLanguageTag) }

    // Sends the user to Android's own Notification Listener access screen
    // — the one permission this feature needs that the app can never grant

    // "*/*" rather than "application/json": many document providers (Drive,
    // Downloads, some file managers) report a .json file as
    // application/octet-stream or text/plain, and GetContent's mime filter
    // hides anything that doesn't match — the user's own backup file would
    // silently not show up. Real validation happens right after the file is
    // read (stagePendingRestore), so being permissive here is safe.
    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.handleRestorePicked(uri)
        }
    }

    val csvPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.handleCsvPicked(uri)
        }
    }

    var showYouTubeImportSheet by remember { mutableStateOf(false) }

    // Lets the user pick exactly where the backup file is saved (SAF), so
    // it's guaranteed to be somewhere restoreLauncher's picker can browse
    // back to later — unlike a silent write into app-private storage.
    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) viewModel.exportBackup(uri, appVersionName(context))
    }

    CompositionLocalProvider(
        LocalToggleDetailViewer provides { activeToggleDetail = it },
    ) {
        var activeTab by remember { mutableStateOf<SettingsTab?>(null) }
        var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }
        var highlightedSettingId by remember { mutableStateOf<String?>(null) }
        val focusManager = LocalFocusManager.current

        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

        LaunchedEffect(highlightedSettingId) {
            if (highlightedSettingId != null) {
                delay(3200L)
                highlightedSettingId = null
            }
        }

        BackHandler(enabled = searchQuery.isNotBlank() || isSearchBarVisible || activeTab != null) {
            if (searchQuery.isNotBlank() || isSearchBarVisible) {
                viewModel.clearSearch()
                isSearchBarVisible = false
            } else if (activeTab != null) {
                activeTab = null
            }
        }

        GlassHeaderLayout(
            Modifier
                .fillMaxSize()
                .adaptiveContentWidth(maxWidth = 760.dp),
            header = { headerModifier, headerBackdrop ->
        ExpressiveHeader(
            modifier = headerModifier,
            backdrop = headerBackdrop,
            title = if (searchQuery.isNotBlank()) stringResource(R.string.settings_search_title) else (activeTab?.title ?: stringResource(R.string.settings)),
            onBack = {
                if (searchQuery.isNotBlank() || isSearchBarVisible) {
                    viewModel.clearSearch()
                    isSearchBarVisible = false
                } else if (activeTab != null) {
                    activeTab = null
                } else {
                    onBack()
                }
            },
            actions = {
                HeaderActionIcon(
                    icon = if (isSearchBarVisible || searchQuery.isNotBlank()) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = if (isSearchBarVisible || searchQuery.isNotBlank()) stringResource(R.string.settings_search_clear) else stringResource(R.string.settings_search_title),
                    onClick = {
                        if (isSearchBarVisible || searchQuery.isNotBlank()) {
                            viewModel.clearSearch()
                            isSearchBarVisible = false
                        } else {
                            isSearchBarVisible = true
                        }
                    },
                )
            },
        )
            },
        ) { headerInset ->
        // While searching, the search bar and results start below the header.
        val searching = isSearchBarVisible || searchQuery.isNotBlank()
        val listTopInset = if (searching) 0.dp else headerInset
        Column(Modifier.fillMaxSize().padding(top = headerInset - listTopInset)) {
        AnimatedVisibility(
            visible = isSearchBarVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            SettingsSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onClear = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.clearSearch()
                    } else {
                        isSearchBarVisible = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            )
        }

        if (searchQuery.isNotBlank()) {
            SettingsSearchResultsList(
                query = searchQuery,
                results = searchResults,
                onResultClick = { entry ->
                    focusManager.clearFocus()
                    viewModel.clearSearch()
                    isSearchBarVisible = false
                    activeTab = entry.parentTab
                    highlightedSettingId = entry.id
                },
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        } else {
        AnimatedContent(
            targetState = activeTab,
            transitionSpec = {
                if (targetState != null && initialState == null) {
                    (slideInHorizontally { width -> width / 4 } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width / 4 } + fadeOut(),
                    )
                } else if (targetState == null && initialState != null) {
                    (slideInHorizontally { width -> -width / 4 } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width / 4 } + fadeOut(),
                    )
                } else {
                    fadeIn().togetherWith(fadeOut())
                }
            },
            label = "SettingsTabTransition",
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { tab ->
            val listState = remember(tab) { LazyListState() }

            LaunchedEffect(highlightedSettingId, tab) {
                if (highlightedSettingId != null && tab != null) {
                    val targetIndex = getTargetSectionIndex(highlightedSettingId)
                    if (targetIndex > 0) {
                        delay(80L)
                        listState.animateScrollToItem(targetIndex)
                    } else if (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) {
                        delay(80L)
                        listState.animateScrollToItem(0)
                    }
                }
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp + listTopInset,
                    bottom = 32.dp + LocalMiniPlayerScrollClearance.current + safeDrawingBottomPadding(),
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize().safeHorizontalContentPadding(),
            ) {
                when (tab) {
                    null -> {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                SectionLabel("Collaboration & Social")
                                SettingsGroup(rowCount = 1) { _, position ->
                                    SettingsActionCard(
                                        icon = Icons.Filled.Group,
                                        iconContainer = MaterialTheme.colorScheme.primaryContainer,
                                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        title = stringResource(R.string.settings_jam_title),
                                        subtitle = if (jamUiState.isConnected) {
                                            "In Session: #${jamUiState.roomCode} \u2022 ${jamUiState.participants.size} listening together"
                                        } else {
                                            stringResource(R.string.settings_jam_sub)
                                        },
                                        onClick = onOpenJam,
                                        position = position,
                                    )
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                SectionLabel("Services & Add-ons")
                                SettingsGroup(rowCount = 4) { index, position ->
                                    when (index) {
                                        0 -> SettingsActionCard(
                                            icon = Icons.Filled.Extension,
                                            iconContainer = MaterialTheme.colorScheme.tertiaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                                            title = stringResource(R.string.settings_modules_title),
                                            subtitle = stringResource(R.string.settings_modules_sub),
                                            onClick = onOpenModules,
                                            position = position,
                                            isHighlighted = (highlightedSettingId == "services.modules" || highlightedSettingId == "library.modules"),
                                        )
                                        1 -> SettingsActionCard(
                                            icon = Icons.Filled.Album,
                                            iconContainer = MaterialTheme.colorScheme.primaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            title = stringResource(R.string.set_cat_lfm),
                                            subtitle = if (isLastFmConnected) "Connected as ${session.username} \u2022 Scrobbling active" else "Connect account, scrobbles & statistics sync",
                                            onClick = { activeTab = SettingsTab.LAST_FM },
                                            position = position,
                                        )
                                        2 -> SettingsActionCard(
                                            icon = Icons.Filled.CloudSync,
                                            iconContainer = MaterialTheme.colorScheme.secondaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            title = stringResource(R.string.set_cat_yt),
                                            subtitle = if (ytConnection.isConnected) {
                                                "Connected as ${ytConnection.accountName} • 24/7 library sync"
                                            } else {
                                                "YouTube Music account, channels, history & 24/7 sync"
                                            },
                                            onClick = { activeTab = SettingsTab.YOUTUBE },
                                            position = position,
                                        )
                                        else -> SettingsActionCard(
                                            icon = Icons.Filled.PlayCircle,
                                            iconContainer = MaterialTheme.colorScheme.secondaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            title = stringResource(R.string.set_cat_spotify),
                                            subtitle = if (spotifyConnected) {
                                                "Connected (sp_dc) • Canvas, playlist imports & Liked Songs sync"
                                            } else {
                                                "Account connection, Canvas, playlist imports & Liked Songs sync"
                                            },
                                            onClick = { activeTab = SettingsTab.SPOTIFY },
                                            position = position,
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                SectionLabel("Playback & UI")
                                SettingsGroup(rowCount = 3) { index, position ->
                                    when (index) {
                                        0 -> SettingsActionCard(
                                            icon = Icons.Filled.Palette,
                                            iconContainer = MaterialTheme.colorScheme.tertiaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                                            title = stringResource(R.string.set_cat_appear),
                                            subtitle = stringResource(R.string.set_cat_appear_sub),
                                            onClick = { activeTab = SettingsTab.APPEARANCE },
                                            position = position,
                                        )
                                        1 -> SettingsActionCard(
                                            icon = Icons.Filled.GraphicEq,
                                            iconContainer = MaterialTheme.colorScheme.primaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            title = stringResource(R.string.set_cat_audio),
                                            subtitle = stringResource(R.string.set_cat_audio_sub),
                                            onClick = { activeTab = SettingsTab.AUDIO },
                                            position = position,
                                        )
                                        else -> SettingsActionCard(
                                            icon = Icons.Filled.QueueMusic,
                                            iconContainer = MaterialTheme.colorScheme.secondaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            title = stringResource(R.string.set_cat_lib),
                                            subtitle = run {
                                                val count = downloadCount
                                                if (count > 0) "$count songs downloaded • Imports, Home layout" else "Home layout, Playlist imports, Downloads, Exclusions"
                                            },
                                            onClick = { activeTab = SettingsTab.LIBRARY },
                                            position = position,
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                SectionLabel("System & About")
                                SettingsGroup(rowCount = 2) { index, position ->
                                    when (index) {
                                        0 -> SettingsActionCard(
                                            icon = Icons.Filled.Backup,
                                            iconContainer = MaterialTheme.colorScheme.tertiaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                                            title = stringResource(R.string.set_cat_data),
                                            subtitle = stringResource(R.string.set_cat_data_sub),
                                            onClick = { activeTab = SettingsTab.DATA_BACKUP },
                                            position = position,
                                        )
                                        else -> SettingsActionCard(
                                            icon = Icons.Filled.Info,
                                            iconContainer = MaterialTheme.colorScheme.primaryContainer,
                                            iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            title = stringResource(R.string.set_cat_about),
                                            subtitle = "v${appVersionName(context)} • Language, Community, Source code",
                                            onClick = { activeTab = SettingsTab.ABOUT },
                                            position = position,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SettingsTab.AUDIO -> {
                        item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionLabel(stringResource(R.string.settings_section_audio))
                    val wifiQualitySubtitle = streamingQualityLabel(misc.wifiStreamingQuality)
                    val cellularQualitySubtitle = streamingQualityLabel(misc.cellularStreamingQuality)
                    val downloadQualitySubtitle = when (misc.downloadQuality) {
                        28 -> "Dolby Atmos (Spatial Immersive Audio)"
                        27 -> "Hi-Res (24-bit / 192 kHz FLAC)"
                        7 -> "Hi-Res (24-bit / 96 kHz FLAC)"
                        6 -> "CD Lossless (16-bit / 44.1 kHz FLAC)"
                        5 -> "Standard (320 kbps MP3)"
                        4 -> "Data Saver (96 kbps HE-AAC)"
                        -1 -> "YouTube Music (AAC / Opus)"
                        else -> "Hi-Res (24-bit / 192 kHz FLAC)"
                    }

                    val isIgnored = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
                    val audioRows = buildList<@Composable (GroupPosition) -> Unit> {
                        add { position ->
                            SettingsActionCard(
                                icon = Icons.Filled.Wifi,
                                iconContainer = MaterialTheme.colorScheme.primaryContainer,
                                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                title = "Wi-Fi streaming quality",
                                subtitle = wifiQualitySubtitle,
                                onClick = {
                                    editingCellularQuality = false
                                    showQualityDialog = true
                                },
                                position = position,
                                isHighlighted = (highlightedSettingId == "audio.streaming_quality" || highlightedSettingId == "audio.wifi_streaming_quality"),
                            )
                        }
                        add { position ->
                            SettingsActionCard(
                                icon = Icons.Filled.SignalCellularAlt,
                                iconContainer = MaterialTheme.colorScheme.secondaryContainer,
                                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                                title = "Cellular streaming quality",
                                subtitle = cellularQualitySubtitle,
                                onClick = {
                                    editingCellularQuality = true
                                    showQualityDialog = true
                                },
                                position = position,
                                isHighlighted = (highlightedSettingId == "audio.cellular_streaming_quality"),
                            )
                        }
                        add { position ->

                    SettingsActionCard(
                        icon = Icons.Filled.Code,
                        iconContainer = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        title = stringResource(R.string.settings_source_code),
                        subtitle = "github.com/jinoshaju2006/EVEE-Tune",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jinoshaju2006/EVEE-Tune"))
                            if (!startActivitySafely(context, intent)) {
                                viewModel.showToast(context.getString(R.string.toast_no_browser_plain))
                            }
                        },
                        isHighlighted = (highlightedSettingId == "about.source_code"),
                    )

                }
            }
                    }
                }
            }
        }
        }
        }
    }
    }

    // -- Custom color wheel dialog (par 8.4) --
    if (state.showColorWheel) {
        ColorWheelSheet(onDismiss = viewModel::dismissColorWheel, onApply = viewModel::applyCustomColor)
    }

    // -- Clear-all-data confirm --
    if (state.showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearAllConfirm,
            title = { Text(stringResource(R.string.dialog_clear_all_title)) },
            text = { Text(stringResource(R.string.dialog_clear_all_text)) },
            confirmButton = { TextButton(onClick = { viewModel.confirmClearAllData(onLoggedOut) }) { Text(stringResource(R.string.dialog_clear_all_confirm)) } },
            dismissButton = { TextButton(onClick = viewModel::dismissClearAllConfirm) { Text(stringResource(R.string.common_cancel)) } },
        )
    }

    // -- YouTube Music disconnect confirm --
    if (showYtDisconnectConfirm) {
        AlertDialog(
            onDismissRequest = { showYtDisconnectConfirm = false },
            title = { Text(stringResource(R.string.dialog_disconnect_yt_title)) },
            text = { Text(stringResource(R.string.dialog_disconnect_yt_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showYtDisconnectConfirm = false
                    viewModel.disconnectYouTube()
                }) { Text(stringResource(R.string.common_disconnect)) }
            },
            dismissButton = {
                TextButton(onClick = { showYtDisconnectConfirm = false }) { Text(stringResource(R.string.common_cancel)) }
            },
        )
    }

    if (showRecommendationSourceDialog) {
        RecommendationSourceDialog(
            current = if (spotifyConnected) misc.recommendationSource else RecommendationSource.YOUTUBE_MUSIC,
            spotifyConnected = spotifyConnected,
            onSelect = {
                viewModel.setRecommendationSource(it)
                showRecommendationSourceDialog = false
            },
            onDismiss = { showRecommendationSourceDialog = false },
        )
    }

    // -- Spotify disconnect confirm --
    if (showSpotifyDisconnectConfirm) {
        AlertDialog(
            onDismissRequest = { showSpotifyDisconnectConfirm = false },
            title = { Text("Disconnect Spotify?") },
            text = { Text("This will remove your Spotify session and log you out. Spotify Canvas loops and synced playlists will be disabled until you reconnect.") },
            confirmButton = {
                TextButton(onClick = {
                    showSpotifyDisconnectConfirm = false
                    viewModel.disconnectSpotify()
                    runCatching {
                        val cm = android.webkit.CookieManager.getInstance()
                        for (domain in listOf("https://spotify.com", "https://accounts.spotify.com", "https://open.spotify.com")) {
                            val cookies = cm.getCookie(domain) ?: continue
                            for (cookie in cookies.split(";")) {
                                val name = cookie.substringBefore("=").trim()
                                if (name.isNotEmpty()) {
                                    cm.setCookie(domain, "$name=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/")
                                }
                            }
                        }
                        cm.flush()
                    }
                }) { Text(stringResource(R.string.common_disconnect)) }
            },
            dismissButton = {
                TextButton(onClick = { showSpotifyDisconnectConfirm = false }) { Text(stringResource(R.string.common_cancel)) }
            },
        )
    }

    if (showCookieLoginDialog) {
        CookieLoginDialog(
            onDismiss = { showCookieLoginDialog = false },
            onConnect = { cookies ->
                viewModel.connectWithCookies(cookies) { success, _ ->
                    if (success) showCookieLoginDialog = false
                }
            },
        )
    }

    // -- Restore confirm --
    if (state.showRestoreConfirm) {        val isPlaylistMirror = state.pendingRestoreKind == PendingRestoreKind.PLAYLIST_MIRROR
        AlertDialog(
            onDismissRequest = viewModel::dismissRestoreConfirm,
            title = { Text(if (isPlaylistMirror) "Sync playlist JSON?" else "Restore backup?") },
            text = {
                Text(
                    if (isPlaylistMirror) {
                        "This will merge ${state.pendingRestorePlaylistCount ?: 0} playlist(s) from the local JSON file and reconnect automatic syncing."
                    } else {
                        "This will replace your current data with ${state.pendingRestorePlaylistCount ?: 0} playlist(s) and all settings from the backup file."
                    },
                )
            },
            confirmButton = { TextButton(onClick = { viewModel.confirmRestore(onBack) }) { Text(if (isPlaylistMirror) stringResource(R.string.common_sync) else stringResource(R.string.common_restore)) } },
            dismissButton = { TextButton(onClick = viewModel::dismissRestoreConfirm) { Text(stringResource(R.string.common_cancel)) } },
        )
    }

    // -- App language picker (Settings -> Language) styled like reference screenshot --
    if (showLanguageDialog) {
        var selectedLanguage by remember(currentLanguage) { mutableStateOf(currentLanguage) }
        androidx.compose.ui.window.Dialog(onDismissRequest = { showLanguageDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.Language,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.settings_language_dialog_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp),
                        ) {
                            items(AppLanguage.SELECTABLE) { language ->
                                val isSelected = language == selectedLanguage
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            else Color.Transparent
                                        )
                                        .clickable { selectedLanguage = language }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (language == AppLanguage.SYSTEM) {
                                        Icon(
                                            Icons.Filled.Android,
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp),
                                        )
                                    } else {
                                        val flag = when (language) {
                                            AppLanguage.ENGLISH -> "🇺🇸"
                                            AppLanguage.TURKISH -> "🇹🇷"
                                            AppLanguage.CHINESE_SIMPLIFIED -> "🇨🇳"
                                            AppLanguage.RUSSIAN -> "🇷🇺"
                                            AppLanguage.PORTUGUESE_BRAZIL -> "🇧🇷"
                                            AppLanguage.SPANISH -> "🇪🇸"
                                            AppLanguage.INDONESIAN -> "🇮🇩"
                                            AppLanguage.HINDI -> "🇮🇳"
                                            AppLanguage.GERMAN -> "🇩🇪"
                                            AppLanguage.FRENCH -> "🇫🇷"
                                            AppLanguage.ITALIAN -> "🇮🇹"
                                            AppLanguage.JAPANESE -> "🇯🇵"
                                            AppLanguage.KOREAN -> "🇰🇷"
                                            AppLanguage.ARABIC -> "🇸🇦"
                                            AppLanguage.VIETNAMESE -> "🇻🇳"
                                            else -> "🌐"
                                        }
                                        Text(
                                            flag,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Text(
                                        language.nativeDisplayName(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface
                                               else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f),
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedLanguage = language },
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = { showLanguageDialog = false },
                            shape = CircleShape,
                        ) {
                            Text(
                                stringResource(R.string.common_cancel),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.setAppLanguage(selectedLanguage)
                                showLanguageDialog = false
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                        ) {
                            Text(
                                stringResource(android.R.string.ok),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }

    // -- Enable Scrobbling password dialog --
    if (state.showSessionKeyDialog) {
        var password by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = viewModel::dismissSessionKeyDialog,
            title = { Text(stringResource(R.string.set_scrobble_title)) },
            text = {
                Column {
                    Text(
                        "Last.fm only allows scrobbling through a signed session, and the only way to get one without a browser is with your password. It's sent once, directly to Last.fm over HTTPS, and never stored.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.set_scrobble_pass)) },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        isError = state.sessionKeyError != null,
                        supportingText = state.sessionKeyError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.submitPassword(password) },
                    enabled = !state.sessionKeyLoading,
                ) {
                    if (state.sessionKeyLoading) {
                        com.lastwave.app.ui.common.ExpressiveInlineLoadingIndicator(size = 18.dp)
                    } else {
                        Text(stringResource(R.string.set_scrobble_enable))
                    }
                }
            },
            dismissButton = { TextButton(onClick = viewModel::dismissSessionKeyDialog) { Text(stringResource(R.string.common_cancel)) } },
        )
    }

    if (showAutoEq) {
        com.lastwave.app.ui.settings.autoeq.AutoEqDialog(
            onDismiss = { showAutoEq = false },
            onOpenGraphicEq = {
                showAutoEq = false
                showEqSheet = true
            },
        )
    }

    if (showJamSheet) {
        LastWaveJamSheet(
            viewModel = jamViewModel,
            onDismiss = { showJamSheet = false },
        )
    }

    // -- Experimental 15-band equalizer --
    if (showEqSheet) {
        EqualizerSheet(
            eq = eq,
            onDismiss = { showEqSheet = false },
            onSetEnabled = viewModel::setEqualizerEnabled,
            onPickPreset = viewModel::applyEqPreset,
            onBandPreview = viewModel::previewEqBandGain,
            onBandChange = viewModel::setEqBandGain,
            onOpenAutoEq = {
                showEqSheet = false
                showAutoEq = true
            },
        )
    }



    // -- Lyrics provider picker: preferred source first, automatic
    // fallback to the rest when it returns nothing --
    if (showLyricsProviderDialog) {
        LyricsProviderDialog(
            current = misc.lyricsProvider,
            onSelect = {
                viewModel.setLyricsProvider(it)
                showLyricsProviderDialog = false
            },
            onDismiss = { showLyricsProviderDialog = false },
        )
    }

    // -- Lyrics sync offset stepper: shifts highlight/focus only --
    if (showLyricsOffsetDialog) {
        LyricsOffsetDialog(
            currentMs = misc.lyricsOffsetMs,
            onSelect = { viewModel.setLyricsOffsetMs(it) },
            onDismiss = { showLyricsOffsetDialog = false },
        )
    }

    // -- Lyrics text size dialog: scales font size with live preview --
    if (showLyricsSizeDialog) {
        LyricsSizeDialog(
            currentScale = misc.lyricsFontScale,
            onSelect = { viewModel.setLyricsFontScale(it) },
            onDismiss = { showLyricsSizeDialog = false },
        )
    }

    // -- Floating lyrics buttons: idle time before they fade out --
    if (showLyricsAutoHideDialog) {
        LyricsAutoHideDialog(
            currentSeconds = misc.lyricsButtonsAutoHideSeconds,
            onSelect = {
                viewModel.setLyricsButtonsAutoHideSeconds(it)
                showLyricsAutoHideDialog = false
            },
            onDismiss = { showLyricsAutoHideDialog = false },
        )
    }

    // -- Language the lyrics translate button turns songs into --
    if (showTranslationLanguageDialog) {
        TranslationLanguageDialog(
            current = misc.translationLanguage,
            onSelect = {
                viewModel.setTranslationLanguage(it)
                showTranslationLanguageDialog = false
            },
            onDismiss = { showTranslationLanguageDialog = false },
        )
    }

    if (showLoudnessDialog) {
        LoudnessModeDialog(
            current = loudness.mode,
            onSelect = {
                viewModel.setLoudnessMode(it)
                showLoudnessDialog = false
            },
            onDismiss = { showLoudnessDialog = false },
        )
    }

    if (showClarityPresetDialog) {
        ClarityPresetDialog(
            currentIndex = misc.clarityPreset,
            onSelect = {
                viewModel.setClarityPreset(it)
                showClarityPresetDialog = false
            },
            onDismiss = { showClarityPresetDialog = false },
        )
    }

    // -- Selective Playlist Sync sheet --
    if (showSyncPlaylistsSheet) {
        SyncPlaylistsSheet(
            playlists = allPlaylists,
            syncedIds = syncedPlaylistIds,
            onToggleSync = viewModel::togglePlaylistSync,
            onSelectAll = viewModel::selectAllPlaylistsForSync,
            onDismiss = { showSyncPlaylistsSheet = false },
        )
    }

    if (showYtLibraryVisibilitySheet) {
        YouTubeLibraryVisibilitySheet(
            playlists = ytAccountPlaylists,
            hiddenIds = hiddenYtLibraryPlaylistIds,
            onSetVisible = viewModel::setYtLibraryPlaylistVisible,
            onSetAllVisible = viewModel::setAllYtLibraryPlaylistsVisible,
            onDismiss = { showYtLibraryVisibilitySheet = false },
        )
    }

    if (showYtChannelSheet) {
        YouTubeChannelSheet(
            channels = ytChannels,
            isLoading = ytChannelsLoading,
            selectedChannelId = ytConnection.onBehalfOfUser,
            selectedAuthUser = ytConnection.authUserIndex,
            selectedPageId = ytConnection.pageId,
            onReload = viewModel::loadYtChannels,
            onSelect = {
                viewModel.selectYtChannel(it)
                showYtChannelSheet = false
            },
            onDismiss = { showYtChannelSheet = false },
        )
    }

    if (showQualityDialog) {
        val tiers = listOfNotNull(
            Triple(
                CELLULAR_QUALITY_AUTO,
                "Auto",
                "Adapts to your connection • Up to your Wi-Fi quality" to "AUTO",
            ).takeIf { editingCellularQuality },
            Triple(28, "Dolby Atmos", "Spatial Immersive Audio • Master Audio" to "ATMOS"),
            Triple(27, "Hi-Res Lossless", "Up to 24-bit / 192 kHz • Lossless FLAC" to "HI-RES"),
            Triple(5, "Standard Quality", "Up to 320 kbps • MP3 / AAC" to "320 kbps"),
            Triple(-1, "YouTube Music", "128-256 kbps • YouTube Music AAC / Opus stream" to "YOUTUBE"),
            Triple(4, "Data Saver", "Low-bitrate audio to reduce mobile data use" to "DATA SAVER"),
        )
        val selectedQuality = if (editingCellularQuality) misc.cellularStreamingQuality else misc.wifiStreamingQuality
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

        ModalBottomSheet(
            onDismissRequest = { showQualityDialog = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 36.dp, height = 4.dp),
                ) {}
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .adaptiveContentWidth(maxWidth = 640.dp)
                    .align(Alignment.CenterHorizontally)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp + safeDrawingBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.HighQuality,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            if (editingCellularQuality) "Cellular streaming quality" else "Wi-Fi streaming quality",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Choose the preferred quality for this connection",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Text(
                    "YouTube Music plays strictly as-is (never upgrades). Dolby Atmos plays only when selected — otherwise it never plays. Atmos falls back to Hi-Res → CD → 320 kbps → YouTube. Hi-Res steps down to lower lossless tiers, then YouTube last.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    tiers.forEach { (qualityId, title, meta) ->
                        val (subtitle, badge) = meta
                        val isSelected = selectedQuality == qualityId
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.ContextClick)
                                if (editingCellularQuality) {
                                    viewModel.setCellularStreamingQuality(qualityId)
                                } else {
                                    viewModel.setWifiStreamingQuality(qualityId)
                                }
                                showQualityDialog = false
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            shadowElevation = if (isSelected) 3.dp else 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                                        ) {
                                            Text(
                                                badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = stringResource(R.string.set_selected_cd),
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDownloadQualityDialog) {
        val downloadTiers = listOf(
            Triple(28, "Dolby Atmos", "Spatial Immersive Audio • Master Audio" to "ATMOS"),
            Triple(27, "Hi-Res Audio (Max)", "Up to 24-bit / 192 kHz • Studio Master FLAC" to "24-BIT / 192k"),
            Triple(7, "Hi-Res Audio", "24-bit / 96 kHz • Studio FLAC" to "24-BIT / 96k"),
            Triple(6, "CD Lossless", "16-bit / 44.1 kHz • Bit-Exact CD FLAC" to "16-BIT / 44.1k"),
            Triple(5, "Standard Quality", "320 kbps • High-Bitrate MP3 / AAC" to "320 kbps"),
            Triple(4, "Data Saver", "96 kbps • High Efficiency AAC" to "96 kbps"),
            Triple(-1, "YouTube Music", "128-256 kbps • YouTube Music AAC / Opus stream" to "YOUTUBE"),
        )
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

        ModalBottomSheet(
            onDismissRequest = { showDownloadQualityDialog = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 36.dp, height = 4.dp),
                ) {}
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .adaptiveContentWidth(maxWidth = 640.dp)
                    .align(Alignment.CenterHorizontally)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp + safeDrawingBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "Download Quality",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Select offline audio resolution & bit depth",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Text(
                    "YouTube Music downloads strictly as-is (never upgrades). Dolby Atmos downloads only when selected — otherwise it never downloads. Atmos falls back to Hi-Res → CD → 320 kbps → YouTube. Hi-Res steps down to lower tiers, then YouTube last.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    downloadTiers.forEach { (qualityId, title, meta) ->
                        val (subtitle, badge) = meta
                        val isSelected = misc.downloadQuality == qualityId
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.ContextClick)
                                viewModel.setDownloadQuality(qualityId)
                                showDownloadQualityDialog = false
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            shadowElevation = if (isSelected) 3.dp else 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                                        ) {
                                            Text(
                                                badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = stringResource(R.string.set_selected_cd),
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showYouTubeImportSheet) {
        YouTubeImportSheet(
            onDismiss = { showYouTubeImportSheet = false },
            innerTube = viewModel.innerTube,
            importManager = viewModel.playlistImportManager,
            onImportSuccess = { saved ->
                viewModel.showToast(context.getString(R.string.toast_imported, saved.title, saved.tracks.size))
            },
        )
    }

    state.toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            viewModel.dismissToast()
        }
        Box(
            Modifier
                .fillMaxSize()
                .safeHorizontalContentPadding()
                .padding(
                    bottom = 24.dp +
                        LocalMiniPlayerScrollClearance.current +
                        safeDrawingBottomPadding(),
                ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(shape = ExpressivePillShape, color = MaterialTheme.colorScheme.inverseSurface, tonalElevation = 6.dp) {
                Text(msg, color = MaterialTheme.colorScheme.inverseOnSurface, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            }
        }
    }

    if (activeToggleDetail != null) {
        val detail = activeToggleDetail!!
        Dialog(
            onDismissRequest = { activeToggleDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                // Scrim background (unblurred content on top)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { activeToggleDetail = null },
                        ),
                )
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .widthIn(max = 420.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (detail.checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.size(44.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = if (detail.checked) Icons.Filled.Check else Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = if (detail.checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (detail.checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                            ) {
                                Text(
                                    text = if (detail.checked) "Enabled" else "Disabled",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (detail.checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = detail.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = detail.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                        )
                        Spacer(Modifier.height(20.dp))
                        FilledTonalButton(
                            onClick = { activeToggleDetail = null },
                            modifier = Modifier.align(Alignment.End),
                            shape = CircleShape,
                        ) {
                            Text("Close", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
