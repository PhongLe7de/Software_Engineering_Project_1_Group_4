import { Page, expect } from '@playwright/test';


/*
  *  Util file for all the repeating code
 */

export interface TestUser {
  username: string;
  password: string;
  email: string;
}

export class TestHelper {
  constructor(private page: Page) {}

  /**
   * Generate a random test user
   */
  static generateUser(prefix: string = 'user'): TestUser {
    const random = Math.floor(Math.random() * 10000);
    return {
      username: `${prefix}_${random}`,
      password: `Test123!${random}`,
      email: `${prefix}_${random}@test.com`,
    };
  }

  /**
   * Register a new user
   */
  async register(user: TestUser): Promise<void> {
    await this.page.goto('/');

    // wait for the registration modal to be visible
    await this.page.waitForTimeout(1000);


    // Fill in registration form
    await this.page.getByLabel(/display name/i).fill(user.username);
    await this.page.getByLabel(/email/i).fill(user.email);
    await this.page.getByLabel(/password/i).fill(user.password);
    const submitButton = this.page.getByRole('button', { name: /register/i });
    await submitButton.click();

    await this.page.waitForTimeout(3000);
  }

}
