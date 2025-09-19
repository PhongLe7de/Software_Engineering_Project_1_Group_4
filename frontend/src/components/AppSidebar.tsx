import * as React from "react"
import {
    IconInnerShadowTop,
    IconSettings,
    IconPencil,
    IconEraser
} from "@tabler/icons-react"

import { NavSecondary } from "@/components/nav-secondary"
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
import {useState} from "react";

const data = {
    user: {
        name: "shadcn",
        email: "m@example.com",
        avatar: "/avatars/shadcn.jpg",
    },
    navSecondary: [
        {
            title: "Settings",
            url: "#",
            icon: IconSettings,
        }
    ],
};

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
                                <IconInnerShadowTop className="!size-5" />
                                <span className="text-base font-semibold">Realtime Whiteboard</span>
                            </a>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarHeader>
            <SidebarContent>
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
                <HexColorPicker
                    color={tool == "pen" ? brushColor : color}
                    onChange={(c) => {
                        if (tool == "pen") {
                            setBrushColor(c);
                        } else {
                            setColor(c);
                        }
                    }}
                    className=""
                />
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
                <NavSecondary items={data.navSecondary} className="mt-auto" />
            </SidebarContent>
            <SidebarFooter>
                <NavUser user={data.user} />
            </SidebarFooter>
        </Sidebar>
    )
}
