package com.boringcactus.kmp.swiftmapperf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {
        var logContainerSize by remember { mutableIntStateOf(20) }
        var loading by remember { mutableStateOf(false) }
        var createDuration: Duration? by remember { mutableStateOf(null) }
        var listEveryAccessDuration: Duration? by remember { mutableStateOf(null) }
        var listOnceAccessDuration: Duration? by remember { mutableStateOf(null) }
        var listIndirectAccessDuration: Duration? by remember { mutableStateOf(null) }
        var mapEveryAccessDuration: Duration? by remember { mutableStateOf(null) }
        var mapOnceAccessDuration: Duration? by remember { mutableStateOf(null) }
        var mapIndirectAccessDuration: Duration? by remember { mutableStateOf(null) }
        Column(
            Modifier.background(MaterialTheme.colors.background).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("log₂(collection size)")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = logContainerSize.toFloat(),
                    onValueChange = { logContainerSize = it.roundToInt() },
                    modifier = Modifier.weight(1f),
                    steps = 9,
                    valueRange = 12f..20f
                )
                Text(
                    logContainerSize.toString(),
                    Modifier.width(32.dp)
                )
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    loading = true
                    createDuration = null
                    listEveryAccessDuration = null
                    listOnceAccessDuration = null
                    listIndirectAccessDuration = null
                    mapEveryAccessDuration = null
                    mapOnceAccessDuration = null
                    mapIndirectAccessDuration = null

                    val (data, time) = measureTimedValue { Data(logContainerSize) }
                    createDuration = time

                    listEveryAccessDuration = measureTime {
                        var totalLength = 0
                        for (i in 1..5) {
                            totalLength += data.list.getOrNull(i)?.length ?: 0
                        }
                    }

                    listOnceAccessDuration = measureTime {
                        val list = data.list
                        var totalLength = 0
                        for (i in 1..5) {
                            totalLength += list.getOrNull(i)?.length ?: 0
                        }
                    }

                    listIndirectAccessDuration = measureTime {
                        var totalLength = 0
                        for (i in 1..5) {
                            totalLength += data.getFromList(i)?.length ?: 0
                        }
                    }

                    mapEveryAccessDuration = measureTime {
                        var totalLength = 0
                        for (key in listOf("lorem", "ipsum", "dolor", "sit", "amet")) {
                            totalLength += data.map[key]?.length ?: 0
                        }
                    }

                    mapOnceAccessDuration = measureTime {
                        val map = data.map
                        var totalLength = 0
                        for (key in listOf("lorem", "ipsum", "dolor", "sit", "amet")) {
                            totalLength += map[key]?.length ?: 0
                        }
                    }

                    mapIndirectAccessDuration = measureTime {
                        var totalLength = 0
                        for (key in listOf("lorem", "ipsum", "dolor", "sit", "amet")) {
                            totalLength += data.getFromMap(key)?.length ?: 0
                        }
                    }

                    loading = false
                }
            }) { Text("Run Benchmark") }
            Row {
                Text("Create data", Modifier.weight(1f))
                DurationView(createDuration, loading)
            }
            Row {
                Text("Access list, getting every time", Modifier.weight(1f))
                DurationView(listEveryAccessDuration, loading)
            }
            Row {
                Text("Access list, getting once", Modifier.weight(1f))
                DurationView(listOnceAccessDuration, loading)
            }
            Row {
                Text("Access list, getting indirectly", Modifier.weight(1f))
                DurationView(listIndirectAccessDuration, loading)
            }
            Row {
                Text("Access map, getting every time", Modifier.weight(1f))
                DurationView(mapEveryAccessDuration, loading)
            }
            Row {
                Text("Access map, getting once", Modifier.weight(1f))
                DurationView(mapOnceAccessDuration, loading)
            }
            Row {
                Text("Access map, getting indirectly", Modifier.weight(1f))
                DurationView(mapIndirectAccessDuration, loading)
            }
        }
    }
}

@Composable
private fun DurationView(duration: Duration?, loading: Boolean) {
    when {
        duration != null -> Text(duration.toString())
        loading -> CircularProgressIndicator()
        else -> Text("-")
    }
}
