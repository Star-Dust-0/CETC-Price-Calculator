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
# Android 11+ hides Android/data/<pkg> from adb shell; use run-as (debuggable build) + tar through exec-out
adb exec-out "run-as com.stardust.cetccalc tar -cf - -C /sdcard/Android/data/com.stardust.cetccalc/files ." > shots.tar || true
tar -xf shots.tar -C screenshots/ || true
find screenshots -type f -name '*.png' | sort || true

exit $TEST_EXIT
