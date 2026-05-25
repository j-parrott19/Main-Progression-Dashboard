# Mainframe

Mainframe is a RuneLite sidebar dashboard for normal members main accounts. It helps players answer "what should I work on next?" by combining a curated mid-game roadmap, safe progress detection, and local custom checklist items.

It does not automate gameplay, generate input, communicate with game worlds, display pathing, highlight quest steps, or try to replace Quest Helper. Gear unlocks, PvM rewards, minigame rewards, and ambiguous account unlocks remain manual checklist items unless RuneLite can safely observe them.

## Features

- Curated main-account goals for account unlocks, skill targets, gear goals, and quest clusters.
- Safe automatic progress checks for visible skill levels and completed quests.
- Per-character progression paths for balanced, bossing, PvP, completion, and maxing priorities.
- Optional public hiscore import for boss kills, clue counts, collection log count, minigame rows, and skill backup.
- Manual checkboxes for gear, reward unlocks, prayer scrolls, defenders, fire cape, and custom goals.
- Local-only persistence through RuneLite configuration storage.
- Per-character saved progress when logged in, with a profile fallback before login.

## Development

This project follows the RuneLite external plugin template.

```powershell
# Use a Java 17 or 21 JDK for Gradle; the plugin still compiles with --release 11.
$env:JAVA_HOME='C:\Path\To\jdk-21'
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat run
```

`run` now builds the plugin jar, copies it into `%USERPROFILE%\.runelite\sideloaded-plugins`, and opens the Jagex Launcher. Start RuneLite from the Jagex Launcher after the task finishes so the plugin loads into the launcher-authenticated client.

In the Jagex Launcher, configure RuneLite's client arguments to include `--developer-mode --debug`. Developer mode is what allows RuneLite to load the sideloaded plugin jar.

If RuneLite is installed in a custom profile directory or the launcher is somewhere else:

```powershell
.\gradlew.bat run -PruneliteHome='C:\Path\To\.runelite' -PjagexLauncherPath='C:\Path\To\JagexLauncher.exe'
```

For the old standalone development-client flow, use `.\gradlew.bat runDevelopmentClient`.

RuneLite Plugin Hub submission is intentionally not included yet; it requires a public GitHub repository URL and commit hash.

## Compliance Boundary

Mainframe is a passive dashboard. It avoids generated mouse/keyboard input, world communication, game-object guidance, NPC/object click direction, path overlays, and step-by-step quest solving.
