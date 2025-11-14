import Canvas from "../../src/components/canvas/Canvas.tsx";
import { vi } from "vitest";
import { render } from "@testing-library/react";
import { describe, it, expect, beforeEach } from "vitest";
import AuthProvider from "frontend/src/context/AuthContext.tsx";

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
    clearRect: vi.fn(),
};

HTMLCanvasElement.prototype.getContext = vi.fn().mockReturnValue(mockContext);

describe("Canvas Component", () => {
    const defaultProps = {
        boardId: 1,
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
        render(
            <AuthProvider>
                <Canvas {...defaultProps} />
            </AuthProvider>
        );

        const canvas = document.querySelector('canvas');
        expect(canvas).toBeInTheDocument();
    });

    it("applies correct cursor style based on tool", () => {
        const { rerender } = render(
            <AuthProvider>
                <Canvas {...defaultProps} />
            </AuthProvider>
        );

        const canvas = document.querySelector('canvas');
        expect(canvas?.style.cursor).toBe('crosshair');

        rerender(
            <AuthProvider>
                <Canvas {...defaultProps} tool="hand" />
            </AuthProvider>
        );
        expect(canvas?.style.cursor).toBe('grab');

        rerender(
            <AuthProvider>
                <Canvas {...defaultProps} tool="eraser" />
            </AuthProvider>
        );
        expect(canvas?.style.cursor).toBe('crosshair');
    });

    it("does not render cursor when sidebar is not visible", () => {
        render(
            <AuthProvider>
                <Canvas {...defaultProps} sidebarVisible={false} />
            </AuthProvider>
        );

        const canvas = document.querySelector('canvas');
        expect(canvas?.style.cursor).toBe('');
    });
});
