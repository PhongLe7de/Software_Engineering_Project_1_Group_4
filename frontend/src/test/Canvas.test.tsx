import Canvas from "../components/Canvas";
import { vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";

describe("Canvas Component", () => {
    const defaultProps = {
        userData: { user_id: 1, display_name: 'Test User', photo_url: 'test.jpg' },
        sidebarVisible: true,
        tool: 'pen',
        brushSize: 5,
        brushColor: '#000000',
        onDrawingEvent: vi.fn(),
        onCursorMove: vi.fn()
    };

    it("renders the Canvas component", () => {
        render(<Canvas {...defaultProps} />);
        const canvas = screen.getByRole('img');
        expect(canvas).toBeInTheDocument();
    });
});