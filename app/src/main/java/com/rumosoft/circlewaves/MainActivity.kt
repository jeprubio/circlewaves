package com.rumosoft.circlewaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
    val refreshKey = remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { refreshKey.intValue++ }
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            key(refreshKey.intValue) {
                ExpandingCirclesWithConfig(
                    config = ExpandingItemsConfig(
                        disableAnimation = false,
                    )
                )
            }
        }
    }
}

data class ExpandingItemsConfig(
    val numberOfWaves: Int = 4,
    val lineWidth: Float = 1f,
    val expansionDurationMs: Int = 800,
    val scale: Float = 6.0f,
    val colour: Color = Color(0xFF0B9CEA),
    val minOpacity: Float = 0.45f,
    val disableAnimation: Boolean = false,
)

private const val INITIAL_STABILIZATION_DELAY = 100L

@Composable
fun ExpandingCirclesWithConfig(config: ExpandingItemsConfig = ExpandingItemsConfig()) {
    val activeWaves = remember { mutableStateListOf<Int>() }
    val updatedConfig by rememberUpdatedState(config)
    val animationFinished = remember { mutableStateOf(false) }

    val screenRadius = with(LocalDensity.current) {
        min(
            LocalConfiguration.current.screenWidthDp.dp.toPx(),
            LocalConfiguration.current.screenHeightDp.dp.toPx()
        ) / 2
    }
    val targetRadius = screenRadius * 1.80f

    LaunchedEffect(Unit) {
        if (updatedConfig.disableAnimation) {
            repeat(updatedConfig.numberOfWaves) { activeWaves.add(it) }
            animationFinished.value = true
        } else {
            delay(INITIAL_STABILIZATION_DELAY)

            repeat(updatedConfig.numberOfWaves) { activeWaves.add(it) }

            delay(config.expansionDurationMs.toLong())
            animationFinished.value = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        activeWaves.forEach { index ->
            key(index) {
                ExpandingWave(
                    config = updatedConfig,
                    index = index,
                    targetRadius = targetRadius,
                    startDelay = 0
                )
            }
        }
    }
}

@Composable
fun ExpandingWave(
    config: ExpandingItemsConfig,
    index: Int,
    targetRadius: Float,
    startDelay: Long
) {
    val targetOpacity = 0.85f - (index.toFloat() / (config.numberOfWaves - 1).coerceAtLeast(1)) * 0.55f

    val minRadius = targetRadius * 0.33f
    val waveTargetRadius = if (config.numberOfWaves > 1) {
        minRadius + (index.toFloat() / (config.numberOfWaves - 1)) * (targetRadius - minRadius)
    } else {
        targetRadius
    }

    val animationStarted = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!config.disableAnimation) {
            delay(startDelay)
            animationStarted.value = true
        }
    }

    val radius by animateFloatAsState(
        targetValue = if (config.disableAnimation) waveTargetRadius
        else if (!animationStarted.value) 0f
        else waveTargetRadius,
        animationSpec = tween(
            durationMillis = config.expansionDurationMs,
            easing = androidx.compose.animation.core.EaseInOutBack
        ),
        label = "radius"
    )

    val opacity by animateFloatAsState(
        targetValue = if (config.disableAnimation) targetOpacity
        else if (!animationStarted.value) 1f
        else targetOpacity,
        animationSpec = tween(
            durationMillis = config.expansionDurationMs,
            easing = androidx.compose.animation.core.EaseInOutBack
        ),
        label = "opacity"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = config.colour.copy(alpha = opacity),
            radius = radius,
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
