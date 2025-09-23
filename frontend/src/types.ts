export type CanvasProps = {
    userData: {
        userId: number;
        displayName: string; photoUrl: string } | undefined;
    sidebarVisible: boolean;
    tool: string;
    brushSize: number;
    brushColor: string;
    onDrawingEvent?: (event: DrawingEvent) => void;
    remoteEvents?: DrawingEvent[];
    onCursorMove?: ( x: number, y: number) => void;
};

export type DrawingEvent = {
    id: string;
    userId: number;
    displayName: string;
    timestamp: number;
    type: 'start' | 'draw' | 'end';
    tool: string;
    x: number;
    y: number;
    brushSize: number;
    brushColor: string;
    strokeId: string;
};
