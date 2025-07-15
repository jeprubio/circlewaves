package com.rumosoft.circlewaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumosoft.circlewaves.ui.theme.CircleWavesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CircleWavesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DeviceMap(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DeviceMap(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ExpandingCirclesWithConfig()
        CategoriesGrid()
    }
}

data class ExpandingItemsConfig(
    val numberOfWaves: Int = 4,
    val lineWidth: Float = 1f,
    val delay: Long = 700,
    val expansionDurationMs: Int = 3500,
    val scale: Float = 6.0f,
    val colour: Color = Color(0xFF0B9CEA),
    val minOpacity: Float = 0.45f  // Changed to 45% opacity for outermost circle
)

@Composable
fun ExpandingCirclesWithConfig(config: ExpandingItemsConfig = ExpandingItemsConfig()) {
    val activeWaves = remember { mutableStateListOf<Int>() }
    val updatedConfig by rememberUpdatedState(config)
    val animationRunning = remember { mutableStateOf(true) }

    val screenRadius = with(LocalDensity.current) {
        min(
            LocalConfiguration.current.screenWidthDp.dp.toPx(),
            LocalConfiguration.current.screenHeightDp.dp.toPx()
        ) / 2
    }

    val targetRadius = screenRadius * 2.0f

    LaunchedEffect(Unit) {
        for (i in 0 until updatedConfig.numberOfWaves) {
            activeWaves.add(i)
            delay(updatedConfig.delay / 2)
        }

        val stopTime = (config.expansionDurationMs * 0.5f).toLong()
        delay(stopTime)
        animationRunning.value = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        activeWaves.forEach { index ->
            key(index) {
                ExpandingWave(
                    config = updatedConfig,
                    index = index,
                    isRunning = animationRunning.value,
                    targetRadius = targetRadius,
                    startDelay = index * (updatedConfig.delay / 2)
                )
            }
        }
    }
}

@Composable
fun ExpandingWave(
    config: ExpandingItemsConfig,
    index: Int,
    isRunning: Boolean,
    targetRadius: Float,
    startDelay: Long
) {
    val radius = remember { Animatable(0f) }
    val opacity = remember { Animatable(1f) }

    // Calculate target opacity - outermost (45%) to innermost (85%)
    val targetOpacity = 0.45f + (index.toFloat() / (config.numberOfWaves - 1).coerceAtLeast(1)) * 0.4f

    LaunchedEffect(Unit) {
        delay(startDelay)

        launch {
            radius.animateTo(
                targetValue = targetRadius,
                animationSpec = tween(
                    durationMillis = config.expansionDurationMs,
                    easing = LinearEasing
                )
            )
        }

        launch {
            opacity.animateTo(
                targetValue = targetOpacity,
                animationSpec = tween(
                    durationMillis = config.expansionDurationMs,
                    easing = LinearEasing
                )
            )
        }
    }

    LaunchedEffect(isRunning) {
        if (!isRunning) {
            radius.stop()
            opacity.stop()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = config.colour.copy(alpha = opacity.value),
            radius = radius.value,
            center = Offset(size.width / 2, size.height / 2),
            style = Stroke(width = config.lineWidth.dp.toPx())
        )
    }
}

@Composable
@Preview
fun WaveAnimationPreview() {
    CircleWavesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ExpandingCirclesWithConfig(config = ExpandingItemsConfig())
        }
    }
}
