package com.example.smartretail.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Helper untuk haptic feedback (getaran)
 * Memberikan tactile response saat user interact dengan UI
 */
object HapticHelper {
    
    fun performHapticFeedback(context: Context, type: HapticType = HapticType.CLICK) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            
            // Check if vibrator exists and has vibrator capability
            if (vibrator == null || !vibrator.hasVibrator()) {
                return
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticType.CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    HapticType.HEAVY_CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    HapticType.DOUBLE_CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {
            // Silently fail - haptic feedback is not critical
            android.util.Log.e("HapticHelper", "Failed to perform haptic feedback", e)
        }
    }
}

enum class HapticType {
    CLICK,          // Light tap (untuk button biasa)
    HEAVY_CLICK,    // Heavy tap (untuk critical actions)
    DOUBLE_CLICK    // Double tap (untuk success feedback)
}

/**
 * Composable helper untuk easy access ke haptic feedback
 */
@Composable
fun rememberHaptic(): (HapticType) -> Unit {
    val context = LocalContext.current
    return { type ->
        HapticHelper.performHapticFeedback(context, type)
    }
}
