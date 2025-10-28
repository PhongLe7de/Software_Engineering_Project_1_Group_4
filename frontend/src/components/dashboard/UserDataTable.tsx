import * as React from "react"
import {Badge} from "@/components/ui/badge.tsx"
import {Button} from "@/components/ui/button.tsx"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table.tsx"
import { useTranslation } from "react-i18next"

type User = {
    id: string
    name: string
    email: string
    role: string
    status: string
    createdAt: string
}

// Data table of users in admin panel

const UserDataTable = ({users}: { users: User[] }) => {
    const { t } = useTranslation();
    

    const [data, setData] = React.useState<User[]>(users)
    const toggleBan = (id: string) => {
        setData((prev) =>
            prev.map((user) =>
                user.id === id
                    ? {
                        ...user,
                        status: user.status === "Banned" ? "Active" : "Banned",
                    }
                    : user
            )
        )
    }

    const removeUser = (id: string) => {
        setData((prev) => prev.filter((user) => user.id !== id))
    }

    return (
        <div className="overflow-x-auto rounded-lg border">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>{t('user.')}</TableHead>
                        <TableHead>{t('user.name')}</TableHead>
                        <TableHead>{t('user.email')}</TableHead>
                        <TableHead>{t('user.role')}</TableHead>
                        <TableHead>{t('user.status')}</TableHead>
                        <TableHead>{t('user.createdAt')}</TableHead>
                        <TableHead className="text-right">{t('user.actions')}</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {data.length > 0 ? (
                        data.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell>{user.id}</TableCell>
                                <TableCell>{user.name}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>
                                    <Badge>{user.role}</Badge>
                                </TableCell>
                                <TableCell>
                                    <Badge
                                        variant={
                                            user.status === "Active" ? "default" : "destructive"
                                        }
                                    >
                                        {user.status}
                                    </Badge>
                                </TableCell>
                                <TableCell>
                                    {new Date(user.createdAt).toLocaleDateString()}
                                </TableCell>
                                <TableCell className="text-right space-x-2">
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        onClick={() => toggleBan(user.id)}
                                    >
                                        {user.status === "Banned" ? t('user.unban') : t('user.ban')}
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant="destructive"
                                        onClick={() => removeUser(user.id)}
                                    >
                                        {t('user.remove')}
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))
                    ) : (
                        <TableRow>
                            <TableCell colSpan={7} className="text-center">
                                {t('user.no_users_found')}
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </div>
    )
}

export default UserDataTable