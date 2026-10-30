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
# Screenshots are mirrored to the public Pictures/CETC-CI collection (visible to adb;
# the app itself is uninstalled when the test task finishes, so run-as is unavailable)
adb shell 'ls -la /sdcard/Pictures/CETC-CI/ 2>&1' || true
adb pull /sdcard/Pictures/CETC-CI/ screenshots/ || true
find screenshots -type f -name '*.png' | sort || true

exit $TEST_EXIT
