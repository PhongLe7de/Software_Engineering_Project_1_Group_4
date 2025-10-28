import React, { useEffect, useState, useRef, useCallback } from "react";
import type {CanvasProps, DrawingEvent} from "../../types.ts";
import {generateId} from "../../lib/utils.ts";
import {useAuth} from "@/hooks/useAuth.tsx";
import { useTranslation } from "react-i18next";

// TODO: REMOVE CURSOR WHEN USER LEAVES
function Canvas({ boardId, sidebarVisible, tool, brushSize, brushColor, remoteEvents=[], onDrawingEvent, onCursorMove }: CanvasProps) {
    const { user } = useAuth();
    const canvasRef = useRef<HTMLCanvasElement | null>(null);
    const contextRef = useRef<CanvasRenderingContext2D | null>(null);
    const [drawingEvents, setDrawingEvents] = useState<DrawingEvent[]>([]);
    const [isDrawing, setIsDrawing] = useState(false);
    const [isDragging, setIsDragging] = useState(false);
    const [currentStrokeId, setCurrentStrokeId] = useState<string>("");
    
    const { t } = useTranslation();

    // Viewport position state for hand tool navigation
    const [viewportX, setViewportX] = useState(0);
    const [viewportY, setViewportY] = useState(0);
    const [dragStart, setDragStart] = useState({ x: 0, y: 0 });

    const containerRef = useRef<HTMLDivElement>(null);

    const CANVAS_WIDTH = 3840;
    const CANVAS_HEIGHT = 2160;


    const initializeCanvas = useCallback(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        canvas.width = CANVAS_WIDTH;
        canvas.height = CANVAS_HEIGHT;

        const ctx = canvas.getContext("2d");
        if (ctx) {
            ctx.lineCap = "round";
            ctx.lineJoin = "round";
            contextRef.current = ctx;
        }
    }, []);

    // Clear on mount to prevent overlapping when history is loaded on connect
    const clearCanvas = useCallback(() => {
        const canvas = canvasRef.current;
        const ctx = contextRef.current;
        if (!canvas || !ctx) return;

        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }, []);

    // Function to handle draw events from backend
    const replayAllEvents = useCallback(() => {
        const ctx = contextRef.current;
        if (!ctx) return;

        clearCanvas();
        ctx.globalCompositeOperation = 'source-over';

        const drawStrokes = (events: DrawingEvent[]) => {
            const strokes = new Map<string, DrawingEvent[]>();
            events.forEach(event => {
                if (!strokes.has(event.strokeId)) {
                    strokes.set(event.strokeId, []);
                }
                strokes.get(event.strokeId)!.push(event);
            });

            strokes.forEach(strokeEvents => {
                if (strokeEvents.length === 0) return;

                strokeEvents.sort((a, b) => a.timestamp - b.timestamp);
                const firstEvent = strokeEvents[0];

                ctx.beginPath();
                ctx.strokeStyle = (firstEvent.tool === "eraser") ? "white" : firstEvent.brushColor;
                ctx.lineWidth = firstEvent.brushSize;
                ctx.moveTo(firstEvent.x, firstEvent.y);

                strokeEvents.forEach(event => {
                    if (event.type === 'draw') {
                        ctx.lineTo(event.x, event.y);
                    }
                });
                ctx.stroke();
            });
        };
        // Draw remote events first that they're in the background
        drawStrokes(remoteEvents);
        drawStrokes(drawingEvents);

    }, [drawingEvents, remoteEvents, clearCanvas]);

    // Replay when events change
    useEffect(() => {
        replayAllEvents();
    }, [replayAllEvents]);

    // context menu prevention and right-click dragging
    useEffect(() => {
        const container = containerRef.current;
        if (!container) return;

        const registerRightClick = (e: MouseEvent) => {
            e.preventDefault();
        };
        const rightClickDragging = (e: MouseEvent) => {
            if (e.button !== 2) return;
            setIsDragging(true);
            setDragStart({ x: e.clientX, y: e.clientY });
        };

        document.addEventListener("mousedown", rightClickDragging);
        document.addEventListener("contextmenu", registerRightClick);
        return () => {
            document.removeEventListener("contextmenu", registerRightClick);
            document.removeEventListener("mousedown", rightClickDragging);
        };
    }, []);

    // Initialize canvas once
    useEffect(() => {
        initializeCanvas();
    }, [initializeCanvas]);

    const createDrawingEvent = (
        type: DrawingEvent['type'],
        x: number,
        y: number,
        strokeId: string
    ): DrawingEvent => ({
        id: generateId(),
        boardId: boardId,
        userId: user?.id ?? 0,
        displayName: user?.displayName || t('common.undefined_username'),
        timestamp: Date.now(),
        type,
        tool,
        x,
        y,
        brushSize,
        brushColor,
        strokeId,
    });

    const addDrawingEvent = (event: DrawingEvent) => {
        setDrawingEvents((prev) => [...prev, event]); // local events
        onDrawingEvent?.(event); // sending draw event to backend
    };

    const startDrawing = (e: React.MouseEvent<HTMLCanvasElement>) => {
        if (!sidebarVisible) { return }
        if (tool === "hand") {
            setIsDragging(true);
            setDragStart({ x: e.clientX, y: e.clientY });
            return;
        }

        if (e.button !== 0) return;
        const strokeId = generateId();
        setCurrentStrokeId(strokeId);
        setIsDrawing(true);

        const x = e.nativeEvent.offsetX;
        const y = e.nativeEvent.offsetY;

        // Create the event before setting context properties that might depend on the tool
        const startEvent = createDrawingEvent("start", x, y, strokeId);
        addDrawingEvent(startEvent);

        const ctx = contextRef.current;
        if (ctx) {
            ctx.beginPath();
            ctx.strokeStyle = tool === "eraser" ? "white" : brushColor;
            ctx.lineWidth = brushSize;
            ctx.moveTo(x, y);
        }

    };

    const draw = (e: React.MouseEvent<HTMLCanvasElement>) => {
        if (isDragging) {
            // Calculate how much the mouse has moved
            const dx = e.clientX - dragStart.x;
            const dy = e.clientY - dragStart.y;

            // Update the viewport position
            setViewportX(prev => prev + dx);
            setViewportY(prev => prev + dy);

            // Update drag start point
            setDragStart({ x: e.clientX, y: e.clientY });
            return;
        }

        if (!isDrawing || !currentStrokeId) return;

        const x = e.nativeEvent.offsetX;
        const y = e.nativeEvent.offsetY;

        const drawEvent = createDrawingEvent('draw', x, y, currentStrokeId);
        addDrawingEvent(drawEvent);

        const ctx = contextRef.current;
        if (ctx) {
            ctx.lineTo(x, y);
            ctx.stroke();
        }
    };

    const stopDrawing = (e: React.MouseEvent<HTMLCanvasElement>) => {
        if (isDragging) {
            setIsDragging(false);
            return;
        }

        if (!isDrawing || !currentStrokeId) return;

        const x = e.nativeEvent.offsetX;
        const y = e.nativeEvent.offsetY;

        const endEvent = createDrawingEvent('end', x, y, currentStrokeId);
        addDrawingEvent(endEvent);

        setIsDrawing(false);
        setCurrentStrokeId("");

        const ctx = contextRef.current;
        if (ctx) {
            ctx.closePath();
        }
    };

    const getCursorStyle = () => {
        if (!sidebarVisible) {
            return
        }
        if (isDragging) {
            return 'grabbing';
        }
        switch (tool) {
            case 'hand':
                return isDragging ? 'grabbing' : 'grab';
            case 'eraser':
                return 'crosshair';
            default:
                return 'crosshair';
        }
    };

    const getCursorPosition = (e: React.MouseEvent<HTMLCanvasElement>) => {
        const x = e.nativeEvent.offsetX;
        const y = e.nativeEvent.offsetY;
        onCursorMove?.( x, y);
    };


    const handleMouseMove = (e: React.MouseEvent<HTMLCanvasElement>) => {
        getCursorPosition(e);
        draw(e);
    }

    return (
        <div className="w-full h-screen overflow-hidden" ref={containerRef}>
            <div
                className="relative"
                style={{
                    transform: `translate(${viewportX}px, ${viewportY}px)`,
                    transition: isDragging ? 'none' : 'transform 0.1s ease-out'
                }}
            >
                <canvas
                    ref={canvasRef}
                    width={CANVAS_WIDTH}
                    height={CANVAS_HEIGHT}
                    onMouseDown={startDrawing}
                    onMouseMove={handleMouseMove}
                    onMouseUp={stopDrawing}
                    onMouseLeave={stopDrawing}
                    style={{ cursor: getCursorStyle() }}
                    className="border border-gray-300"
                />
            </div>
        </div>
    );
}

export default Canvas;