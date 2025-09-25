import {createContext, useEffect, useState} from "react";
import type {User} from "../types";

export const AuthContext = createContext<{
    user: User | null;
    login: (userData: loginData) => Promise<User>;
    register: (userData: registerData) => Promise<User>;
    logout: () => void;
} | null>(null);


type AuthResponse = {
    user: User;
    token: string;
}
type registerData = {
    email: string;
    password: string
    displayName: string
    photoUrl: string
}
type loginData = {
    email: string;
    password: string;
}

const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const savedUser = localStorage.getItem('user');
        if (savedUser) {
            try {
                setUser(JSON.parse(savedUser));
            } catch (e) {
                console.error("Failed to parse user from localStorage", e);
                localStorage.removeItem('user');
            }
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
            throw new Error(errorData.message || `HTTP error! status: ${res.status}`);
        }

        const data: AuthResponse = await res.json();

        setUser(data.user);
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
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
            throw new Error(errorData.message || `HTTP error! status: ${res.status}`);
        }

        const data: AuthResponse = await res.json();
        console.log(data)
        setUser(data.user);
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        return data.user;
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{user, login, register, logout}}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;