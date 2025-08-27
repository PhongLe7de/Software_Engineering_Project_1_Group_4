import { useState } from 'react'
import Canvas from "@/components/Canvas.tsx";
import { AppSidebar } from "@/components/AppSidebar.tsx";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";

function App() {
    const [userData, setUserData] = useState(undefined)
    const [sidebarVisible, setSidebarVisible] = useState(true);
    const [tool, setTool] = useState("pen");
    const [brushSize, setBrushSize] = useState(5);
    const [brushColor, setBrushColor] = useState("#000000");

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
                />
            </main>
        </SidebarProvider>
    )
}

export default App