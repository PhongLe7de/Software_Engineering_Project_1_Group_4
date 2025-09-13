export type CanvasProps = {
    userData: {
        user_id: number;
        display_name: string; photo_url: string } | undefined;
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
    user_id: number;
    display_name: string;
    timestamp: number;
    type: 'start' | 'draw' | 'end';
    tool: string;
    x: number;
    y: number;
    brushSize: number;
    brushColor: string;
    strokeId: string;
};
