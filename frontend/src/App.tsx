import {useState} from 'react'
import Canvas from "@/components/Canvas.tsx";
import UserCreateModal from "@/components/UserCreateModal.tsx";
import {AppSidebar} from "@/components/AppSidebar.tsx";
import {SidebarProvider, SidebarTrigger, useSidebar} from "@/components/ui/sidebar";
import type {DrawingEvent} from "@/types.ts";
import useWebSocket, {type CursorPosition} from "@/hooks/useWebSocket.tsx";
import {Toaster} from "sonner";
import Cursor from "@/components/Cursor.tsx";
import Cursors from "@/components/Cursor.tsx";

function AppContent() {
    const [userData, setUserData] = useState<{
        user_id: number,
        display_name: string;
        photo_url: string;
    } | undefined>(undefined);

    const [sidebarVisible, setSidebarVisible] = useState(false);
    const [tool, setTool] = useState("pen");
    const [brushSize, setBrushSize] = useState(5);
    const [brushColor, setBrushColor] = useState("#000000");
    const [localCursorPosition, setLocalCursorPosition] = useState<CursorPosition | undefined>(undefined); // temporary only for testing

    const {
        isConnected,
        remoteEvents,
        sendDrawingEvent,
        sendCursorPosition,
        remoteCursors
    } = useWebSocket({sidebarVisible, userData});

    const {state} = useSidebar();

    const handleDrawingEvent = (event: DrawingEvent) => {
        sendDrawingEvent(event);
    };

    const handleCursorMovement = (x: number, y: number) => {
        sendCursorPosition(x, y);
        if (userData) {  // This is testing out styles before backend is ready
            setLocalCursorPosition({
                display_name: userData.display_name,
                photo_url: userData.photo_url,
                x,
                y,
            });
        }
    };

    return (
        <>

            <Toaster richColors position="top-center"/>
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

                <Cursor cursors={localCursorPosition ? [...remoteCursors, localCursorPosition] : remoteCursors} userData={userData} />
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
            <AppContent/>
        </SidebarProvider>
    )
}

export default App