package com.boringcactus.kmp.swiftmapperf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform