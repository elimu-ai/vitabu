package ai.elimu.vitabu.util

import ai.elimu.model.v2.enums.ReadingLevel

fun ReadingLevel?.toSpeechRate(): Float {
    return when (this) {
        ReadingLevel.LEVEL1 -> 0.2f
        ReadingLevel.LEVEL2 -> 0.3f
        ReadingLevel.LEVEL3 -> 0.4f
        ReadingLevel.LEVEL4 -> 0.5f
        null -> 0.5f
    }
}