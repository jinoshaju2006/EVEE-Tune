from pathlib import Path
import re

def replace_once(path, old, new, label):
    source = path.read_text(encoding="utf-8")
    count = source.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}.")
    path.write_text(source.replace(old, new, 1), encoding="utf-8")

# Keep this experimental build separate from the stable branch.
gradle = Path("app/build.gradle.kts")
source = gradle.read_text(encoding="utf-8")
if source.count('versionCode = 30') != 1 or source.count('versionName = "5.1.2"') != 1:
    raise SystemExit("Experimental version metadata did not match the pinned upstream baseline.")
source = source.replace('versionCode = 30', 'versionCode = 31', 1)
source = source.replace('versionName = "5.1.2"', 'versionName = "5.1.2-experimental"', 1)
gradle.write_text(source, encoding="utf-8")

# Persist the opt-in refresh-rate preference. Existing installs default to System Default.
prefs = Path("app/src/main/java/com/lastwave/app/data/local/SettingsPreferences.kt")
source = prefs.read_text(encoding="utf-8")
source = source.replace(
    '    val dynamicNowPlayingEnabled: Boolean = false,\n',
    '    val dynamicNowPlayingEnabled: Boolean = false,\n'
    '    /** Experimental display preference. False preserves Android System Default. */\n'
    '    val highestRefreshRateEnabled: Boolean = false,\n',
    1,
)
source = source.replace(
    '        val DYNAMIC_NOW_PLAYING = booleanPreferencesKey("lw_dynamic_now_playing")\n',
    '        val DYNAMIC_NOW_PLAYING = booleanPreferencesKey("lw_dynamic_now_playing")\n'
    '        val HIGHEST_REFRESH_RATE_ENABLED = booleanPreferencesKey("evee_highest_refresh_rate_enabled")\n',
    1,
)
source = source.replace(
    '                dynamicNowPlayingEnabled = p.readSafely(Keys.DYNAMIC_NOW_PLAYING) ?: false,\n',
    '                dynamicNowPlayingEnabled = p.readSafely(Keys.DYNAMIC_NOW_PLAYING) ?: false,\n'
    '                highestRefreshRateEnabled = p.readSafely(Keys.HIGHEST_REFRESH_RATE_ENABLED) ?: false,\n',
    1,
)
needle = '    suspend fun setDynamicNowPlaying(enabled: Boolean) {\n        dataStore.edit { it[Keys.DYNAMIC_NOW_PLAYING] = enabled }\n    }\n'
if source.count(needle) != 1:
    raise SystemExit("Could not locate SettingsPreferences setter insertion point.")
source = source.replace(
    needle,
    needle + '\n    suspend fun setHighestRefreshRateEnabled(enabled: Boolean) {\n'
    '        dataStore.edit { it[Keys.HIGHEST_REFRESH_RATE_ENABLED] = enabled }\n'
    '    }\n',
    1,
)
prefs.write_text(source, encoding="utf-8")

# Expose the preference to the Appearance screen.
vm = Path("app/src/main/java/com/lastwave/app/ui/settings/SettingsViewModel.kt")
source = vm.read_text(encoding="utf-8")
needle = '    fun setDynamicNowPlaying(enabled: Boolean) {\n        viewModelScope.launch {\n            settingsPreferences.setDynamicNowPlaying(enabled)\n        }\n    }\n'
if needle not in source:
    raise SystemExit("Could not locate SettingsViewModel dynamic-now-playing setter.")
source = source.replace(
    needle,
    needle + '\n    fun setHighestRefreshRateEnabled(enabled: Boolean) {\n'
    '        viewModelScope.launch {\n'
    '            settingsPreferences.setHighestRefreshRateEnabled(enabled)\n'
    '        }\n'
    '    }\n',
    1,
)
vm.write_text(source, encoding="utf-8")

