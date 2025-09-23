import { createRootRoute, Link, Outlet } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import App from "@/App.tsx";

const RootLayout = () => (
    <>
        <div className="p-2 border-b border-slate-300 mb-4">
            <Link to="/">Home</Link> | <Link to="/about">About</Link>
        </div>
        <div className="p-2">
            <Outlet />
        </div>
        <TanStackRouterDevtools position="bottom-right" />
        <App />

    </>
)

export const Route = createRootRoute({ component: RootLayout })