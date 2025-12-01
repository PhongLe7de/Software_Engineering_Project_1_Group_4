import { Page, expect } from '@playwright/test';

/**
 * Test utilities for Playwright E2E tests
 */

export interface TestUser {
    username: string;
    password: string;
    email: string;
}

export class TestHelper {
    constructor(private page: Page) {}

    /**
     * Calculate average of number array
     */
    static average(numbers: number[]): number {
        return numbers.length ? numbers.reduce((a, b) => a + b, 0) / numbers.length : 0;
    }

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
     * Dismiss the MOTD dialog if it's blocking the canvas
     */
    private async dismissMotdIfVisible(): Promise<void> {
        const dialog = this.page.getByRole('dialog', { name: 'Message of the day:' });
        if (await dialog.isVisible()) {
            await this.page.getByRole('button', { name: 'Close' }).click();
            await dialog.waitFor({ state: 'hidden', timeout: 5000 });
        }
    }

    /**
     * Register a new user
     * @param user User info
     */
    async register(user: TestUser): Promise<void> {
        await this.page.goto('/');
        await this.page.getByTestId('register-displayname-input').waitFor({ state: 'visible' });

        await this.page.getByTestId('register-displayname-input').fill(user.username);
        await this.page.getByTestId('register-email-input').fill(user.email);
        await this.page.getByTestId('register-password-input').fill(user.password);
        await this.page.getByRole('button', { name: /register/i }).click();

        // After successful registration, user should be redirected to /home
        await this.page.waitForSelector('[data-testid="sidebar-dropdown-menu"]', { timeout: 15000 });
    }

    /**
     * Login with existing user credentials
     * @param email User email
     * @param password User password
     * @param navigate Whether to navigate to '/' first (set false when benchmarking auth speed separately)
     */
    async login(email: string, password: string, navigate: boolean = true): Promise<void> {
        if (navigate) {
            await this.page.goto('/');
        }

        // Wait for either login or register form
        await this.page.waitForSelector('[data-testid="login-email-input"], [data-testid="register-displayname-input"]');

        // Switch to login form if register form is showing
        const registerInput = this.page.getByTestId('register-displayname-input');
        if (await registerInput.isVisible()) {
            await this.page.getByText(/login/i).last().click();
            await this.page.getByTestId('login-email-input').waitFor({ state: 'visible' });
        }

        await this.page.getByTestId('login-email-input').fill(email);
        await this.page.getByTestId('login-password-input').fill(password);
        await this.page.getByRole('button', { name: /^login$/i }).click();

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
        return await this.page.getByTestId('sidebar-dropdown-menu').isVisible();
    }

    /**
     * Logout current user
     */
    async logout(): Promise<void> {
        const dropdownMenu = this.page.getByTestId('sidebar-dropdown-menu');
        if (await dropdownMenu.isVisible()) {
            await dropdownMenu.click();

            const logoutButton = this.page.getByTestId('sidebar-logout-button');
            await logoutButton.waitFor({ state: 'visible' });
            await logoutButton.click();

            await dropdownMenu.waitFor({ state: 'hidden', timeout: 10000 });
        }
    }

    /**
     * Attempts to join an existing board by name or creates a new one if it doesn't exist.
     * Assumes the user is on the /home board list page.
     * @param boardName The name of the board to access
     */
    async accessOrCreateBoard(boardName: string) {
        await this.page.waitForURL(/\/home/, { timeout: 10000 });

        const boardCardLocator = this.page.locator(`[data-testid^="board-card-"]:has-text("${boardName}")`).first();

        // If board is not visible, create it
        if (!await boardCardLocator.isVisible()) {
            const createBoardButton = this.page.getByTestId('create-board-dialog-trigger');
            await createBoardButton.waitFor({ state: 'visible' });
            await createBoardButton.click();

            await this.page.getByRole("textbox", { name: "Board Name" }).click();
            await this.page.getByRole("textbox", { name: "Board Name" }).fill(boardName);
            await this.page.getByTestId("create-board-submit-button").click();

            // Wait for the card to appear in the list
            await boardCardLocator.waitFor({ state: 'visible', timeout: 5000 });
        }

        // Join OR Open
        const actionButton = boardCardLocator.getByRole('button', { name: /(join|open) board/i });

        await actionButton.click();

        // Wait for navigation to complete
        await this.page.waitForURL(/\/board\/\d+/, { timeout: 15000 });
    }

