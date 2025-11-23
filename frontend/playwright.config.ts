import { defineConfig } from '@playwright/test';

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({

  testDir: './e2e',

  use: {
    baseURL: 'http://localhost:4200',
    trace: 'on-first-retry',
  },

  webServer: {
    command: 'npm start',
    url: 'http://localhost:4200',
    reuseExistingServer: !process.env.CI,
  },
});
