#!/usr/bin/env bash
# Runs instrumentation tests on the emulator, then ALWAYS pulls screenshots and logs.
# The android-emulator-runner executes each script line as a separate `sh -c`,
# so all state handling must live in this single file invocation.
set +e
gradle connectedDebugAndroidTest --stacktrace
TEST_EXIT=$?
echo "test exit: $TEST_EXIT"

echo '--- CETCShot/CETCTax logcat ---'
adb logcat -d -s CETCShot:I CETCTax:I || true

mkdir -p screenshots
# Android 11+ hides Android/data/<pkg> from adb shell; run-as (debuggable build) sees it
adb shell "run-as com.stardust.cetccalc ls -la /sdcard/Android/data/com.stardust.cetccalc/files/" || true
for f in $(adb shell "run-as com.stardust.cetccalc ls /sdcard/Android/data/com.stardust.cetccalc/files/" 2>/dev/null | tr -d '\r'); do
  adb exec-out "run-as com.stardust.cetccalc cat /sdcard/Android/data/com.stardust.cetccalc/files/$f" > "screenshots/$f" || true
done
find screenshots -type f -name '*.png' | sort || true

exit $TEST_EXIT
