import * as React from "react"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"

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
                        <TableHead>ID</TableHead>
                        <TableHead>Name</TableHead>
                        <TableHead>Email</TableHead>
                        <TableHead>Role</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Created At</TableHead>
                        <TableHead className="text-right">Actions</TableHead>
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
                                        {user.status === "Banned" ? "Unban" : "Ban"}
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant="destructive"
                                        onClick={() => removeUser(user.id)}
                                    >
                                        Remove
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))
                    ) : (
                        <TableRow>
                            <TableCell colSpan={7} className="text-center">
                                No users found.
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </div>
    )
}

export default UserDataTable