#!/bin/bash
set -e

# Path configurations
APK_PATH="${APK_PATH:-../app-build/SocialShield.apk}"
APPIUM_PORT=4723
ADB="${ANDROID_HOME}/platform-tools/adb"

echo "Starting SocialShield Mobile E2E Test Execution Pipeline..."

# Helper: dismiss any visible dialog or system popup
dismiss_dialogs() {
    echo "[dismiss] Clearing any blocking dialogs..."
    # Press BACK key three times to clear dialogs
    $ADB shell input keyevent 4  2>/dev/null || true
    sleep 0.5
    $ADB shell input keyevent 4  2>/dev/null || true
    sleep 0.5
    # Press HOME key to go to launcher
    $ADB shell input keyevent 3  2>/dev/null || true
    sleep 0.5
    # Tap "OK" / "Close" button at common dialog positions
    # For a 1080x2340 screen, typical dialog button is around (540, 1400)
    $ADB shell input tap 540 1400 2>/dev/null || true
    sleep 0.3
    # Also try bottom center for AlertDialog buttons (540, 1600)
    $ADB shell input tap 540 1600 2>/dev/null || true
    sleep 0.3
}

# 1. Dismiss any pre-existing dialogs before install
dismiss_dialogs

# 2. Install APK on running emulator (using -t to allow test APK, -g to grant all permissions)
echo "Installing APK from path: ${APK_PATH}..."
# Run install in background so we can dismiss dialogs while it waits
$ADB install -r -t -g "${APK_PATH}" &
INSTALL_PID=$!

# Dismiss dialogs in parallel during install (for up to 60s)
for i in {1..12}; do
    sleep 5
    if ! kill -0 $INSTALL_PID 2>/dev/null; then
        break
    fi
    dismiss_dialogs
done

# Wait for install to finish
if wait $INSTALL_PID; then
    echo "APK installed successfully."
else
    echo "Warning: Primary install failed. Retrying with uninstall first..."
    $ADB uninstall com.socialshield 2>/dev/null || true
    dismiss_dialogs
    $ADB install -t -g "${APK_PATH}" || echo "APK install failed — proceeding anyway..."
fi

# Final dialog clear after install
dismiss_dialogs
sleep 2

# 2. Start Appium Server in background
echo "Starting Appium server on port ${APPIUM_PORT}..."
npm install -g appium || echo "Appium global installation skipped or already present."
appium driver install uiautomator2 || echo "UiAutomator2 driver installation skipped or already present."
appium --log-level warn > /tmp/appium.log 2>&1 &
APPIUM_PID=$!
echo "Appium started with PID: ${APPIUM_PID}"

# 3. Wait for Appium to be responsive
echo "Waiting for Appium service to respond..."
for i in {1..30}; do
    if curl -s "http://localhost:${APPIUM_PORT}/" > /dev/null; then
        echo "Appium server is active and responding."
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Error: Appium failed to start or respond on port ${APPIUM_PORT} within 30 seconds."
        exit 1
    fi
    sleep 1
done

# 4. Resolve Node.js path from GitHub path
if [ -n "${GITHUB_PATH}" ] && [ -f "${GITHUB_PATH}" ]; then
    echo "Sourcing paths from GITHUB_PATH..."
    while read -r path_line; do
        if [ -n "${path_line}" ]; then
            export PATH="${path_line}:${PATH}"
        fi
    done < "${GITHUB_PATH}"
fi

# 5. Run tests inside Appium folder
cd "$(dirname "$0")/.."
echo "Installing Node dependencies..."
npm install --no-audit --no-fund

echo "Running WDIO test runner..."
set +e
node node_modules/@wdio/cli/bin/wdio.js run wdio.conf.js
WDIO_EXIT_CODE=$?
set -e

echo "WDIO execution completed with exit code: ${WDIO_EXIT_CODE}"

# 6. Fallback report generation if tests failed prematurely
if [ ${WDIO_EXIT_CODE} -ne 0 ]; then
    if [ ! -f "reports/SocialShield_Test_Report.xlsx" ] || [ ! -f "reports/execution-report.html" ]; then
        echo "No execution reports found. Generating failure fallback logs..."
        node utils/generateFallbackReport.js
    fi
fi

# Cleanup
echo "Cleaning up background processes..."
kill ${APPIUM_PID} || true

exit ${WDIO_EXIT_CODE}
