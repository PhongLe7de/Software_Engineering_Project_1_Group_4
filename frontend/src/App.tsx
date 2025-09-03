import { useState } from 'react'
import Canvas from "@/components/Canvas.tsx";
import { AppSidebar } from "@/components/AppSidebar.tsx";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import type {DrawingEvent} from "@/types.ts";
import useWebSocket from "@/hooks/useWebSocket.tsx";



function App() {
    const [userData, setUserData] = useState(undefined)
    const [sidebarVisible, setSidebarVisible] = useState(true);
    const [tool, setTool] = useState("pen");
    const [brushSize, setBrushSize] = useState(5);
    const [brushColor, setBrushColor] = useState("#000000");
    const {
        isConnected,
        sendDrawingEvent,
        sendCursorPosition,
    } = useWebSocket();


    const handleDrawingEvent = (event: DrawingEvent) => {
        // TODO: send drawing event when backend is ready
        sendDrawingEvent(event);
    };

    const handleCursorMovement = (username: string, x: number, y: number) => {
        // TODO: create Cursor component send cursor position when backend is ready
        sendCursorPosition(username, x, y);
    };

    return (
        <SidebarProvider>
            <AppSidebar />
            <main className="flex-1 relative w-full h-screen">
                <SidebarTrigger />
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
        </SidebarProvider>
    )
}

export default App