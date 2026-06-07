package com.routinealarm.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Color(0xFF1E88E5), // スタイリッシュなブルー
    onPrimary = Color.White,
    secondary = Color(0xFF00897B), // スタイリッシュなティール
    background = Color(0xFFEFF4F8), // カードとの差が出る淡いブルーグレー
    surface = Color(0xFFFFFFFF), // カード背景は白
    surfaceVariant = Color(0xFFE4EAF0),
    onBackground = Color(0xFF2C2C2C), // 濃いグレー（スタイリッシュ）
    onSurface = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFF58636E),
    outline = Color(0xFF9AA8B5),
    error = Color(0xFFD32F2F)
)

@Composable
fun RoutineAlarmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        content = content
    )
}
