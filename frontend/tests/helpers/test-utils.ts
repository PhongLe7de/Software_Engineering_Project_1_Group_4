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
     * @param prefix Prefix for testuser (ie. stress_10_user_1_...)
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
     * @param user User info
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

        // After successful registration, user should be redirected to /home (sidebar appears)
        await this.page.waitForSelector('[data-testid="sidebar-dropdown-menu"]', { timeout: 15000 });
    }

    /**
     * Login with existing user credentials
     * @param email User email
     * @param password User password
     * @param navigate for separating page loading from auth time when benchmarking auth speeds = firefox is slower than chrome/safari
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
            // After successful login, user should be redirected to /home
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
     * Attempts to join an existing board by name or creates a new one if it doesn't exist.
     * Assumes the user is on the /home board list page.
     * @param boardName The name of the board to access.
     */
    async accessOrCreateBoard(boardName: string) {
        await this.page.waitForURL(/\/home/, { timeout: 10000 });

        // Locate boardcard by name
        const boardCardLocator = this.page.locator(`[data-testid^="board-card-"]:has-text("${boardName}")`).first();
        if (await boardCardLocator.isVisible()) {

            const joinButton =  boardCardLocator.getByRole('button', { name: /join board/i });
            const openButton = boardCardLocator.getByRole('button', { name: /open board/i });

            if (await joinButton.isVisible()) {
                await joinButton.click();
            } else if (await openButton.isVisible()) {
                await openButton.click();
            } else {
                throw new Error(`Board card for "${boardName}" found, but neither join nor open button was visible.`);
            }

            // Wait for URL change to /board/$boardId
            await this.page.waitForURL(/\/board\/\d+/, { timeout: 15000 });

        } else {
            // If not found, create a new board
            // Locator for the button that opens the Create Board Dialog
            const createBoardButton = this.page.getByTestId('create-board-dialog-trigger');
            await createBoardButton.waitFor({ state: 'visible' });
            await createBoardButton.click();
            await this.page.getByRole("textbox", { name: "Board Name" }).click();
            await this.page.getByRole("textbox", { name: "Board Name" }).fill(boardName);
            await this.page.getByTestId("create-board-submit-button").click();
        }
    }

    /**
     * Draw on canvas at specific coordinates
     * @param x x-coords
     * @param y y-coords
     */
    async drawOnCanvas(x: number, y: number): Promise<void> {
        const canvas = this.page.locator('canvas').first();
        await canvas.waitFor({ state: 'visible' });

        const box = await canvas.boundingBox();
        if (!box) throw new Error('Canvas not found');
        await this.page.getByRole('button', { name: 'Close' }).click();
        await this.page.waitForTimeout(300);
        // Draw a simple stroke
        await this.page.mouse.move(box.x + x, box.y + y);
        await this.page.mouse.down();
        await this.page.mouse.move(box.x + x + 50, box.y + y + 50);
        await this.page.mouse.up();
    }

    /**
     * Draw multiple strokes rapidly
     * @param count number of strokes
     */
    async drawMultipleStrokes(count: number): Promise<void> {
        for (let i = 0; i < count; i++) {
            const x = Math.floor(Math.random() * 400);
            const y = Math.floor(Math.random() * 400);
            await this.drawOnCanvas(x, y);
        }
    }

    /**
     * Wait for WebSocket connection (by checking for interactive canvas)
     */
    async waitForWebSocketConnection(): Promise<void> {
        await this.page.waitForLoadState('domcontentloaded');
        // Check if canvas is interactive
        const canvas = this.page.locator('canvas').first();
        await expect(canvas).toBeVisible();
    }

    /**
     * Verify canvas has content (strokes have been drawn)
     */
    async verifyCanvasHasContent(): Promise<void> {
        const canvas = this.page.locator('canvas').first();
        await expect(canvas).toBeVisible();
        // TODO: Measure somehow that lines were drawn 🤷
    }

    /**
     * Measure response time for an action
     * @param action being measured
     */
    async measureResponseTime(action: () => Promise<void>): Promise<number> {
        const start = Date.now();
        await action();
        return Date.now() - start;
    }

}