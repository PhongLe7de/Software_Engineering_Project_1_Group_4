import {test, expect} from '@playwright/test';
import {TestHelper, TestUser} from '../helpers/test-utils';

/**
 * Board Stress Tests
 *
 *  Concurrent Load - 10 Users
 *  TODO:
 *  Concurrent Load - 50 Users across 5 boards
 *  Drawing Performance - Single user rapid drawing
 */

test.describe('Board Stress Tests', () => {
    /**
     * - 10 users register → log in → access same board → draw simultaneously
     * - Pass criteria: Avg login <3s, error rate <1%
     */
    test('should handle 10 concurrent users accessing the same board', async ({browser}) => {
        const TEST_BOARD_NAME = `StressBoard-10Users-${Date.now()}`;
        const concurrentUsers = 10;
        const users: TestUser[] = [];

        // Generate test users
        for (let i = 0; i < concurrentUsers; i++) {
            users.push(TestHelper.generateUser(`stress_10_user_${i}_${Date.now()}`));
        }
        // Concurrent Registration
        const registrationPromises = users.map(async (user, index) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);

            try {
                console.log(`[User ${index}] Starting registration: ${user.username}`);
                await helper.register(user);
                console.log(`[User ${index}] Registration completed: ${user.username}`);
                return {success: true, user};
            } catch (error) {
                console.error(`[User ${index}] Registration failed:`, error);
                return {success: false, user, error};
            } finally {
                await context.close();
            }
        });
        await Promise.all(registrationPromises);

        // Create the board
        const firstUser = users[0];
        const setupContext = await browser.newContext();
        const setupPage = await setupContext.newPage();
        const setupHelper = new TestHelper(setupPage);

        console.log(`[Setup] Creating board with first user: ${firstUser.username}`);
        await setupHelper.login(firstUser.email, firstUser.password, true);
        await setupHelper.accessOrCreateBoard(TEST_BOARD_NAME);
        await setupContext.close();

        //  Concurrent Board Access and Drawing
        const boardAccessPromises = users.map(async (user, index) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);
            let responseTime = 0;

            try {
                console.log(`[User ${index}] Starting login and board access: ${user.username}`);
                await page.goto('/'); // DONT MEASURE NAV TIMES
                await page.waitForLoadState('domcontentloaded');

                // Measure: Login time + Board Load time (Required Avg < 3s)
                responseTime = await helper.measureResponseTime(async () => {
                    await helper.login(user.email, user.password, false);
                    await helper.accessOrCreateBoard(TEST_BOARD_NAME);
                    await page.waitForSelector('canvas', { timeout: 10000 });
                    await helper.waitForWebSocketConnection();
                });

                console.log(`[User ${index}] Login + Board Load completed in ${responseTime}ms`);

                // Draw on canvas
                await helper.drawOnCanvas(100 + index * 5, 100);

                return {success: true, index, user, responseTime};
            } catch (error) {
                console.error(`[User ${index}] Failed:`, error);
                return {success: false, index, user, error, responseTime};
            } finally {
                await context.close();
            }
        });

        const results = await Promise.all(boardAccessPromises);
        const successfulResults = results.filter(r => r.success);
        const successCount = successfulResults.length;

        // Calculate average response time
        const avgResponseTime = successfulResults
            .reduce((sum, r) => sum + (r.responseTime || 0), 0) / (successCount || 1);

        console.log(`Successfully accessed board: ${successCount}/${concurrentUsers} users`);
        console.log(`Average Login + Board Load time: ${avgResponseTime.toFixed(2)}ms`);

        // Acceptance Criteria:
        // Avg login <3s
        expect(avgResponseTime).toBeLessThan(3000);
        // 10 out of 10 users must succeed
        expect(successCount).toBe(concurrentUsers);
    });

});
