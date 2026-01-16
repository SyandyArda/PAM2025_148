package com.example.smartretail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Stock Level Badge Component
 * Shows stock status with color coding
 */
@Composable
fun StockLevelBadge(stock: Int) {
    val (color, text) = when {
        stock == 0 -> Color(0xFFF44336) to "HABIS"
        stock <= 10 -> Color(0xFFFF9800) to "RENDAH"
        else -> Color(0xFF4CAF50) to "TERSEDIA"
    }
    
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Stock Level Indicator with Progress Bar
 * Visual representation of stock level
 */
@Composable
fun StockLevelIndicator(
    stock: Int,
    modifier: Modifier = Modifier,
    maxStock: Int = 100
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { (stock.toFloat() / maxStock).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                stock == 0 -> Color(0xFFF44336)
                stock <= 10 -> Color(0xFFFF9800)
                else -> Color(0xFF4CAF50)
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        // Stock number
        Text(
            text = "$stock",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = when {
                stock == 0 -> Color(0xFFF44336)
                stock <= 10 -> Color(0xFFFF9800)
                else -> Color(0xFF4CAF50)
            }
        )
    }
}
