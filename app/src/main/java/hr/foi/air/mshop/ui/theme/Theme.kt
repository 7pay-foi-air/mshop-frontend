package hr.foi.air.mshop.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    secondary = RedSecondary,
    tertiary = RedTertiary,

    background = Color(0xFF0F0E0E),
    surface = Color(0xFF141212),
    surfaceVariant = Color(0xFF1F1B1B),
    outline = Color(0xFF3A2F2F),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFF3EDE7),
    onSurface = Color(0xFFF3EDE7),
    onSurfaceVariant = Color(0xFFF3EDE7).copy(alpha = 0.80f),
)

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    secondary = RedSecondary,
    tertiary = RedTertiary,

    background = WarmBackground,
    surface = WarmBackground,
    surfaceVariant = RosyInput,
    outline = WarmOutline,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = OnWarm,
    onBackground = OnWarm,
    onSurface = OnWarm,
    onSurfaceVariant = OnWarm.copy(alpha = 0.75f),
)

@Composable
fun MShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}