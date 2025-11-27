import { Page, expect } from '@playwright/test';


/*
  * Util file for all the repeating code
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

        // Wait for the registration form to be visible
        await this.page.getByTestId('register-displayname-input').waitFor({ state: 'visible' });

        await this.page.getByTestId('register-displayname-input').fill(user.username);
        await this.page.getByTestId('register-email-input').fill(user.email);
        await this.page.getByTestId('register-password-input').fill(user.password);
        const submitButton = this.page.getByRole('button', { name: /register/i });
        await submitButton.click();

        await this.page.waitForSelector('[data-testid="sidebar-dropdown-menu"]', { timeout: 15000 });
    }

    /**
     * Login with existing user credentials
     * 'navigate' param for separating page load time from auth time
     */
    async login(email: string, password: string, navigate: boolean = true): Promise<void> {
        if (navigate) {
            await this.page.goto('/');
        }

        // wait for either the login input or the register input
        await this.page.waitForSelector('[data-testid="login-email-input"], [data-testid="register-displayname-input"]');
        const registerInput = this.page.getByTestId('register-displayname-input');

        // If register input is visible switch to login
        if (await registerInput.isVisible()) {
            await this.page.getByText(/login/i).last().click();
            await this.page.getByTestId('login-email-input').waitFor({ state: 'visible' });
        }

        await this.page.getByTestId('login-email-input').fill(email);
        await this.page.getByTestId('login-password-input').fill(password);

        const submitButton = this.page.getByRole('button', { name: /^login$/i });
        await submitButton.click();

        try {
            await this.page.waitForSelector('[data-testid="sidebar-dropdown-menu"]', { timeout: 15000 });
        } catch (e) {
            console.error(`Login failed or sidebar did not load for ${email}`);
            throw e;
        }
    }

    /**
     * Check if user is logged in
     */
    async isLoggedIn(): Promise<boolean> {
        // Check for sidebar dropdown menu as indicator that user is logged in
        const dropdownMenu = this.page.getByTestId('sidebar-dropdown-menu');
        return await dropdownMenu.isVisible(); // Use isVisible for simpler boolean check
    }

    /**
     * Logout current user
     * IMPROVEMENT: Replaced all hard waits with explicit state checks.
     */
    async logout(): Promise<void> {
        // Open the sidebar dropdown menu
        const dropdownMenu = this.page.getByTestId('sidebar-dropdown-menu');
        if (await dropdownMenu.isVisible()) {
            await dropdownMenu.click();

            // Click the logout button inside the dropdown (it has a data-testid)
            const logoutButton = this.page.getByTestId('sidebar-logout-button');

            // Wait for the button to appear before clicking
            await logoutButton.waitFor({ state: 'visible' });
            await logoutButton.click();

            // Wait for the indicator of successful logout (i.e., the dropdown menu disappears)
            await dropdownMenu.waitFor({ state: 'hidden', timeout: 10000 });
        }
    }

    /**
     * Measure response time for an action
     */
    async measureResponseTime(action: () => Promise<void>): Promise<number> {
        const start = Date.now();
        await action();
        return Date.now() - start;
    }
}