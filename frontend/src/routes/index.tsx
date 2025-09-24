import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import Canvas from "@/components/Canvas.tsx";
import UserCreateModal from "@/components/UserCreateModal.tsx";
import { AppSidebar } from "@/components/AppSidebar.tsx";
import { SidebarTrigger, useSidebar } from "@/components/ui/sidebar";
import type { DrawingEvent } from "@/types.ts";
import useWebSocket from "@/hooks/useWebSocket.tsx";
import Cursor from "@/components/Cursor.tsx";

function Index() {
    const [userData, setUserData] = useState<{
        userId: number,
        displayName: string;
        photoUrl: string;
    } | undefined>(undefined);

    const [sidebarVisible, setSidebarVisible] = useState(false);
    const [tool, setTool] = useState("pen");
    const [brushSize, setBrushSize] = useState(5);
    const [brushColor, setBrushColor] = useState("#000000");

    const {
        isConnected,
        remoteEvents,
        sendDrawingEvent,
        sendCursorPosition,
        remoteCursors
    } = useWebSocket({ sidebarVisible, userData });

    const { state } = useSidebar();

    const handleDrawingEvent = (event: DrawingEvent) => {
        sendDrawingEvent(event);
    };

    const handleCursorMovement = (x: number, y: number) => {
        sendCursorPosition(x, y);
    };

    return (
        <>
            <div>
                <span
                    className={`fixed left-6 transform backdrop-blur-[4px] z-50 text-m
                    ${isConnected ? 'text-green-600' : 'text-red-600'}`}
                >
                    {isConnected ? '● Connected' : '● Disconnected'}
                </span>
            </div>
            {!sidebarVisible && (<UserCreateModal
                activateSidebar={setSidebarVisible}
                setUserData={setUserData}
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
                <Cursor cursors={remoteCursors} userData={userData} />
                <Canvas
                    userData={userData}
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

export const Route = createFileRoute('/')({
    component: Index,
})