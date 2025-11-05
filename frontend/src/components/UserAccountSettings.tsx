import { useState } from "react";
import { useTranslation } from "react-i18next";

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
    user: {
        name: string
        email: string
        avatar: string
    }
    avatars?: string[]
}

export function UserAccountSettings({
    open,
    onOpenChange,
    user,
}: UserAccountSettingsProps) {
    const { t } = useTranslation();
    const [name, setName] = useState(user.name);
    const [email, setEmail] = useState(user.email);
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const handleSave = () => {
        // Implement save logic here
        console.log("Saving settings:", { name, email, currentPassword, newPassword });
        onOpenChange(false);
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
                    <Button variant="outline" onClick={() => onOpenChange(false)}>
                        {t('settings.cancel')}
                    </Button>
                    <Button onClick={handleSave}>{t('settings.save_changes')}</Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}