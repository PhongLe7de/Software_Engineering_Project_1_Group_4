export type User = {
    id: number;
    displayName: string;
    photoUrl: string;
    email: string;
    locale: string;
} | null;

export type CanvasProps = {
    boardId: number;
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
    boardId: number;
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

export type BoardDto = {
    id: number;
    boardName: string;
    ownerId: number;
    userIds: number[];
    amountOfUsers: number;
    numberOfStrokes: number;
    motdLabel?: string;
    customMessage?: string;
};

export type CreateBoardRequest = {
    boardName: string;
    ownerId: number;
    customMessage?: string;
};
