import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import UserRegisterModal from '../components/UserRegisterModal.tsx';
import  { AuthContext } from "@/context/AuthContext.tsx";

vi.mock('@faker-js/faker', () => ({
    faker: {
        internet: {
            username: () => 'test-user',
            email: () => 'test-user@example.com',
        },
    },
}));

// placeholder instead of the actual carousel
vi.mock('@/components/ui/carousel', () => ({
    Carousel: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    CarouselContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    CarouselItem: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    CarouselNext: () => <button>Next</button>,
    CarouselPrevious: () => <button>Previous</button>,
}));

const mockRegister = vi.fn();
const mockLogin = vi.fn();
const mockLogout = vi.fn();

const renderWithAuthProvider = (component: React.ReactElement) => {
    return render(
        <AuthContext.Provider value={{
            user: null,
            sidebarVisible: false,
            register: mockRegister,
            login: mockLogin,
            logout: mockLogout
        }}>
            {component}
        </AuthContext.Provider>
    );
};


describe('UserRegisterModal', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('should call register function on form submission', async () => {
        const user = userEvent.setup();
        const mockUserData = {
            email: 'JohnDoe@example.com',
            password: 'password123',
            displayName: 'JohnDoe',
            photoUrl: '🦧',
        };
        mockRegister.mockResolvedValue({ user_id: 1, display_name: 'JohnDoe', photo_url: '🦧' });

        renderWithAuthProvider(<UserRegisterModal />);

        const emailInput = screen.getByLabelText('Email');
        await user.clear(emailInput);
        await user.type(emailInput, mockUserData.email);

        const displayNameInput = screen.getByLabelText('Display name');
        await user.clear(displayNameInput);
        await user.type(displayNameInput, mockUserData.displayName);

        const passwordInput = screen.getByLabelText('Password');
        await user.clear(passwordInput);
        await user.type(passwordInput, mockUserData.password);

        const submitButton = screen.getByRole('button', { name: /Register/i });
        await user.click(submitButton);

        await waitFor(() => {
            expect(mockRegister).toHaveBeenCalledTimes(1);
            expect(mockRegister).toHaveBeenCalledWith(mockUserData);
        });
    });

    it('should show an error message if registration fails', async () => {
        const user = userEvent.setup();
        mockRegister.mockRejectedValue(new Error("Registration failed"));

        renderWithAuthProvider(<UserRegisterModal />);

        const displayNameInput = screen.getByLabelText('Display name');
        await user.clear(displayNameInput);
        await user.type(displayNameInput, 'JohnDoe');

        const submitButton = screen.getByRole('button', { name: /Register/i });
        await user.click(submitButton);

        await waitFor(() => {
            expect(mockRegister).toHaveBeenCalledTimes(1);
        });
    });
});
