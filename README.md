# RiderWise AI v2.0

Fresh Java Android Studio project for OPPO Reno 5 / ColorOS.

## Implemented in this source baseline
- Android MediaProjection consent flow and foreground capture service.
- Five-second target OCR scheduler with a single in-flight OCR guard.
- Google ML Kit text recognition.
- Initial Lalamove booking-card extraction for pickup, drop-off, fare, and visible pickup km.
- SQLite persistence with booking fingerprints.
- Offline city/area centroid dataset and approximate geographic compatibility heuristic.
- Combination enumeration up to 7 bookings (reference + up to 6 candidates).
- Compact read-only floating assistant.
- No automatic booking acceptance.

## Important implementation status
This is a source baseline, not a device-certified release. The OCR extractor is heuristic and needs validation against multiple real Lalamove screen states. Geographic coordinates are approximate sample centroids and must be expanded/verified before operational reliance. The recommendation engine currently ranks combinations using per-candidate reference-route compatibility; it does not yet solve a full pickup/drop-off precedence graph or real road routing. The floating assistant is read-only and should be reviewed only while safely parked.

## Build
1. Open this folder in Android Studio.
2. Install Android SDK Platform 35 and Android SDK Build Tools.
3. Use JDK 17.
4. Allow Gradle sync to download Gradle 8.9 and dependencies.
5. Build > Make Project, then Build > Build Bundle(s) / APK(s) > Build APK(s).

The Gradle wrapper properties are included. If Android Studio asks to generate wrapper scripts, accept or run `gradle wrapper` from a local Gradle installation.

## Permissions
- Screen capture: approved through Android's MediaProjection system dialog.
- Overlay: enabled by the user through Android settings.
- Foreground service: declared for media projection.
- Notification permission: Android may request it depending on OS version and target behavior.

ColorOS may restrict background operation. Follow OPPO's supported battery/background settings; no restriction bypass is implemented.

## Testing checklist
- Verify screen capture consent and revocation.
- Test OCR against the supplied Lalamove UI and different scrolling/card states.
- Test duplicate booking behavior and fare parsing (`₱184.00 + ₱34.00`).
- Verify known and unknown area handling.
- Test overlay permission denial/regrant.
- Test service interruption and memory behavior.
- Test on the actual OPPO Reno 5 before claiming device compatibility.

## Known limitations
- No Google Maps Directions API or exact road route.
- OCR segmentation and fare-component interpretation are preliminary.
- The sample geographic dataset is not exhaustive.
- Earnings/hour and fuel-cost outputs require reliable route distance, duration, fuel price, and motorcycle economy inputs; no estimates are fabricated.
- Android may stop screen capture when permission is revoked, the service is stopped, or system policy intervenes.
