# SocialShield Android E2E Appium Test Automation Framework

This is a complete, enterprise-grade test automation framework designed for the SocialShield Android application. It implements the Page Object Model (POM) design pattern with TestNG, Java 21, Appium 2.x, Maven, Log4j2, ExtentReports, Apache POI, and OpenPDF to deliver automated testing and comprehensive reports.

---

## Technical Stack
* **Language**: Java 21
* **Build tool**: Apache Maven
* **Runner**: TestNG (7.10.x)
* **Mobile Driver**: Appium Java Client (9.2.x) targeting Appium Server 2.x
* **HTML Reporting**: ExtentReports (5.1.x)
* **Excel Reporting**: Apache POI (5.2.x)
* **PDF Reporting**: OpenPDF (1.3.x)
* **Logging**: Log4j2

---

## Directory Structure
```
/AppiumFramework
  ├─ pom.xml                      # Maven project definition & dependencies
  ├─ testng.xml                   # Test suite orchestration file
  ├─ config.properties            # Device capabilities & report locations configurations
  ├─ README.md                    # Execution & setup instructions
  ├─ logs/                        # Log4j file logs (appium_test.log)
  ├─ reports/                     # Automatically generated execution artifacts
  │   ├─ excelReports/            # Excel Worksheets detailing status & times
  │   ├─ pdfReports/              # Executive-facing PDF summary logs
  │   ├─ extent-reports/          # Spark HTML Interactive Dashboards (index.html)
  │   ├─ failedScreenshots/       # Failure-state capture PNGs
  │   ├─ deployment-readiness-report.json # JSON metrics output
  │   └─ defect-summary-report.txt       # Production-readiness verdict report
  └─ src/
      ├─ main/
      │   ├─ java/com/socialshield/testing/
      │   │   ├─ driver/
      │   │   │   └─ DriverManager.java      # Driver instantiation & configuration utility
      │   │   ├─ pages/                      # Page Object Classes
      │   │   │   ├─ BasePage.java           # Common interaction wrappers (swipes, waits)
      │   │   │   ├─ AuthPage.java           # Onboarding, email, and Google sign-in forms
      │   │   │   ├─ HomePage.java           # Security stats, charts, tabs, and feed
      │   │   │   ├─ ScanPage.java           # Media drop zones & Profile inputs
      │   │   │   ├─ ResultPage.java         # Classification scores & recommendations
      │   │   │   ├─ HistoryPage.java        # Search box, filters, and deletions
      │   │   │   └─ SettingsPage.java       # Theme switches, notifications, and signout
      │   │   └─ utils/                      # Helper engines
      │   │       ├─ ScreenshotUtil.java     # PNG capture generator
      │   │       ├─ ExtentManager.java      # HTML reporter config
      │   │       ├─ ExcelReporter.java      # POI sheet writer
      │   │       ├─ PdfReporter.java        # PDF document builder
      │   │       └─ DeploymentReporter.java # JSON/Text readiness verdict writer
      │   └─ resources/
      │       └─ log4j2.xml                  # Log4j configuration
      └─ test/
          └─ java/com/socialshield/testing/tests/
              ├─ BaseTest.java               # Lifecycle setups/teardowns & listeners
              ├─ TestListener.java           # TestNG execution tracking listener
              ├─ AuthTests.java              # TC001 - TC010 (Authentication & Auto-login)
              ├─ DashboardTests.java         # TC011 - TC030 (Widgets, graphs & counters)
              ├─ ProfileScanTests.java       # TC031 - TC055 (AI results & classification limits)
              ├─ HistoryTests.java           # TC056 - TC070 (Saved files search, filter & delete)
              ├─ ReportsTests.java           # TC071 - TC085 (Sync, export verification)
              ├─ SettingsTests.java          # TC086 - TC100 (Light/dark themes, switches)
              ├─ UiUxTests.java              # TC101 - TC125 (Spacing, alignment, contrast, tablet)
              ├─ FunctionalTests.java        # TC126 - TC150 (Navigation, database, REST APIs)
              └─ ValidationTests.java        # TC151 - TC170 (SQL injections, boundaries, mandates)
```

---

## Setup & Prerequisites

### 1. System Requirements
* Install **JDK 21** and ensure `JAVA_HOME` environment variable points to its installation path.
* Install **Apache Maven 3.9+** and add its `bin` directory to your system `PATH`.
* Install **Node.js v18+**.

### 2. Appium 2.x Installation
Install Appium globally via npm:
```bash
npm install -g appium
```
Install the UiAutomator2 driver:
```bash
appium driver install uiautomator2
```

### 3. Android Emulators / Real Devices
* Ensure an Android emulator or a physical device is connected.
* Verify via CLI command:
  ```bash
  adb devices
  ```
* Ensure you have compiled the debug APK. The default path points to:
  `C:/Users/sanat/Desktop/SocialShield App/SocialShield.apk`

---

## How to Execute the Suite

### 1. Start Appium Server
Start the server in a separate terminal:
```bash
appium
```
It will start on port `4723` by default.

### 2. Configure Settings (Optional)
Modify variables in `AppiumFramework/config.properties` if you need to customize server URLs, device names, or absolute APK paths:
```properties
app.path=C:/Users/sanat/Desktop/SocialShield App/SocialShield.apk
device.name=Android Emulator
```

### 3. Execute Maven CLI
Navigate into `/AppiumFramework` and run:
```bash
mvn clean test
```

---

## Automatically Generated Reports

Every time the TestNG execution completes, the framework outputs several detailed reports in separate directories:

1. **HTML Extent Dashboard Report**: Located at `reports/extent-reports/index.html`. It gives an interactive visual representation of executed test steps, durations, and embedded screenshots for failed assertions.
2. **Excel Report**: Located at `reports/excelReports/SocialShield_Test_Report.xlsx`. Captures `Test Case ID`, `Module`, `Status`, `Execution Time`, and `Defect ID`.
3. **PDF Report**: Located at `reports/pdfReports/SocialShield_Executive_Report.pdf`. A clean, executive-ready document featuring a high-level table summary and detailed execution logs.
4. **Deployment Readiness Verdict Report**: Written in JSON at `reports/deployment-readiness-report.json` and in raw text at `reports/defect-summary-report.txt`. Evaluates overall scores across modules and appends a final release verdict (`READY FOR PRODUCTION` vs `NOT READY FOR PRODUCTION`).
5. **Logger Outputs**: Text log logs are saved inside `logs/appium_test.log`.
