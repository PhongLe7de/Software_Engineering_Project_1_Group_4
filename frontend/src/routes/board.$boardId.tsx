import { createFileRoute } from '@tanstack/react-router'
import { useState, useEffect } from 'react'
import Canvas from "@/components/canvas/Canvas.tsx";
import UserRegisterModal from "@/components/frontpage/UserRegisterModal.tsx";
import { AppSidebar } from "@/components/canvas/AppSidebar.tsx";
import { SidebarTrigger, useSidebar } from "@/components/ui/sidebar";
import type { DrawingEvent, BoardDto } from "@/types.ts";
import useWebSocket from "@/hooks/useWebSocket.tsx";
import Cursors from "@/components/canvas/Cursor.tsx";
import {useAuth} from "@/hooks/useAuth.tsx";
import { getBoardById } from "@/services/boardService.ts";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";

function BoardCanvas() {
    const { boardId } = Route.useParams();
    const [tool, setTool] = useState("pen");
    const [brushSize, setBrushSize] = useState(5);
    const [brushColor, setBrushColor] = useState("#000000");
    const [boardInfo, setBoardInfo] = useState<BoardDto | null>(null);
    const [showMotdModal, setShowMotdModal] = useState(false);
    const { user, sidebarVisible } = useAuth();

    const boardIdNumber = parseInt(boardId, 10); // convert the param id to int
    // TODO: BOARD SETTINGS TO UPDATE THE MESSAGE
    // Fetch board info when component mounts
    useEffect(() => {
        const fetchBoardInfo = async () => {
            try {
                const info = await getBoardById(boardIdNumber);
                setBoardInfo(info);
                console.log('Board info:', info);

                // Show MOTD as a modal
                if (info.motdLabel && info.customMessage) {
                    setShowMotdModal(true);
                }
            } catch (error) {
                console.error('Failed to fetch board info:', error);
            }
        };

        if (sidebarVisible && boardIdNumber) {
            fetchBoardInfo();
        }
    }, [boardIdNumber, sidebarVisible]);

    const {
        isConnected,
        remoteEvents,
        sendDrawingEvent,
        sendCursorPosition,
        remoteCursors
    } = useWebSocket({ sidebarVisible, boardId: boardIdNumber });

    const { state } = useSidebar();

    const handleDrawingEvent = (event: DrawingEvent) => {
        sendDrawingEvent(event);
    };

    const handleCursorMovement = (x: number, y: number) => {
        sendCursorPosition(user, x, y);
    };

    return (
        <>
            <div className="fixed top-4 right-6 z-50">
                <span
                    className={`transform backdrop-blur-[4px] text-m
                    ${isConnected ? 'text-green-600' : 'text-red-600'}`}
                >
                    {isConnected ? '● Connected' : '● Disconnected'}
                </span>
            </div>

            {/* MOTD Modal */}
            <Dialog open={showMotdModal} onOpenChange={setShowMotdModal}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{boardInfo?.motdLabel}</DialogTitle>
                        <DialogDescription>
                            {boardInfo?.customMessage}
                        </DialogDescription>
                    </DialogHeader>
                </DialogContent>
            </Dialog>

            {!sidebarVisible && (<UserRegisterModal
            />)}
            {sidebarVisible && <AppSidebar
                brushColor={brushColor}
                setBrushColor={setBrushColor}
                tool={tool}
                setTool={setTool}
                brushSize={brushSize}
                setBrushSize={setBrushSize}
            />}
            {sidebarVisible && (<SidebarTrigger
                className={`
                    fixed top-4 z-50 transition-all duration-200 ease-linear
                    ${state === 'expanded'
                    ? 'left-[calc(var(--sidebar-width)+0.5rem)]'
                    : 'left-2'
                }
                `}
            />)}
            <main className="flex-1 relative w-full h-screen">
                <Cursors cursors={remoteCursors} />
                <Canvas
                    boardId={boardIdNumber}
                    sidebarVisible={sidebarVisible}
                    tool={tool}
                    brushSize={brushSize}
                    brushColor={brushColor}
                    remoteEvents={remoteEvents}
                    onDrawingEvent={handleDrawingEvent}
                    onCursorMove={handleCursorMovement}
                />
            </main>
        </>
    );
}

export const Route = createFileRoute('/board/$boardId')({
    component: BoardCanvas,
})
