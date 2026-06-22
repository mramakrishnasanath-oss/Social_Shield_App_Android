const fs = require('fs');
const path = require('path');
const { startRun, recordTest, generateReport } = require('./xlsxReporter');
const { generateHtmlReport } = require('./generateHtmlReport');
const { generateSummary } = require('./generateSummary');

const RESULTS_FILE = path.join(__dirname, '..', '.wdio-results.jsonl');
const reportsDir = path.join(__dirname, '..', 'reports');
const excelPath = path.join(reportsDir, 'SocialShield_Test_Report.xlsx');
const htmlPath = path.join(reportsDir, 'execution-report.html');

console.log('WDIO execution failed prematurely. Writing fallback reports...');

if (!fs.existsSync(reportsDir)) {
    fs.mkdirSync(reportsDir, { recursive: true });
}

const fallbackRecord = {
    name: 'Fatal Suite Execution Failure',
    category: 'E2E',
    duration: 100,
    status: 'FAILED',
    error: 'The mobile testing suite exited early due to a driver or compilation crash. Verify emulator setup and local server logs.',
    timestamp: new Date().toISOString()
};

fs.writeFileSync(RESULTS_FILE, JSON.stringify(fallbackRecord) + '\n', 'utf8');

startRun();
recordTest(fallbackRecord);
generateReport(excelPath);

generateHtmlReport(RESULTS_FILE, htmlPath);

generateSummary(RESULTS_FILE);

console.log('Fallback reports generated successfully.');
