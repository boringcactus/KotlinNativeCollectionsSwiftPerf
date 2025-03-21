package com.boringcactus.kmp.swiftmapperf

import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

fun formatDuration(seconds: Long, attoseconds: Long): String {
    val femtoseconds = attoseconds / 1_000
    val picoseconds = femtoseconds / 1_000
    val nanoseconds = picoseconds / 1_000
    return (seconds.seconds + nanoseconds.nanoseconds).toString()
}
