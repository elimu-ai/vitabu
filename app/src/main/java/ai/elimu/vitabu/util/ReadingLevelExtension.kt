package ai.elimu.vitabu.util

import ai.elimu.model.v2.enums.ReadingLevel

fun ReadingLevel?.toSpeechRate(): Float {
    return when (this) {
        ReadingLevel.LEVEL1 -> 0.4f
        ReadingLevel.LEVEL2 -> 0.6f
        ReadingLevel.LEVEL3 -> 0.8f
        ReadingLevel.LEVEL4 -> 1.0f
        null -> 1.0f
    }
}