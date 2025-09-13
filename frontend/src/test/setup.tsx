import "@testing-library/jest-dom/vitest"
import { afterEach } from 'vitest'
import { cleanup } from '@testing-library/react'

// Cleans after each test case (e.g. clearing jsdom)
afterEach(() => {
    cleanup();
});