import SwiftUI

@main
struct iOSApp: App {
    init() {
        if let path = Bundle.main.path(forResource: "Secrets", ofType: "plist") {
            print("✅ Secrets.plist found at: \(path)")
            if let dict = NSDictionary(contentsOfFile: path) {
                print("Contents:", dict)
            } else {
                print("❌ Could not read contents")
            }
        } else {
            print("❌ Secrets.plist not found")
        }

    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
