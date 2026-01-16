package com.example.smartretail.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Enhanced Search Bar with Debounce
 * Delays search execution to reduce unnecessary queries
 */
@Composable
fun EnhancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    placeholder: String = "Cari produk...",
    modifier: Modifier = Modifier,
    debounceMillis: Long = 300
) {
    var searchText by remember { mutableStateOf(query) }
    
    // Debounce effect
    LaunchedEffect(searchText) {
        delay(debounceMillis)
        onSearch(searchText)
    }
    
    OutlinedTextField(
        value = searchText,
        onValueChange = {
            searchText = it
            onQueryChange(it)
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = {
                    searchText = ""
                    onQueryChange("")
                    onSearch("")
                }) {
                    Icon(Icons.Default.Clear, "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

/**
 * Debounced value helper
 * Returns debounced value after specified delay
 */
@Composable
fun <T> rememberDebounced(
    value: T,
    delayMillis: Long = 300
): T {
    var debouncedValue by remember { mutableStateOf(value) }
    
    LaunchedEffect(value) {
        delay(delayMillis)
        debouncedValue = value
    }
    
    return debouncedValue
}
