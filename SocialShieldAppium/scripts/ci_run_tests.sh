#!/bin/bash
set -e

# Path configurations
APK_PATH="${APK_PATH:-../app-build/SocialShield.apk}"
APPIUM_PORT=4723

echo "Starting SocialShield Mobile E2E Test Execution Pipeline..."

# 1. Install APK on running emulator
echo "Installing APK from path: ${APK_PATH}..."
if adb install -r "${APK_PATH}"; then
    echo "APK installed successfully."
else
    echo "Warning: adb install failed. Trying to force install..."
    adb install -r -t -g "${APK_PATH}" || echo "Proceeding anyway..."
fi

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
