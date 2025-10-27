export type User = {
    userId: number;
    displayName: string;
    photoUrl: string;
    email: string;
} | null;

export type CanvasProps = {
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
};

export type CreateBoardRequest = {
    boardName: string;
    ownerId: number;
};
