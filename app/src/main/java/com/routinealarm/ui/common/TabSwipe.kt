package com.routinealarm.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.tabLabelSwipe(
    pagerState: PagerState,
    pageCount: Int,
    coroutineScope: CoroutineScope
): Modifier = pointerInput(pagerState, pageCount) {
    awaitEachGesture {
        val down = awaitFirstDown(
            requireUnconsumed = false,
            pass = PointerEventPass.Initial
        )
        var pointerId = down.id
        var totalDragX = 0f
        var totalDragY = 0f
        var isHorizontalDrag = false

        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val change = event.changes.firstOrNull { it.id == pointerId }
                ?: event.changes.firstOrNull()
                ?: break

            pointerId = change.id
            if (change.changedToUpIgnoreConsumed()) break

            val dragAmount = change.positionChange()
            totalDragX += dragAmount.x
            totalDragY += dragAmount.y

            if (!isHorizontalDrag) {
                isHorizontalDrag =
                    abs(totalDragX) > viewConfiguration.touchSlop &&
                    abs(totalDragX) > abs(totalDragY)
            }

            if (isHorizontalDrag) {
                change.consume()
            }
        }

        val threshold = size.width * 0.10f
        if (isHorizontalDrag && abs(totalDragX) >= threshold) {
            val targetPage = if (totalDragX < 0) {
                (pagerState.currentPage + 1).coerceAtMost(pageCount - 1)
            } else {
                (pagerState.currentPage - 1).coerceAtLeast(0)
            }

            if (targetPage != pagerState.currentPage) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(targetPage)
                }
            }
        }
    }
}
