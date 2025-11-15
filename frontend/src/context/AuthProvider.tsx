import {useEffect, useState} from "react";
import type {User} from "../types";
import {toast} from "sonner";
import {AuthContext, type AuthResponse, type registerData, type loginData} from "./AuthContext";

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

    const logout = () => {
        setUser(null);
        setSidebarVisible(false);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{user, sidebarVisible, login, register, logout}}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;
