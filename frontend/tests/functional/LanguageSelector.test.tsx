import * as React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import "@testing-library/jest-dom/vitest";
import userEvent from "@testing-library/user-event";
import { LanguageSelector } from "../../src/components/LanguageSelector";
import { UserAccountSettings } from "../../src/components/UserAccountSettings";
import { AuthContext } from "../../src/context/AuthContext";

const mockUpdateUser = vi.fn();
const mockRegister = vi.fn();
const mockLogin = vi.fn();
const mockLogout = vi.fn();

let currentLanguage = "en";
const languageChangeListeners: Array<() => void> = [];

const translations: Record<string, Record<string, string>> = {
    en: {
        "settings.account_settings": "Account Settings",
        "settings.account_settings_description": "Change your language, username or password.",
        "settings.name": "Change Name",
    },
    ja: {
        "settings.account_settings": "アカウント設定",
        "settings.account_settings_description": "言語、ユーザー名、またはパスワードを変更します。",
        "settings.name": "名前を変更",
    },
    ru: {
        "settings.account_settings": "Настройки аккаунта",
        "settings.account_settings_description": "Измените язык, имя пользователя или пароль.",
        "settings.name": "Изменить имя",
    },
    zh: {
        "settings.account_settings": "账户设置",
        "settings.account_settings_description": "更改您的語言、使用者名稱或密碼。",
        "settings.name": "修改姓名",
    }
};

