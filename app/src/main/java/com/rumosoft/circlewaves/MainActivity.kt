package com.rumosoft.circlewaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
    val lineWidth: Float = 1.5f,
    val delay: Long = 500,
    val expansionDurationMs: Int = 3500,
    val scale: Float = 6.0f,
    val colour: Color = Color(0xFF0B9CEA),
)

@Composable
fun ExpandingCirclesWithConfig(config: ExpandingItemsConfig = ExpandingItemsConfig()) {
    val activeWaves = remember { mutableStateListOf<Int>() }
    val updatedConfig by rememberUpdatedState(config)

    LaunchedEffect(Unit) {
        for (i in 0 until updatedConfig.numberOfWaves) {
            delay(updatedConfig.delay)
            activeWaves.add(i)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(activeWaves.size) { index ->
            key(index) {
                ExpandingWave(config = updatedConfig)
            }
        }
    }
}

@Composable
fun ExpandingWave(config: ExpandingItemsConfig) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Transition")
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = maxRadius(config),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.expansionDurationMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "Animated Radius"
    )
    val animatedOpacity by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.expansionDurationMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "Animated Opacity"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            color = config.colour.copy(alpha = animatedOpacity),
            radius = animatedRadius,
            center = center,
            style = Stroke(width = config.lineWidth.dp.toPx())
        )
    }
}

@Composable
private fun maxRadius(config: ExpandingItemsConfig): Float {
    val baseRadius = with(LocalDensity.current) {
        min(
            LocalConfiguration.current.screenWidthDp.dp.toPx(),
            LocalConfiguration.current.screenHeightDp.dp.toPx()
        ) / 2
    }
    return baseRadius * config.scale
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
