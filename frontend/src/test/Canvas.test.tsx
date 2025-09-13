import Canvas from "../components/Canvas";
import { vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { describe, it, expect, beforeEach } from "vitest";

const mockContext = {
    lineCap: '',
    lineJoin: '',
    beginPath: vi.fn(),
    strokeStyle: '',
    lineWidth: 0,
    moveTo: vi.fn(),
    lineTo: vi.fn(),
    stroke: vi.fn(),
    closePath: vi.fn(),
};

HTMLCanvasElement.prototype.getContext = vi.fn().mockReturnValue(mockContext);

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

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("renders the Canvas component", () => {
        render(<Canvas {...defaultProps} />);
        screen.debug();

        const canvas = document.querySelector('canvas');
        expect(canvas).toBeInTheDocument();
    });
});