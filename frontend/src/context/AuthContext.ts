import {createContext} from "react";
import type {User} from "../types";

export type AuthResponse = {
    user: User;
    token: string;
}

export type registerData = {
    email: string;
    password: string;
    displayName: string;
    photoUrl: string;
    locale: string;
}

export type loginData = {
    email: string;
    password: string;
}

export type updateUserData = {
    displayName?: string;
    email?: string;
    currentPassword?: string;
    newPassword?: string;
}

export const AuthContext = createContext<{
    user: User | null;
    sidebarVisible: boolean;
    login: (userData: loginData) => Promise<User>;
    register: (userData: registerData) => Promise<User>;
    updateUser: (userId: number, userData: updateUserData) => Promise<User>;
    logout: () => void;
} | null>(null);
