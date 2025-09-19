import * as React from "react"
import {
    IconInnerShadowTop,
    IconSettings,
    IconPencil,
    IconEraser
} from "@tabler/icons-react"

import { NavMain } from "@/components/nav-main"
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

const data = {
    user: {
        name: "shadcn",
        email: "m@example.com",
        avatar: "/avatars/shadcn.jpg",
    },
    navMain: [
        {
            title: "Pen",
            url: "#",
            icon: IconPencil,
        },
        {
            title: "Eraser",
            url: "#",
            icon: IconEraser,
        },
    ],
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

export function AppSidebar({ brushColor, setBrushColor, brushSize, setBrushSize, ...props }: AppSidebarProps) {
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
                <NavMain items={data.navMain} />
                <HexColorPicker color={brushColor} onChange={setBrushColor} className=""/>
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
