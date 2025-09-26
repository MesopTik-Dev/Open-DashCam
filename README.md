# Open DashCam
OpenDashcam is an open-source Android app that turns old smartphones into smart dashcams.  
It supports loop recording, storage management, and event protection.  
Future releases will add ADAS (collision and lane departure warnings) using on-device AI.

## Features
- Continuous loop recording (TODO in MVP)
- Stable background recording via ForegroundService
- Protect/lock clips manually or via G-sensor
- Timestamp and GPS overlays (planned)
- ADAS features (planned)

## Build Instructions
1. Clone repo  
2. Open in Android Studio (latest)  
3. Run `./gradlew assembleDebug`  
4. Install on a device (Android 8.0+)

## License
Apache 2.0
