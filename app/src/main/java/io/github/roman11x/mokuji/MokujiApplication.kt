package io.github.roman11x.mokuji

import android.app.Application

// Initialize the app container before the app's components start
class MokujiApplication: Application() {
    val container = AppContainer()
}