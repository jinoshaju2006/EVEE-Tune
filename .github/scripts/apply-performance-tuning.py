#!/usr/bin/env python3
"""Apply conservative EVEE Tune runtime-performance improvements after upstream import.

These changes preserve app features and playback logic. They reduce background
composition work during main-tab scrolling and shorten image crossfades in
artwork-heavy lists.
"""
from pathlib import Path

def replace_once(path: str, old: str, new: str) -> None:
    file = Path(path)
    if not file.exists():
        raise SystemExit(f"Performance patch target missing: {path}")
    source = file.read_text(encoding="utf-8")
    count = source.count(old)
    if count != 1:
        raise SystemExit(f"Expected exactly one performance patch anchor in {path}; found {count}")
    file.write_text(source.replace(old, new, 1), encoding="utf-8")

replace_once(
    "app/src/main/java/com/lastwave/app/ui/shell/MainShell.kt",
    """            // Keep every tab composed. With 1, jumping Playlists -> Home
            // (dock tap = instant scrollToPage) disposed and rebuilt the whole
            // Feed (sections, shelves, covers) on that frame, and Statistics
            // was rebuilt the same way from the far tab. Only 3 tabs, and
            // off-screen pages are not drawn, so this is cheap.
            beyondViewportPageCount = tabs.size - 1,""",
    """            // Keep only the visible tab composed. Keeping Feed, Statistics,
            // and Playlists alive together retains many image requests and
            // animations while the user scrolls one screen. Pager's saveable
            // state holder preserves page state when an off-screen tab returns.
            // Compose adjacent pages as the user swipes rather than running all
            // three destinations continuously in the background.
            beyondViewportPageCount = 0,""",
)

replace_once(
    "app/src/main/java/com/lastwave/app/ui/common/ArtworkImage.kt",
    ".crossfade(300)",
    ".crossfade(100)",
)

print("Applied EVEE Tune scrolling and artwork-rendering performance improvements.")
