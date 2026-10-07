package io.github.roman11x.mokuji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import io.github.roman11x.mokuji.ui.theme.MokujiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MokujiTheme {
                MokujiApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