vi.mock("react-i18next", () => ({
    useTranslation: () => ({
        t: (key: string) => translations[currentLanguage][key] || key,
        i18n: {
            changeLanguage: (lang: string) => {
                currentLanguage = lang;
                languageChangeListeners.forEach(listener => listener());
                return Promise.resolve();
            },
            language: currentLanguage,
            on: (event: string, callback: () => void) => {
                if (event === 'languageChanged') {
                    languageChangeListeners.push(callback);
                }
            },
            off: (event: string, callback: () => void) => {
                if (event === 'languageChanged') {
                    const index = languageChangeListeners.indexOf(callback);
                    if (index > -1) {
                        languageChangeListeners.splice(index, 1);
                    }
                }
            },
        },
    }),
    I18nextProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

vi.mock("@/components/ui/dropdown-menu", () => ({
    DropdownMenu: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    DropdownMenuTrigger: ({ children }: { children: React.ReactNode; asChild?: boolean }) =>
        <div>{children}</div>,
    DropdownMenuContent: ({ children }: { children: React.ReactNode }) =>
        <div role="menu">{children}</div>,
    DropdownMenuItem: ({ children, onClick, className }: {
        children: React.ReactNode;
        onClick?: () => void;
        className?: string;
    }) => (
        <button role="menuitem" onClick={onClick} className={className}>
            {children}
        </button>
    ),
}));

vi.mock("@/components/ui/dialog", () => ({
    Dialog: ({ children, open }: { children: React.ReactNode; open: boolean }) =>
        open ? <div role="dialog">{children}</div> : null,
    DialogContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    DialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    DialogTitle: ({ children }: { children: React.ReactNode }) => <h2>{children}</h2>,
    DialogDescription: ({ children }: { children: React.ReactNode }) => <p>{children}</p>,
}));

vi.mock("@/components/ui/label", () => ({
    Label: ({ children, htmlFor }: { children: React.ReactNode; htmlFor?: string }) =>
        <label htmlFor={htmlFor}>{children}</label>,
}));

const mockUser = {
    id: 1,
    displayName: "TestUser",
    email: "test@example.com",
    photoUrl: "🦧",
    locale: "en"
};

const renderWithProviders = (component: React.ReactElement) => {
    return render(
        <AuthContext.Provider value={{
            user: mockUser,
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

describe("LanguageSelector Component", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockUpdateUser.mockResolvedValue(undefined);
        currentLanguage = "en";
        languageChangeListeners.length = 0;
    });

    it("should render all language options", () => {
        renderWithProviders(<LanguageSelector />);

        expect(screen.getByText(/English/)).toBeInTheDocument();
        expect(screen.getByText(/日本語/)).toBeInTheDocument();
        expect(screen.getByText(/Русский/)).toBeInTheDocument();
        expect(screen.getByText(/中文/)).toBeInTheDocument();
    });

    it("should change language to Japanese and update user locale", async () => {
        const user = userEvent.setup();
        const { rerender } = renderWithProviders(<UserAccountSettings open={true} onOpenChange={() => {}} />);

        expect(screen.getByText("Account Settings")).toBeInTheDocument();
        expect(screen.getByText("Change your language, username or password.")).toBeInTheDocument();
        expect(screen.getByText("Change Name")).toBeInTheDocument();

        const japaneseButton = screen.getByRole("menuitem", { name: /日本語/ });
        await user.click(japaneseButton);

        rerender(
            <AuthContext.Provider value={{
                user: mockUser,
                sidebarVisible: false,
                register: mockRegister,
                login: mockLogin,
                updateUser: mockUpdateUser,
                logout: mockLogout
            }}>
                <UserAccountSettings open={true} onOpenChange={() => {}} />
            </AuthContext.Provider>
        );

        await waitFor(() => {
            expect(screen.getByText("アカウント設定")).toBeInTheDocument();
            expect(screen.getByText("言語、ユーザー名、またはパスワードを変更します。")).toBeInTheDocument();
            expect(screen.getByText("名前を変更")).toBeInTheDocument();
        });

        expect(mockUpdateUser).toHaveBeenCalledWith({ locale: "ja" });
    });

    it("should change language to Chinese and update user locale", async () => {
        const user = userEvent.setup();
        const { rerender } = renderWithProviders(<UserAccountSettings open={true} onOpenChange={() => {}} />);

        const chineseButton = screen.getByRole("menuitem", { name: /中文/ });
        await user.click(chineseButton);

        rerender(
            <AuthContext.Provider value={{
                user: mockUser,
                sidebarVisible: false,
                register: mockRegister,
                login: mockLogin,
                updateUser: mockUpdateUser,
                logout: mockLogout
            }}>
                <UserAccountSettings open={true} onOpenChange={() => {}} />
            </AuthContext.Provider>
        );

        await waitFor(() => {
            expect(screen.getByText("账户设置")).toBeInTheDocument();
            expect(screen.getByText("更改您的語言、使用者名稱或密碼。")).toBeInTheDocument();
            expect(screen.getByText("修改姓名")).toBeInTheDocument();
        });

        expect(mockUpdateUser).toHaveBeenCalledWith({ locale: "zh" });
    });

    it("should change language to Russian and update user locale", async () => {
        const user = userEvent.setup();
        const { rerender } = renderWithProviders(<UserAccountSettings open={true} onOpenChange={() => {}} />);

        const russianButton = screen.getByRole("menuitem", { name: /Русский/ });
        await user.click(russianButton);

        rerender(
            <AuthContext.Provider value={{
                user: mockUser,
                sidebarVisible: false,
                register: mockRegister,
                login: mockLogin,
                updateUser: mockUpdateUser,
                logout: mockLogout
            }}>
                <UserAccountSettings open={true} onOpenChange={() => {}} />
            </AuthContext.Provider>
        );

        await waitFor(() => {
            expect(screen.getByText("Настройки аккаунта")).toBeInTheDocument();
            expect(screen.getByText("Измените язык, имя пользователя или пароль.")).toBeInTheDocument();
            expect(screen.getByText("Изменить имя")).toBeInTheDocument();
        });

        expect(mockUpdateUser).toHaveBeenCalledWith({ locale: "ru" });
    });

    it("should change language to English and update user locale", async () => {
        const user = userEvent.setup();
        const { rerender } = renderWithProviders(<UserAccountSettings open={true} onOpenChange={() => {}} />);

        const englishButton = screen.getByRole("menuitem", { name: /English/ });
        await user.click(englishButton);

        rerender(
            <AuthContext.Provider value={{
                user: mockUser,
                sidebarVisible: false,
                register: mockRegister,
                login: mockLogin,
                updateUser: mockUpdateUser,
                logout: mockLogout
            }}>
                <UserAccountSettings open={true} onOpenChange={() => {}} />
            </AuthContext.Provider>
        );

        await waitFor(() => {
            expect(screen.getByText("Account Settings")).toBeInTheDocument();
            expect(screen.getByText("Change your language, username or password.")).toBeInTheDocument();
            expect(screen.getByText("Change Name")).toBeInTheDocument();
        });

        expect(mockUpdateUser).toHaveBeenCalledWith({ locale: "en" });
    });
});