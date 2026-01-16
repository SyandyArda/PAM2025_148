package com.example.smartretail.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Period selector for filtering chart data
 */
enum class Period {
    DAILY,
    WEEKLY,
    MONTHLY
}

@Composable
fun PeriodSelector(
    selectedPeriod: Period,
    onPeriodSelected: (Period) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedPeriod == Period.DAILY,
            onClick = { onPeriodSelected(Period.DAILY) },
            label = { Text("Hari Ini") }
        )
        
        FilterChip(
            selected = selectedPeriod == Period.WEEKLY,
            onClick = { onPeriodSelected(Period.WEEKLY) },
            label = { Text("7 Hari") }
        )
        
        FilterChip(
            selected = selectedPeriod == Period.MONTHLY,
            onClick = { onPeriodSelected(Period.MONTHLY) },
            label = { Text("30 Hari") }
        )
    }
}
