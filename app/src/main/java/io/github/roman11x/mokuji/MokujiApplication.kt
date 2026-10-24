package io.github.roman11x.mokuji

import android.app.Application

// Initialize the app container before the app's components start
class MokujiApplication : Application() {
    /**
     * The container is initialized using 'by lazy' to ensure it is only created
     * when first accessed. This is necessary because the Application's 'Context'
     * (represented by 'this') is not fully attached and ready for use until
     * after the constructor has finished and the system has initialized the base context.
     */
    val container by lazy { AppContainer(this) }
}