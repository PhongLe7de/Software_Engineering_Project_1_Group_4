import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog.tsx';
import { Button } from '@/components/ui/button.tsx';
import { Input } from '@/components/ui/input.tsx';
import { Label } from '@/components/ui/label.tsx';
import { toast } from 'sonner';
import { createBoard } from '@/services/boardService.ts';
import { Plus } from 'lucide-react';
import { useTranslation } from 'react-i18next';

const formSchema = z.object({
    boardName: z.string().min(1, 'Board name is required').max(50, 'Board name must be less than 50 characters'),
});

type FormData = z.infer<typeof formSchema>;

interface CreateBoardDialogProps {
    ownerId: number;
    onBoardCreated: () => void;
}
interface CreateBoardDialogProps {
    ownerId: number;
    onBoardCreated: () => void;
}
//TODO: ADD MESSAGE OF THE DAY FIELD
export function CreateBoardDialog({ ownerId, onBoardCreated }: CreateBoardDialogProps) {
    const [open, setOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const {t} = useTranslation();
    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm<FormData>({
        resolver: zodResolver(formSchema),
    });

    const onSubmit = async (data: FormData) => {
        setIsLoading(true);
        try {
            await createBoard({
                boardName: data.boardName,
                ownerId: ownerId,
            });
            toast.success(t('home.board_created_success', { boardName: data.boardName }));
            reset();
            onBoardCreated();
            setOpen(false);
        } catch (error) {
            toast.error(t('home.board_created_error'));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button
                    className="flex items-center gap-2"
                    data-testid="create-board-dialog-trigger"
                >
                    <Plus className="h-4 w-4" />
                    {t('home.create_board')}
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogHeader>
                        <DialogTitle>{t('home.create_new_board')}</DialogTitle>
                        <DialogDescription>
                            {t('home.create_new_board_description')}.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="boardName">{t('home.board_name')}</Label>
                            <Input
                                id="boardName"
                                placeholder={t('home.board_name_placeholder')}
                                {...register('boardName')}
                            />
                            {errors.boardName && (
                                <p className="text-sm text-destructive">{errors.boardName.message}</p>
                            )}
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit" disabled={isLoading} data-testid="create-board-submit-button">
                            {isLoading ? t('home.creating') : t('home.create_board')}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
