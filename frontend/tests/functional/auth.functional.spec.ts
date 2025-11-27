import { test, expect } from '@playwright/test';
import { TestHelper } from '../helpers/test-utils';

/**
 * These test the authentication:
 * - Invalid credentials
 * - Maintain session between page 5 reloads
 */
test('should handle authentication with invalid credentials (single attempt)', async ({ page }) => {
    const helper = new TestHelper(page);
    const invalidEmail = "false@false.com";
    const invalidPassword = "invalid_password";
    let loginFailedAsExpected: boolean;
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');

    try {
        await helper.login(invalidEmail, invalidPassword, false);
        loginFailedAsExpected = false;

    } catch (_) {
        const isLoggedIn = await helper.isLoggedIn();
        // Success condition: login helper failed and the final state is NOT logged in.
        loginFailedAsExpected = !isLoggedIn;
        if (!loginFailedAsExpected) {
            console.error(`Unexpectedly logged in with invalid credentials: ${invalidEmail}`);
        }
    }
    expect(loginFailedAsExpected).toBe(true);
});

test('should maintain session across page reloads', async ({ page }) => {
    const helper = new TestHelper(page);
    const user = TestHelper.generateUser('session_test');
    await helper.register(user);
    expect(await helper.isLoggedIn()).toBe(true);

    // Reload page multiple times
    const reloads = 5;
    for (let i = 0; i < reloads; i++) {
        console.log(`Reload ${i + 1}/${reloads}`);
        await page.reload();
        await page.getByTestId("sidebar-dropdown-menu").waitFor({ state: 'visible' });
        // Should still be logged in after reload
        expect(await helper.isLoggedIn()).toBe(true);
    }
});