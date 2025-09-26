import * as React from "react"
import {
    IconPhotoEdit,
    IconPencil,
    IconEraser
} from "@tabler/icons-react"
import { NavUser } from "@/components/nav-user"
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "@/components/ui/sidebar";
import { HexColorPicker } from "react-colorful";
import { useState } from "react";
import { useAuth } from "@/hooks/useAuth.tsx";

const colorPalette = [
    { name: 'Black', color: '#000000' },
    { name: 'Red', color: '#ff0000' },
    { name: 'Yellow', color: '#ffff00' },
    { name: 'Green', color: '#00ff00' },
    { name: 'Blue', color: '#0000ff' }
];

interface AppSidebarProps extends React.ComponentProps<typeof Sidebar> {
    brushColor: string;
    setBrushColor: (color: string) => void;
    tool: string;
    setTool: (tool: string) => void;
    brushSize: number;
    setBrushSize: (size: number) => void;
}

export function AppSidebar({ brushColor, setBrushColor, tool, setTool, brushSize, setBrushSize, ...props }: AppSidebarProps) {
    const [color, setColor] = useState(brushColor);
    const { user } = useAuth();

    console.log(user?.photoUrl);

    const userData = {
        user: {
            name: user?.displayName || "Undefined username",
            email: user?.email || "Undefined email",
            avatar: user?.photoUrl || "Undefined photo",
        },
    };

    React.useEffect(() => {
        if (tool == "pen") {
            setColor(brushColor);
        }
    }, [brushColor, tool]);

    const handleToolChange = (newTool: string) => {
        setTool(newTool);
        if (newTool == "eraser") {
            setBrushColor("#ffffff"); // Set brush color to white for eraser
        } else if (newTool == "pen") {
            setBrushColor(color); // Restore brush color
        }
    }

    const handleColorSelect = (selectedColor: string) => {
        if (tool == "pen") {
            setBrushColor(selectedColor);
        } else {
            setColor(selectedColor);
        }
    }

    return (
        <Sidebar collapsible="offcanvas" {...props}>
            <SidebarHeader>
                <SidebarMenu>
                    <SidebarMenuItem>
                        <SidebarMenuButton
                            asChild
                            className="data-[slot=sidebar-menu-button]:!p-1.5"
                        >
                            <a href="#">
                                <IconPhotoEdit className="!size-5" />
                                <span className="text-base font-semibold">Realtime Whiteboard</span>
                            </a>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarHeader>
            <SidebarContent>
                <div className="px-4 py-2">
                    <label className="block text-sm font-medium mb-2">
                        Brush Size: {brushSize}px
                    </label>
                    <input
                        type="range"
                        min="1"
                        max="50"
                        value={brushSize}
                        onChange={(e) => setBrushSize(Number(e.target.value))}
                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                    />
                </div>
                <div className="px-4 py-2">
                    <div className="flex gap-2">
                        <button
                            onClick={() => handleToolChange("pen")}
                            className={`flex-1 flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                                tool === "pen"
                                    ? "bg-blue-600 text-white"
                                    : "bg-gray-200 text-gray-700 hover:bg-gray-300"
                            }`}
                        >
                            <IconPencil size={16} />
                            Pen
                        </button>
                        <button
                            onClick={() => handleToolChange("eraser")}
                            className={`flex-1 flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                                tool === "eraser"
                                    ? "bg-blue-600 text-white"
                                    : "bg-gray-200 text-gray-700 hover:bg-gray-300"
                            }`}
                        >
                            <IconEraser size={16} />
                            Eraser
                        </button>
                    </div>
                </div>
                <div className="px-4 py-2 flex justify-center">
                    <HexColorPicker
                        color={tool == "pen" ? brushColor : color}
                        onChange={(c) => {
                            if (tool == "pen") {
                                setBrushColor(c);
                            } else {
                                setColor(c);
                            }
                        }}
                    />
                </div>
                <div className="gap-2 flex justify-center">
                    {colorPalette.map((colorItem) => (
                        <button
                            key={colorItem.name}
                            onClick={() => handleColorSelect(colorItem.color)}
                            className="w-8 h-8 rounded-full border-2 border-gray-300 hover:border-gray-500 transition-colors cursor-pointer shadow-sm hover:shadow-md"
                            style={{ backgroundColor: colorItem.color }}
                            title={colorItem.name}
                        />
                    ))}
                </div>
            </SidebarContent>
            <SidebarFooter>
                <NavUser user={userData.user} />
            </SidebarFooter>
        </Sidebar>
    )
}
