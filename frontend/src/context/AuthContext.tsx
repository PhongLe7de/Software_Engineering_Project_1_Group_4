import {createContext, useEffect, useState} from "react";
import type {User} from "../types";
import {toast} from "sonner";

export const AuthContext = createContext<{
    user: User | null;
    sidebarVisible: boolean;
    login: (userData: loginData) => Promise<User>;
    register: (userData: registerData) => Promise<User>;
    updateUser: (userId: number, userData: updateUserData) => Promise<User>;
    logout: () => void;
} | null>(null);


type AuthResponse = {
    user: User;
    token: string;
}
type registerData = {
    email: string;
    password: string;
    displayName: string;
    photoUrl: string;
    locale: string;
}
type loginData = {
    email: string;
    password: string;
}
type updateUserData = {
    displayName?: string;
    email?: string;
    currentPassword?: string;
    newPassword?: string;
}

const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const [user, setUser] = useState<User | null>(null);
    const [sidebarVisible, setSidebarVisible] = useState(false);

    useEffect(() => {
        const savedUser = localStorage.getItem('user');
        if (savedUser) {
            try {
                const parsedUser: User = JSON.parse(savedUser);
                setUser(parsedUser);
                setSidebarVisible(true);
                toast.success(`Welcome back, ${parsedUser?.displayName}!`);
            } catch (e) {
                console.error("Failed to parse user from localStorage", e);
                localStorage.removeItem('user');
            }
        }
        return ()=> {  // Clean up function for testing login/register
            // setUser(null);
            // setSidebarVisible(false);
            // localStorage.removeItem('user');
            // localStorage.removeItem('token');
        }
    }, []);

    const register = async (userData: registerData) => {
        const res = await fetch(`${import.meta.env.VITE_API_URL}api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (!res.ok) {
            const errorData = await res.json();
            if (errorData.status === 500) toast.error(errorData.message);
            else toast.error(errorData.message);
            throw new Error(errorData.message || `HTTP error! status: ${res.status}`);
        }

        const data: AuthResponse = await res.json();

        setUser(data.user);
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        setSidebarVisible(true);
        return data.user;
    };

    const login = async (userData: loginData) => {
        const res = await fetch(`${import.meta.env.VITE_API_URL}api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (!res.ok) {
            const errorData = await res.json();
            if (errorData.status === 403) toast.error('Invalid email or password');
            throw new Error(errorData.message || `HTTP error! status: ${res.status}`);
        }

        const data: AuthResponse = await res.json();
        console.log(data)
        setUser(data.user);
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        setSidebarVisible(true);
        return data.user;
    };

    const updateUser = async (userId: number, userData: updateUserData) => {
        const token = localStorage.getItem('token');

        const res = await fetch(`${import.meta.env.VITE_API_URL}api/user/update/${userId}`, {
            method: 'PUT',
            headers: {
                "Content-Type": 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(userData),
        });

        if (!res.ok) {
            const errorData = await res.json();
            toast.error(errorData.message || "Failed to update user");
            throw new Error(errorData.message || `HTTP error! status: ${res.status}`);
        }

        const data: User = await res.json();

        setUser(data);
        localStorage.setItem('user', JSON.stringify(data));
        toast.success("Settings updated successfully!");

        return data;
    }

    const logout = () => {
        setUser(null);
        setSidebarVisible(false);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{user, sidebarVisible, login, register, updateUser, logout}}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;