import {useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { LanguageSelector } from "./LanguageSelector";

interface UserAccountSettingsProps {
    open: boolean
    onOpenChange: (open: boolean) => void
}

export function UserAccountSettings({
    open,
    onOpenChange,
}: UserAccountSettingsProps) {
    const { t } = useTranslation();
    const { updateUser, user } = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    // Reset form fields when dialog opens or user changes
    useEffect(() => {
        if (user && open) {
            setName(user.displayName);
            setEmail(user.email);
            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");
        }
    }, [user, open]);

    if (!user) {
        return null;
    }

    const handleSave = async () => {
        if (!user?.id) {
            console.error("User ID not found");
            return;
        }

        setIsLoading(true);

        try {
            // Validate password fields only if user is trying to change password
            if (newPassword || confirmPassword) {
                if (!currentPassword) {
                    toast.error(t("settings.current_password_required"));
                    setIsLoading(false);
                    return;
                }
                if (newPassword !== confirmPassword) {
                    toast.error(t('settings.passwords_dont_match'));
                    setIsLoading(false);
                    return;
                }
                if (newPassword.length < 5) {
                    toast.error(t('settings.password_too_short'));
                    setIsLoading(false);
                    return;
                }
            }

            // Build update data object (only include changed fields)
            const updateData: {
                displayName?: string;
                email?: string;
                currentPassword?: string;
                newPassword?: string;
            } = {};

            if (name !== user.displayName) {
                updateData.displayName = name;
            }

            if (email !== user.email) {
                updateData.email = email;
            }

            if (currentPassword && newPassword && confirmPassword) {
                updateData.currentPassword = currentPassword;
                updateData.newPassword = newPassword;
            }

            // Only make request if there are changes
            if (Object.keys(updateData).length === 0) {
                toast.info(t("settings.no_changes"));
                setIsLoading(false);
                return;
            }

            await updateUser(user.id, updateData);

            // Clear password fields on success
            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");

            onOpenChange(false);
        } catch (error) {
            console.error("Failed to update user:", error);
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-md max-h-[85vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>{t('settings.account_settings')}</DialogTitle>
                </DialogHeader>

                <div className="space-y-6 py-4">
                    <div className="space-y-4">
                        <div className="flex items-center">
                            <LanguageSelector />
                            <Label htmlFor="language">{t('settings.language')}</Label>
                        </div>
                    </div>

                    <Separator />

                    <div className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="name">{t('settings.name')}</Label>
                            <Input
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder={t('settings.name_placeholder')}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">{t('settings.email')}</Label>
                            <Input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder={t('settings.email_placeholder')}
                            />
                        </div>
                    </div>

                    <Separator />

                    <div className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="current-password">{t('settings.current_password')}</Label>
                            <Input
                                id="current-password"
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                                placeholder={t('settings.current_password_placeholder')}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="new-password">{t('settings.new_password')}</Label>
                            <Input
                                id="new-password"
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                placeholder={t('settings.new_password_placeholder')}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="confirm-password">{t('settings.confirm_password')}</Label>
                            <Input
                                id="confirm-password"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder={t('settings.confirm_password_placeholder')}
                            />
                        </div>
                    </div>
                </div>

                <div className="flex justify-end gap-2 pt-4">
                    <Button
                        variant="outline"
                        onClick={() => onOpenChange(false)}
                        disabled={isLoading}
                    >
                        {t('settings.cancel')}
                    </Button>
                    <Button
                        onClick={handleSave}
                        disabled={isLoading}
                    >
                        {isLoading ? t('settings.saving') : t('settings.save_changes')}
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}