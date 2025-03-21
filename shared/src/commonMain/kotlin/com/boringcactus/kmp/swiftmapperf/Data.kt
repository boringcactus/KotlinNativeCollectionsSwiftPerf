package com.boringcactus.kmp.swiftmapperf

import kotlin.math.pow
import kotlin.math.roundToInt

data class Data(val list: List<String>, val map: Map<String, String>) {
    constructor(logSize: Int) : this(
        (1..(2.0.pow(logSize).roundToInt())).map { generate(1..8) },
        (1..(2.0.pow(logSize).roundToInt())).associate { generate(1..8) to generate(1..8) }
    )

    fun getFromList(index: Int) = list.getOrNull(index)
    fun getFromMap(key: String) = map[key]

    companion object {
        private val words = listOf("lorem", "ipsum", "dolor", "sit", "amet")

        private fun generate(wordsRange: IntRange) = buildString {
            for (i in 1..wordsRange.random()) {
                if (i > 1) {
                    append(' ')
                }
                append(words.random())
            }
        }
    }
}
