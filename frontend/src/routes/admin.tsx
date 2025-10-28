import {createFileRoute} from '@tanstack/react-router'
import {ChartAreaInteractive} from "@/components/dashboard/chart-area-interactive.tsx"
import UserDataTable from "@/components/dashboard/UserDataTable.tsx"

import data from "../lib/test-data.json"

export const Route = createFileRoute('/admin')({
    component: AdminPanel,
})

function AdminPanel() {
    return (
        <>
            <div className="flex flex-1 flex-col">
                <div className="@container/main flex flex-1 flex-col gap-2">
                    <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6">
                        <div className="px-4 lg:px-6">
                            <ChartAreaInteractive/>
                        </div>
                        <UserDataTable users={data}/>
                    </div>
                </div>
            </div>

        </>
    )
}