# Apply the preference only to this app's window; never change the device-wide setting.
activity = Path("app/src/main/java/com/lastwave/app/MainActivity.kt")
source = activity.read_text(encoding="utf-8")
source = source.replace(
    'import kotlinx.coroutines.flow.distinctUntilChanged\n',
    'import kotlinx.coroutines.flow.distinctUntilChanged\nimport kotlinx.coroutines.flow.collect\n',
    1,
)
source = source.replace(
    'import kotlinx.coroutines.flow.drop\n',
    'import kotlinx.coroutines.flow.drop\n',
    1,
)
source = source.replace(
    'import kotlinx.coroutines.flow.map\n',
    'import kotlinx.coroutines.flow.map\n',
    1,
)
old_initial = '''        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            runCatching {
                val currentDisplay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    display
                } else {
                    @Suppress("DEPRECATION")
                    windowManager.defaultDisplay
                }
                val modes = currentDisplay?.supportedModes
                val maxRefreshMode = modes?.maxByOrNull { it.refreshRate }
                if (maxRefreshMode != null && maxRefreshMode.refreshRate > 60f) {
                    val params = window.attributes
                    params.preferredDisplayModeId = maxRefreshMode.modeId
                    window.attributes = params
                }
            }
        }
'''
if source.count(old_initial) != 1:
    raise SystemExit("Could not locate the unconditional startup refresh-rate request.")
source = source.replace(old_initial, '', 1)
old_resume = '''    override fun onResume() {
        super.onResume()
        requestHighestSupportedRefreshRate()
    }
'''
new_resume = '''    override fun onResume() {
        super.onResume()
    }
'''
if source.count(old_resume) != 1:
    raise SystemExit("Could not locate onResume refresh-rate hook.")
