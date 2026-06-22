const fs = require('fs');
const path = require('path');

function generateSummary(jsonlPath) {
    if (!fs.existsSync(jsonlPath)) {
        console.warn('Results file not found for summary generation:', jsonlPath);
        return;
    }
    
    let results = [];
    try {
        const content = fs.readFileSync(jsonlPath, 'utf8');
        results = content.trim().split('\n').filter(line => line).map(line => JSON.parse(line));
    } catch (e) {
        console.error('Failed to parse results JSONL:', e);
        return;
    }
    
    const totalTests = results.length;
    const passedTests = results.filter(t => t.status === 'PASSED').length;
    const failedTests = totalTests - passedTests;
    const passRate = totalTests > 0 ? (passedTests / totalTests * 100).toFixed(2) : '0.00';
    const totalDurationMs = results.reduce((acc, t) => acc + t.duration, 0);
    const durationSeconds = (totalDurationMs / 1000).toFixed(2);
    
    const summaryMd = `
### 📊 SocialShield Mobile E2E Test Execution Summary

| Metric | Value | Status |
| :--- | :---: | :---: |
| **Total Test Cases** | ${totalTests} | 📋 |
| **Passed Tests** | ${passedTests} | ✅ |
| **Failed Tests** | ${failedTests} | ${failedTests > 0 ? '❌' : '➖'} |
| **Pass Rate** | **${passRate}%** | ${parseFloat(passRate) >= 100.0 ? '🏆' : '⚠️'} |
| **Total Duration** | ${durationSeconds} s | ⏱️ |

*Report generated at ${new Date().toLocaleString()}*
`;
    
    const summaryFile = process.env.GITHUB_STEP_SUMMARY;
    if (summaryFile) {
        fs.appendFileSync(summaryFile, summaryMd, 'utf8');
        console.log('Appended test summary to GHA summary file.');
    } else {
        console.log('GITHUB_STEP_SUMMARY environment variable not set. Print summary to console:');
        console.log(summaryMd);
    }
}

if (require.main === module) {
    const defaultJsonl = path.join(__dirname, '..', '.wdio-results.jsonl');
    generateSummary(defaultJsonl);
}

module.exports = {
    generateSummary
};
