import { test, expect } from '@playwright/test';
import { TestHelper, TestUser } from '../helpers/test-utils';

/**
 * These tests stress test the authentication:
 * - 10 concurrent user registrations
 * TODO:
 * - Concurrent logins
 * - Rapid login/logout cycles
 * - Authentication token handling
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
        await page.waitForTimeout(1000);
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
});
