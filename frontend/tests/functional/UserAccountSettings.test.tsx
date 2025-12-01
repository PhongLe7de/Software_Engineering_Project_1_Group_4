import * as React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { UserAccountSettings } from '../../src/components/UserAccountSettings';
import { AuthContext } from "../../src/context/AuthContext";
import { toast } from "sonner";

vi.mock("sonner", () => ({
    toast: {
        error: vi.fn(),
        info: vi.fn(),
        success: vi.fn(),
    },
}));

const mockUpdateUser = vi.fn();
const mockRegister = vi.fn();
const mockLogin = vi.fn();
const mockLogout = vi.fn();

const mockUser = {
    id: 1,
    displayName: "TestUser",
    email: "test@example.com",
    photoUrl: "🦧",
    locale: "en"
};

const renderWithAuthProvider = (
    component: React.ReactElement,
    user = mockUser
) => {
    return render(
        <AuthContext.Provider value={{
            user,
            sidebarVisible: false,
            register: mockRegister,
            login: mockLogin,
            updateUser: mockUpdateUser,
            logout: mockLogout
        }}>
            {component}
        </AuthContext.Provider>
    );
};

describe("UserAccountSettings Component", () => {
    const mockOnOpenChange = vi.fn();

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("should render nothing when user is null", () => {
        const { container } = renderWithAuthProvider(
            <UserAccountSettings open={true} onOpenChange={mockOnOpenChange} />,
            null
        );
        expect(container.firstChild).toBeNull();
    });

    it("should populate name field with current user displayName", () => {
        renderWithAuthProvider(
            <UserAccountSettings open={true} onOpenChange={mockOnOpenChange} />
        );

        const nameInput = screen.getByLabelText("settings.name") as HTMLInputElement;
        expect(nameInput.value).toBe("TestUser");
    });

    it("should update username when name is changed", async () => {
        const user = userEvent.setup();
        mockUpdateUser.mockResolvedValue({});

        renderWithAuthProvider(
            <UserAccountSettings open={true} onOpenChange={mockOnOpenChange} />
        );

        const nameInput = screen.getByLabelText("settings.name");
        await user.clear(nameInput);
        await user.type(nameInput, "NewUsername");

        const saveButton = screen.getByRole('button', { name: /settings.save_changes/i });
        await user.click(saveButton);

        await waitFor(() => {
            expect(mockUpdateUser).toHaveBeenCalledTimes(1);
            expect(mockUpdateUser).toHaveBeenCalledWith({
                displayName: "NewUsername",
            });
        });
    });

    it("should update password when all password fields are filled correctly", async() => {
        const user = userEvent.setup();
        mockUpdateUser.mockResolvedValue({});

        renderWithAuthProvider(
            <UserAccountSettings open={true} onOpenChange={mockOnOpenChange} />
        );

        const currentPasswordInput = screen.getByLabelText("settings.current_password");
        await user.type(currentPasswordInput, "oldPassword123");

        const newPasswordInput = screen.getByLabelText("settings.new_password");
        await user.type(newPasswordInput, "newPassword123");

        const confirmPasswordInput = screen.getByLabelText("settings.confirm_password");
        await user.type(confirmPasswordInput, "newPassword123");

        const saveButton = screen.getByRole("button", { name: /settings.save_changes/i });
        await user.click(saveButton);

        await waitFor(() => {
            expect(mockUpdateUser).toHaveBeenCalledTimes(1);
            expect(mockUpdateUser).toHaveBeenCalledWith({
                password: "newPassword123",
            });
        });
    });

    it("should show error when passwords do not match", async () => {
        const user = userEvent.setup();

        renderWithAuthProvider(
            <UserAccountSettings open={true} onOpenChange={mockOnOpenChange} />
        );

        const currentPasswordInput = screen.getByLabelText("settings.current_password");
        await user.type(currentPasswordInput, "oldPassword123");

        const newPasswordInput = screen.getByLabelText("settings.new_password");
        await user.type(newPasswordInput, "newPassword123");

        const confirmPasswordInput = screen.getByLabelText("settings.confirm_password");
        await user.type(confirmPasswordInput, "differentPassword");

        const saveButton = screen.getByRole("button", { name: /settings.save_changes/i });
        await user.click(saveButton);

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith("settings.passwords_dont_match");
            expect(mockUpdateUser).not.toHaveBeenCalled();
        });
    });
});