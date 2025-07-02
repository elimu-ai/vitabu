package ai.elimu.vitabu.util

import ai.elimu.model.v2.enums.ReadingLevel

fun ReadingLevel?.toSpeechRate(): Float {
    return when (this) {
        ReadingLevel.LEVEL1 -> 0.04f
        ReadingLevel.LEVEL2 -> 0.08f
        ReadingLevel.LEVEL3 -> 0.16f
        ReadingLevel.LEVEL4 -> 0.40f
        null -> 0.40f
    }
}