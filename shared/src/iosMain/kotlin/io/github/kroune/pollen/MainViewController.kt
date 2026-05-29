package io.github.kroune.pollen

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kroune.pollen.di.initKoinIos
import platform.UIKit.UIViewController

/**
 * Starts Koin exactly once, on first access, before any composable resolves a dependency.
 * `by lazy` guards against a double `startKoin` if the controller is recreated.
 */
private val koinInitialized: Boolean by lazy {
    initKoinIos()
    true
}

/**
 * Entry point consumed by the iOS app (`ContentView.swift`) via
 * `MainViewControllerKt.MainViewController()`.
 */
fun MainViewController(): UIViewController {
    koinInitialized
    return ComposeUIViewController { App() }
}
