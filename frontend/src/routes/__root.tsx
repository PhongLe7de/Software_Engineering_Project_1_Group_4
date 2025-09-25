import {createRootRoute, Outlet} from '@tanstack/react-router'
import {TanStackRouterDevtools} from '@tanstack/react-router-devtools'
import {SidebarProvider} from "@/components/ui/sidebar";
import {Toaster} from "sonner";
import AuthProvider from "@/context/AuthContext.tsx";

const RootComponent = () => (
    <SidebarProvider
        style={
            {
                "--sidebar-width": "calc(var(--spacing) * 72)",
                "--header-height": "calc(var(--spacing) * 12)",

            } as React.CSSProperties
        }>
        <Toaster richColors position="top-center"/>
        <AuthProvider>
            <Outlet/> {/* Outlet renders next matching route automatically: index (ex App.tsx) */}
            <TanStackRouterDevtools position="bottom-right"/>
        </AuthProvider>
    </SidebarProvider>
)

export const Route = createRootRoute({
    component: RootComponent,
})