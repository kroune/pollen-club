package io.github.kroune.pollen.qr

import org.koin.dsl.module

val qrModule = module {
    single<QrScannerUi> { GmsQrScannerUi() }
}
