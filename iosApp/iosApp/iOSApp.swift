import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        // Kotlin's initKoinIos() is exposed to Swift as doInitKoinIos():
        // Kotlin/Native renames functions whose names start with "init" to
        // avoid clashing with the Objective-C initializer family.
        IosKoinModuleKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
