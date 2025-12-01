import {defineConfig, devices} from '@playwright/test';

export default defineConfig({
    testDir: './tests',
    fullyParallel: true,
    workers: process.env.CI ? 1 : 4,
    // Retry failed tests on CI
    retries: process.env.CI ? 2 : 0,

    // Reporter configuration
    reporter: [
        ['html', {outputFolder: 'tests/playwright-report'}],
        ['json', {outputFile: 'tests/playwright-report/results.json'}],
        ['list']
    ],
    timeout: 60000,
    expect: {
        timeout: 10000
    },

    use: {
        baseURL: 'http://localhost:5173',
        viewport: {width: 1920, height: 1080},
        trace: 'on-first-retry',
        screenshot: 'only-on-failure',
        video: 'on',
        actionTimeout: 10000,
        navigationTimeout: 30000,
        headless: true,
    },

    projects: [

        {
            name: 'chromium',
            use: {...devices['Desktop Chrome']},
        },
        {
            name: 'firefox',
            use: {...devices['Desktop Firefox']},
        },
        {
            name: 'webkit',
            use: {...devices['Desktop Safari']},
        },
    ],

    // starts the dev server before tests
    webServer: {
        command: 'npm run dev',
        url: 'http://localhost:5173',
        reuseExistingServer: !process.env.CI,
        timeout: 120000,
    },
});
