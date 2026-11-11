package com.lastwave.app.playback

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Small, opt-in floating now-playing capsule for devices without a native
 * OEM media island. The system overlay permission is never requested silently.
 */
internal class DynamicBarOverlay(
    context: Context,
    private val onOpen: () -> Unit,
    private val onPlayPause: () -> Unit,
    private val onNext: () -> Unit,
) {
    private val appContext = context.applicationContext
    private val windowManager = appContext.getSystemService(WindowManager::class.java)
    private var root: FrameLayout? = null
    private var titleView: TextView? = null
    private var artistView: TextView? = null
    private var playView: TextView? = null

    fun update(title: String, artist: String, isPlaying: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM ||
            !Settings.canDrawOverlays(appContext) || title.isBlank()
        ) {
            hide()
            return
        }

        val view = root ?: createView().also { root = it }
        titleView?.text = title
        artistView?.text = artist
        playView?.text = if (isPlaying) "Ⅱ" else "▶"
        if (view.parent == null) {
            runCatching { windowManager.addView(view, windowParams()) }
                .onFailure { root = null }
        }
    }

    fun hide() {
        val view = root ?: return
        runCatching { windowManager.removeViewImmediate(view) }
        root = null
        titleView = null
        artistView = null
        playView = null
    }

    private fun createView(): FrameLayout {
        val frame = FrameLayout(appContext)
        val pill = LinearLayout(appContext).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(10), dp(8))
            background = GradientDrawable().apply {
                setColor(Color.rgb(25, 26, 30))
                cornerRadius = dp(32).toFloat()
                setStroke(dp(1), Color.argb(70, 255, 255, 255))
            }
            elevation = dp(8).toFloat()
        }
        val musicMark = TextView(appContext).apply {
            text = "♫"
            textSize = 21f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(Color.rgb(75, 83, 105))
                shape = GradientDrawable.OVAL
            }
        }
        pill.addView(musicMark, LinearLayout.LayoutParams(dp(40), dp(40)))
        val labels = LinearLayout(appContext).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), 0, dp(8), 0)
            isClickable = true
            setOnClickListener { onOpen() }
        }
        val titleLabel = TextView(appContext).apply {
            textSize = 13f
            setTextColor(Color.WHITE)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        val artistLabel = TextView(appContext).apply {
            textSize = 11f
            setTextColor(Color.LTGRAY)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        titleView = titleLabel
        artistView = artistLabel
        labels.addView(titleLabel)
        labels.addView(artistLabel)
        pill.addView(labels, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        val playButton = actionButton("Ⅱ", "Pause or resume") { onPlayPause() }
        playView = playButton
        pill.addView(playButton, LinearLayout.LayoutParams(dp(42), dp(42)))
        pill.addView(actionButton("›", "Next track") { onNext() }, LinearLayout.LayoutParams(dp(38), dp(42)))
        frame.addView(
            pill,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ).apply {
                leftMargin = dp(16)
                rightMargin = dp(16)
            },
        )
        frame.setOnClickListener { onOpen() }
        return frame
    }

    private fun actionButton(label: String, description: String, action: () -> Unit) =
        TextView(appContext).apply {
            text = label
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            contentDescription = description
            isFocusable = true
            isClickable = true
            setOnClickListener { action() }
            background = GradientDrawable().apply {
                setColor(Color.argb(35, 255, 255, 255))
                shape = GradientDrawable.OVAL
            }
        }

    private fun windowParams() = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        dp(68),
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT,
    ).apply {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        y = dp(22)
    }

    private fun dp(value: Int): Int =
        (value * appContext.resources.displayMetrics.density).toInt()
}
