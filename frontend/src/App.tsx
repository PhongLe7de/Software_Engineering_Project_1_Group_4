import { useState } from 'react'
import Canvas from "@/components/Canvas.tsx";
import UserCreateModal from "@/components/UserCreateModal.tsx";
import { AppSidebar } from "@/components/AppSidebar.tsx";
import { SidebarProvider, SidebarTrigger, useSidebar } from "@/components/ui/sidebar";
import type {DrawingEvent} from "@/types.ts";
import useWebSocket from "@/hooks/useWebSocket.tsx";
import {Toaster} from "sonner";

function AppContent() {
    const [userData, setUserData] = useState<{  displayName: string; photoUrl: string; } | undefined>(undefined);
    const [sidebarVisible, setSidebarVisible] = useState(true); // TODO: initial state false for production
    const [tool, setTool] = useState("pen");
    const [brushSize, setBrushSize] = useState(5);
    const [brushColor, setBrushColor] = useState("#000000");
    const {
        isConnected,
        sendDrawingEvent,
        sendCursorPosition,
    } = useWebSocket();

    const { state } = useSidebar();

    const handleDrawingEvent = (event: DrawingEvent) => {
        sendDrawingEvent(event);
    };

    const handleCursorMovement = (username: string, x: number, y: number) => {
        sendCursorPosition(username, x, y);
    };

    return (
        <>
            <Toaster richColors position="top-center"/>
            <UserCreateModal
                activateSidebar={setSidebarVisible}
                setUserData={setUserData}
            />
            {sidebarVisible && <AppSidebar/>}
            {/* Position the trigger button based on sidebar state */}
            {sidebarVisible && (<SidebarTrigger
                className={`
                    fixed top-4 z-50 transition-all duration-200 ease-linear
                    ${state === 'expanded'
                    ? 'left-[calc(var(--sidebar-width)+0.5rem)]' // Right of expanded sidebar
                    : 'left-2' // Left side when collapsed
                }
                `}
            />)}
            <main className="flex-1 relative w-full h-screen">
                <Canvas
                    userData={userData}
                    sidebarVisible={sidebarVisible}
                    tool={tool}
                    brushSize={brushSize}
                    brushColor={brushColor}
                    onDrawingEvent={handleDrawingEvent}
                    onCursorMove={handleCursorMovement}
                />
            </main>
        </>
    );
}

function App() {
    return (
        <SidebarProvider>
            <AppContent />
        </SidebarProvider>
    )
}

export default App