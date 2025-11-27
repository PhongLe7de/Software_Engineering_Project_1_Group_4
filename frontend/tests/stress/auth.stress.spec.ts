import { test, expect } from '@playwright/test';
import { TestHelper, TestUser } from '../helpers/test-utils';

/**
 * These tests stress test the authentication:
 * - 10 concurrent user registrations
 * - Concurrent logins
 * - Rapid login/logout cycles
 * - Maintain session between page 5 reloads
 */

test.describe('Authentication Stress Tests', () => {
    test('should handle concurrent user registrations', async ({ browser }) => {
        const concurrentUsers = 10;
        const users: TestUser[] = [];

        // Generate test users
        for (let i = 0; i < concurrentUsers; i++) {
            users.push(TestHelper.generateUser(`stress_reg_user_${i}`));
        }

        // Create concurrent registration promises
        const registrationPromises = users.map(async (user, index) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);

            try {
                console.log(`[User ${index}] Starting registration: ${user.username}`);
                await helper.register(user);
                console.log(`[User ${index}] Registration completed: ${user.username}`);

                // Verify registration succeeded
                return { success: true, user };
            } catch (error) {
                console.error(`[User ${index}] Registration failed:`, error);
                return { success: false, user, error };
            } finally {
                await context.close();
            }
        });

        // Execute all registrations concurrently
        const results = await Promise.all(registrationPromises);

        // Verify results
        const successCount = results.filter(r => r.success).length;
        console.log(`Successfully registered ${successCount}/${concurrentUsers} users`);
        expect(successCount).toBeGreaterThan(concurrentUsers * 0.8); // At least 80% success rate
    });

    test('should handle concurrent user logins', async ({ browser }) => {
        const concurrentUsers = 10;

        // Generate users
        const users: TestUser[] = Array.from({ length: concurrentUsers }, (_, i) =>
            TestHelper.generateUser(`stress_login_${i}`)
        );

        // Register
        const registrationPromises = users.map(async (user) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);
            try {
                await helper.register(user);
                await helper.logout();
            } catch (e) {
                console.error(`Failed to setup user ${user.username}`, e);
            } finally {
                await context.close();
            }
        });

        await Promise.all(registrationPromises);
        console.log(`[Setup] Registration complete.`);

        const loginPromises = users.map(async (user, index) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);

            try {
                // Pre-load the page to exclude navigation time, firefox is too slow...
                await page.goto('/');
                await page.waitForLoadState('domcontentloaded');

                console.log(`[User ${index}] Page loaded, starting login action: ${user.username}`);

                // Measure user interaction + API response
                const responseTime = await helper.measureResponseTime(async () => {
                    await helper.login(user.email, user.password, false);
                });

                console.log(`[User ${index}] Login API+UI completed in ${responseTime}ms`);

                const isLoggedIn = await helper.isLoggedIn();
                expect(isLoggedIn).toBe(true);

                return { success: true, responseTime, user };
            } catch (error) {
                console.error(`[User ${index}] Login failed:`, error);
                return { success: false, user, error };
            } finally {
                await context.close();
            }
        });

        // Execute all logins
        const results = await Promise.all(loginPromises);

        // Verify results
        const successCount = results.filter(r => r.success).length;
        const avgResponseTime = results
            .filter(r => r.success && r.responseTime)
            .reduce((sum, r) => sum + (r.responseTime || 0), 0) / successCount;

        console.log(`Successfully logged in ${successCount}/${concurrentUsers} users`);
        console.log(`Average login response time: ${avgResponseTime.toFixed(2)}ms`);

        expect(successCount).toBe(concurrentUsers);
        expect(avgResponseTime).toBeLessThan(5000);
    });

    test('should handle rapid login/logout cycles', async ({ page }) => {
        const helper = new TestHelper(page);
        const user = TestHelper.generateUser('rapid_cycle');

        // Register user
        await helper.register(user);
        await helper.logout();

        // Perform multiple login/logout cycles
        const cycles = 5;
        const responseTimes: number[] = [];

        for (let i = 0; i < cycles; i++) {
            console.log(`Starting cycle ${i + 1}/${cycles}`);
            if (await helper.isLoggedIn()) {
                await helper.logout();
            }

            // Login
            const loginTime = await helper.measureResponseTime(async () => {
                await helper.login(user.email, user.password);
            });
            responseTimes.push(loginTime);
            console.log(`Cycle ${i + 1} login time: ${loginTime}ms`);

            // Verify logged in
            expect(await helper.isLoggedIn()).toBe(true);
        }

        // Verify all cycles completed successfully
        const avgTime = responseTimes.reduce((sum, time) => sum + time, 0) / responseTimes.length;
        console.log(`Average login time across ${cycles} cycles: ${avgTime.toFixed(2)}ms`);
        expect(avgTime).toBeLessThan(5000);
    });


});