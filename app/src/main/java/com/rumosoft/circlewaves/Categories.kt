package com.rumosoft.circlewaves

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumosoft.circlewaves.ui.theme.CircleWavesTheme
import kotlinx.coroutines.launch

@Composable
fun CategoriesGrid() {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (router, computers, mobiles, amplifiers, tablets, tvs, consoles, others) = createRefs()

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.Gray, shape = CircleShape)
                .constrainAs(router) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Router", color = Color.White, fontSize = 14.sp)
        }

        val density = LocalDensity.current
        val routerCenterY = with(LocalConfiguration.current) {
            density.run { (screenHeightDp.dp / 2).toPx() }
        }

        val computersYOffset = -(routerCenterY / 2f)
        val mobilesYOffset = -(routerCenterY / 2.2f)
        val amplifiersYOffset = with(density) { 15.dp.toPx() }
        val amplifiersXOffset = with(density) { -120.dp.toPx() }
        val tabletsXOffset = with(density) { 120.dp.toPx() }
        val tabletsYOffset = with(density) { -15.dp.toPx() }
        val lineYOffset = routerCenterY / 2.5f
        val tvsXOffset = 0f
        val othersYOffset = with(density) { lineYOffset - 20.dp.toPx() }
        val consolesXOffset = with(density) { -100.dp.toPx() }
        val othersXOffset = with(density) { 100.dp.toPx() }

        AnimatedCategory(
            text = "Computers",
            xOffsetTarget = with(density) { -70.dp.toPx() },
            yOffsetTarget = computersYOffset,
            modifier = Modifier.constrainAs(computers) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.Blue
        )

        AnimatedCategory(
            text = "Mobiles",
            xOffsetTarget = with(density) { 70.dp.toPx() },
            yOffsetTarget = mobilesYOffset,
            modifier = Modifier.constrainAs(mobiles) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.Green
        )

        AnimatedCategory(
            text = "Amplifiers",
            xOffsetTarget = amplifiersXOffset,
            yOffsetTarget = amplifiersYOffset,
            modifier = Modifier.constrainAs(amplifiers) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.Red
        )

        AnimatedCategory(
            text = "Tablets",
            xOffsetTarget = tabletsXOffset,
            yOffsetTarget = tabletsYOffset,
            modifier = Modifier.constrainAs(tablets) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.Cyan
        )

        AnimatedCategory(
            text = "TVs",
            xOffsetTarget = tvsXOffset,
            yOffsetTarget = lineYOffset,
            modifier = Modifier.constrainAs(tvs) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.Magenta
        )

        AnimatedCategory(
            text = "Consoles",
            xOffsetTarget = consolesXOffset,
            yOffsetTarget = othersYOffset,
            modifier = Modifier.constrainAs(consoles) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.LightGray
        )

        AnimatedCategory(
            text = "Others",
            xOffsetTarget = othersXOffset,
            yOffsetTarget = othersYOffset,
            modifier = Modifier.constrainAs(others) {
                start.linkTo(router.start)
                end.linkTo(router.end)
                top.linkTo(router.top)
                bottom.linkTo(router.bottom)
            },
            color = Color.DarkGray
        )
    }
}

@Composable
fun AnimatedCategory(
    text: String,
    xOffsetTarget: Float,
    yOffsetTarget: Float,
    modifier: Modifier,
    color: Color
) {
    val density = LocalDensity.current

    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            xOffset.animateTo(
                targetValue = xOffsetTarget,
                animationSpec = tween(durationMillis = 1000)
            )
        }
        launch {
            yOffset.animateTo(
                targetValue = yOffsetTarget,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .offset(x = with(density) { xOffset.value.toDp() }, y = with(density) { yOffset.value.toDp() })
            .background(color, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
@Preview
fun CategoriesPreview() {
    CircleWavesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CategoriesGrid()
        }
    }
}

