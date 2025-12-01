import { test, expect } from '@playwright/test';
import { TestHelper } from '../helpers/test-utils';

/**
 * Board Stress Tests
 *
 * Tests concurrent user load and drawing performance under stress conditions.
 */

test.describe('Board Stress Tests', () => {

    /**
     * Concurrent Load - 10 Users on Single Board
     *
     * Pass criteria:
     * - Average login time < 3s
     * - 100% success rate (all 10 users must connect)
     */
    test('should handle 10 concurrent users accessing the same board', async ({ browser }) => {
        const TEST_BOARD_NAME = `StressBoard-10Users-${Date.now()}`;
        const CONCURRENT_USERS = 10;

        // Generate test users
        const users = Array.from({ length: CONCURRENT_USERS }, (_, i) =>
            TestHelper.generateUser(`stress_10_user_${i}_${Date.now()}`)
        );

        // Concurrent registration
        await Promise.all(users.map(async (user, index) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);

            try {
                console.log(`[User ${index}] Registering: ${user.username}`);
                await helper.register(user);
                console.log(`[User ${index}] Registration complete`);
            } catch (error) {
                console.error(`[User ${index}] Registration failed:`, error);
            } finally {
                await context.close();
            }
        }));

        // Create the shared board
        const setupContext = await browser.newContext();
        const setupPage = await setupContext.newPage();
        const setupHelper = new TestHelper(setupPage);

        console.log(`[Setup] Creating board: ${TEST_BOARD_NAME}`);
        await setupHelper.login(users[0].email, users[0].password, true);
        await setupHelper.accessOrCreateBoard(TEST_BOARD_NAME);
        await setupContext.close();

        // Concurrent board access and drawing
        const results = await Promise.all(users.map(async (user, index) => {
            const context = await browser.newContext();
            const page = await context.newPage();
            const helper = new TestHelper(page);

            try {
                // Pre-navigate (don't measure navigation time)
                await page.goto('/');
                await page.waitForLoadState('domcontentloaded');

                // Measure login time only
                const responseTime = await helper.measureResponseTime(async () => {
                    await helper.login(user.email, user.password, false);
                });

                await helper.accessOrCreateBoard(TEST_BOARD_NAME);
                await page.waitForSelector('canvas', { timeout: 10000 });
                await helper.waitForWebSocketConnection();

                console.log(`[User ${index}] Login completed in ${responseTime}ms`);

                // Draw on canvas (offset by index to spread strokes)
                await helper.drawOnCanvas(100 + index * 5, 100);

                return { success: true, responseTime };
            } catch (error) {
                console.error(`[User ${index}] Failed:`, error);
                return { success: false, responseTime: 0 };
            } finally {
                await context.close();
            }
        }));

        // ANALYSIS
        const successful = results.filter(r => r.success);
        const avgResponseTime = TestHelper.average(successful.map(r => r.responseTime));

        console.log(`Success: ${successful.length}/${CONCURRENT_USERS} users`);
        console.log(`Average login time: ${avgResponseTime.toFixed(2)}ms`);

        // ASSERTIONS
        expect(avgResponseTime).toBeLessThan(3000);
        expect(successful.length).toBe(CONCURRENT_USERS);
    });


    /**
     * Drawing Performance - Single User Rapid Input
     *
     * Measures local render latency (mouse input → React state → DOM update).
     * Does NOT measure backend round-trip time.
     *
     * Pass criteria:
     * - Average render time < 50ms
     * - Zero dropped strokes
     */
    test('should handle rapid drawing by single user', async ({ page }) => {
        const helper = new TestHelper(page);
        const user = TestHelper.generateUser(`rapid_draw_${Date.now()}`);
        const STROKE_COUNT = 50;
        const STROKE_INTERVAL_MS = 50;
        const TEST_BOARD_NAME = `RapidDrawBoard-${Date.now()}`;

        // Setup: register and navigate to fresh board
        await helper.register(user);
        await helper.accessOrCreateBoard(TEST_BOARD_NAME);
        await page.waitForSelector('[data-testid="board-canvas"]', { timeout: 10000 });
        await helper.waitForWebSocketConnection();

        console.log(`Drawing ${STROKE_COUNT} strokes at ${STROKE_INTERVAL_MS}ms intervals`);

        // Draw strokes and collect timing data
        const strokeTimes: number[] = [];
        let droppedStrokes = 0;

        for (let i = 0; i < STROKE_COUNT; i++) {
            try {
                const renderTime = await helper.drawAndMeasureRender();
                strokeTimes.push(renderTime);

                if (renderTime > 100) {
                    console.warn(`Stroke ${i + 1} lag: ${renderTime.toFixed(2)}ms`);
                }
                // Maintain steady interval
                await page.waitForTimeout(Math.max(0, STROKE_INTERVAL_MS - renderTime));
            } catch (error) {
                droppedStrokes++;
                console.error(`Stroke ${i + 1} dropped:`, error);
            }
        }
        // ANALYSIS
        const avgRenderTime = TestHelper.average(strokeTimes);

        console.log(`Average render time: ${avgRenderTime.toFixed(2)}ms`);
        console.log(`Dropped strokes: ${droppedStrokes}`);

        // ASSERTIONS
        expect(avgRenderTime).toBeLessThan(50);
        expect(droppedStrokes).toBe(0);
    });
});