package io.github.kroune.pollen

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Entry point consumed by the iOS app (`ContentView.swift`) via
 * `MainViewControllerKt.MainViewController()`. Koin is started separately
 * from `iOSApp.init()` (see `doInitKoinIos`), so this is purely the Compose host.
 */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
