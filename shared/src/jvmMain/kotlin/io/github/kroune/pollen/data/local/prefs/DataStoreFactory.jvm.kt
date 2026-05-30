package io.github.kroune.pollen.data.local.prefs

import java.io.File

actual fun platformFilePath(fileName: String): String =
    File(System.getProperty("java.io.tmpdir"), fileName).absolutePath