source = source.replace(old_resume, new_resume, 1)
start = source.find('    /**\n     * Ask the window scheduler for the panel\\'s fastest supported rate.')
end = source.find('\n    private fun handlePlaybackIntent', start)
if start < 0 or end < 0:
    raise SystemExit("Could not locate the existing refresh-rate helper.")
new_helper = '''    /**
     * Applies an app-window refresh preference. System Default clears the
     * preference; Highest uses the fastest same-resolution mode up to 144 Hz.
     * Android/OEM power and thermal policies remain authoritative.
     */
    @Suppress("DEPRECATION")
    private fun applyDisplayRefreshRate(highestEnabled: Boolean) {
        runCatching {
            val currentDisplay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display ?: windowManager.defaultDisplay
            } else {
                windowManager.defaultDisplay
            }
            val attributes = window.attributes
            if (!highestEnabled) {
                if (attributes.preferredDisplayModeId != 0 || attributes.preferredRefreshRate != 0f) {
                    attributes.preferredDisplayModeId = 0
                    attributes.preferredRefreshRate = 0f
                    window.attributes = attributes
                }
                return@runCatching
            }

            val currentMode = currentDisplay.mode
            val selectedMode = currentDisplay.supportedModes
                .filter {
                    it.physicalWidth == currentMode.physicalWidth &&
                        it.physicalHeight == currentMode.physicalHeight &&
                        it.refreshRate <= 144.1f
                }
                .maxByOrNull { it.refreshRate }

            if (selectedMode == null || selectedMode.refreshRate <= 60f) {
                attributes.preferredDisplayModeId = 0
                attributes.preferredRefreshRate = 0f
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    attributes.preferredDisplayModeId = selectedMode.modeId
                }
                attributes.preferredRefreshRate = selectedMode.refreshRate
            }
            window.attributes = attributes
        }.onFailure {
            android.util.Log.w(STARTUP_TAG, "Display refresh preference could not be applied", it)
        }
    }
'''
source = source[:start] + new_helper + source[end:]
# Observe the setting live, so changes take effect without restarting the app.
anchor = '        super.onCreate(savedInstanceState)\n        volumeControlStream = android.media.AudioManager.STREAM_MUSIC\n'
if source.count(anchor) != 1:
    raise SystemExit("Could not locate MainActivity initialization for refresh preference observer.")
source = source.replace(
    anchor,
    anchor + '''        lifecycleScope.launch {
            settingsPreferences.settings
                .map { it.highestRefreshRateEnabled }
                .distinctUntilChanged()
                .collect { enabled -> applyDisplayRefreshRate(enabled) }
        }
''',
    1,
)
activity.write_text(source, encoding="utf-8")

# Add the opt-in control to Appearance & Visuals without removing existing rows.
screen = Path("app/src/main/java/com/lastwave/app/ui/settings/SettingsScreen.kt")
source = screen.read_text(encoding="utf-8")
appearance_start = source.find('SettingsTab.APPEARANCE -> {')
appearance_end = source.find('SettingsTab.YOUTUBE -> {', appearance_start)
if appearance_start < 0 or appearance_end < 0:
    raise SystemExit("Could not locate Appearance tab boundaries.")
appearance = source[appearance_start:appearance_end]
if appearance.count('SettingsGroup(rowCount = 11)') != 1:
    raise SystemExit("Appearance settings group row count did not match expected baseline.")
appearance = appearance.replace('SettingsGroup(rowCount = 11)', 'SettingsGroup(rowCount = 12)', 1)
needle = '''                            10 -> SettingsToggleCard(
                                icon = Icons.Filled.Vibration,
                                iconContainer = MaterialTheme.colorScheme.primaryContainer,
                                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                title = stringResource(R.string.settings_ui_haptics),
                                subtitle = stringResource(R.string.settings_ui_haptics_sub),
                                checked = theme?.uiHaptics ?: true,
                                onCheckedChange = viewModel::setUiHaptics,
                                position = position,
                                isHighlighted = (highlightedSettingId == "appearance.ui_haptics"),
                            )
'''
if appearance.count(needle) != 1:
    raise SystemExit("Could not locate the final Appearance setting row.")
appearance = appearance.replace(
    needle,
    needle + '''                            11 -> SettingsToggleCard(
                                icon = Icons.Filled.Bolt,
                                iconContainer = MaterialTheme.colorScheme.tertiaryContainer,
                                iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                                title = "Highest Refresh Rate (up to 144 Hz)",
                                subtitle = if (misc.highestRefreshRateEnabled) {
                                    "Using the highest supported display mode • Android may adjust for power or heat"
                                } else {
                                    "System Default • Let Android manage display refresh rate"
                                },
                                checked = misc.highestRefreshRateEnabled,
                                onCheckedChange = viewModel::setHighestRefreshRateEnabled,
                                position = position,
                                isHighlighted = (highlightedSettingId == "appearance.highest_refresh_rate"),
                            )
''',
    1,
)
source = source[:appearance_start] + appearance + source[appearance_end:]

# Slightly richer spring feedback and animated expansion for existing settings cards.
source = source.replace(
    '    Column(verticalArrangement = Arrangement.spacedBy(GROUP_GAP)) {',
    '    Column(\n        modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)),\n        verticalArrangement = Arrangement.spacedBy(GROUP_GAP),\n    ) {',
    1,
)
source = source.replace(
    'targetValue = if (pressed) 0.97f else 1f,\n        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),',
    'targetValue = if (pressed) 0.955f else 1f,\n        animationSpec = spring(dampingRatio = 0.72f, stiffness = 650f),',
    1,
)
screen.write_text(source, encoding="utf-8")

# Add gentle forward/back navigation transitions to the main app NavHost.
nav_files = list(Path("app/src/main/java").rglob("*.kt"))
nav_patched = False
for nav_file in nav_files:
    nav_source = nav_file.read_text(encoding="utf-8")
    if "LastWaveNavHost" not in nav_source and "NavHost(" not in nav_source:
        continue
    match = re.search(r'NavHost\\((.*?)\\)\\s*\\{', nav_source, flags=re.DOTALL)
    if not match or "enterTransition" in match.group(1):
        continue
    args = match.group(1).rstrip()
    transition_args = '''
    enterTransition = {
        androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) +
            androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { width -> width / 14 },
                animationSpec = androidx.compose.animation.core.tween(220),
            )
    },
    exitTransition = {
        androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(160)) +
            androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { width -> -width / 20 },
                animationSpec = androidx.compose.animation.core.tween(160),
            )
    },
    popEnterTransition = {
        androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) +
            androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { width -> -width / 14 },
                animationSpec = androidx.compose.animation.core.tween(220),
            )
    },
    popExitTransition = {
        androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(160)) +
            androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { width -> width / 20 },
                animationSpec = androidx.compose.animation.core.tween(160),
            )
    },'''
    nav_source = nav_source[:match.start(1)] + args + transition_args + nav_source[match.end(1):]
    nav_file.write_text(nav_source, encoding="utf-8")
    nav_patched = True
    break

if not nav_patched:
    print("Note: no main NavHost call was found; settings press/expand animations are still enabled.")
