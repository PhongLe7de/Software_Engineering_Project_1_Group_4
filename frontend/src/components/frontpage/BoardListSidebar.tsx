import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuItem,
    SidebarMenuButton,
} from '@/components/ui/sidebar.tsx';
import type {User} from '@/types.ts';
import {LayoutDashboard} from 'lucide-react';
import {useTranslation} from 'react-i18next';
import {NavUser} from '@/components/nav-user.tsx';
import {LanguageSelector} from "@/components/LanguageSelector.tsx";

interface BoardListSidebarProps {
    user: User;
}

export function BoardListSidebar({user}: BoardListSidebarProps) {
    const {t} = useTranslation();

    if (!user) return null;

    const userData = {
        user: {
            name: user.displayName || t('common.undefined_username'),
            email: user.email || t('common.undefined_email'),
            avatar: user.photoUrl || t('common.undefined_photo'),
        },
    };

    return (
        <Sidebar collapsible="icon">
            <SidebarHeader>
                <SidebarMenu>
                    <SidebarMenuItem>
                        <SidebarMenuButton
                            size="lg"
                            tooltip={t('home.board_manager')}
                            className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                        >
                            <div
                                className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                                <LayoutDashboard className="size-4"/>
                            </div>
                            <div className="grid flex-1 text-left text-sm leading-tight">
                                <span className="truncate font-semibold">{t('dashboard.realtime_whiteboard')}</span>
                                <span className="truncate text-xs">{t('home.board_manager')}</span>
                            </div>
                        </SidebarMenuButton>
                    </SidebarMenuItem>

                </SidebarMenu>
            </SidebarHeader>
            <SidebarContent>
                <div className="flex flex-col mt-2 ml-2 ">
                    <LanguageSelector/>
                    <div data-testid={"sidebar-dropdown-menu"}>
                        <NavUser user={userData.user}/>
                    </div>
                </div>
            </SidebarContent>
            <SidebarFooter>

            </SidebarFooter>
        </Sidebar>
    );
}