    /**
     * Draws a stroke on the canvas and measures the time until React state updates.
     *
     * NOTE: This measures LOCAL render latency only (mouse input → React state → DOM update).
     * It does NOT measure full round-trip time through the backend.
     *
     * @returns Time in milliseconds from mouse action to React state acknowledgment
     */
    async drawAndMeasureRender(): Promise<number> {
        const canvas = this.page.getByTestId('board-canvas');
        await this.dismissMotdIfVisible();

        // Capture initial stroke count
        // data-stroke-count={drawingEvents.length + remoteEvents.length}
        const initialCount = parseInt(await canvas.getAttribute('data-stroke-count') || '0');

        // Calculate safe draw coordinates (avoid sidebar and toast areas)
        const box = await canvas.boundingBox();
        if (!box) throw new Error('Canvas not found');

        const SIDEBAR_OFFSET = 400;
        const TOP_OFFSET = 200;
        const startX = box.x + SIDEBAR_OFFSET + (Math.random() * 400);
        const startY = box.y + TOP_OFFSET + (Math.random() * 200);

        //  Draw stroke and measure time until React state updates
        const startTime = performance.now();

        await this.page.mouse.move(startX, startY);
        await this.page.mouse.down();
        await this.page.mouse.move(startX + 50, startY + 50, { steps: 2 });
        await this.page.mouse.up();

        // Wait for stroke count to increment
        try {
            await expect.poll(
                async () => parseInt(await canvas.getAttribute('data-stroke-count') || '0'),
                { timeout: 2000, message: `Stroke count stuck at ${initialCount}` }
            ).toBeGreaterThan(initialCount);
        } catch {
            const finalCount = await canvas.getAttribute('data-stroke-count');
            throw new Error(`Stroke dropped! Count: ${initialCount} → ${finalCount}`);
        }

        return performance.now() - startTime;
    }

    /**
     * Draw on canvas at specific coordinates
     * @param x X offset from safe drawing area
     * @param y Y offset from safe drawing area
     */
    async drawOnCanvas(x: number, y: number): Promise<void> {
        const canvas = this.page.getByTestId('board-canvas');
        await canvas.waitFor({ state: 'visible', timeout: 30000 });
        await this.dismissMotdIfVisible();

        const box = await canvas.boundingBox();
        if (!box) throw new Error('Canvas not found');

        const SIDEBAR_OFFSET = 300;
        const finalX = box.x + SIDEBAR_OFFSET + x;
        const finalY = box.y + y;

        await this.page.mouse.move(finalX, finalY);
        await this.page.mouse.down();
        await this.page.mouse.move(finalX + 50, finalY + 50);
        await this.page.mouse.up();
    }

    /**
     * Draw multiple strokes at random positions
     * @param count Number of strokes to draw
     */
    async drawMultipleStrokes(count: number): Promise<void> {
        for (let i = 0; i < count; i++) {
            await this.drawOnCanvas(Math.random() * 400, Math.random() * 400);
        }
    }

    /**
     * Wait for WebSocket connection (canvas becomes interactive)
     */
    async waitForWebSocketConnection(): Promise<void> {
        await this.page.waitForLoadState('domcontentloaded');
        await expect(this.page.locator('canvas').first()).toBeVisible();
    }

    /**
     * Measure response time for an async action
     * @param action Function to measure
     */
    async measureResponseTime(action: () => Promise<void>): Promise<number> {
        const start = Date.now();
        await action();
        return Date.now() - start;
    }
}