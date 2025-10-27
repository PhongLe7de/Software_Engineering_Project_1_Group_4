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
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { toast } from 'sonner';
import { createBoard } from '@/services/boardService';
import { Plus } from 'lucide-react';

const formSchema = z.object({
    boardName: z.string().min(1, 'Board name is required').max(50, 'Board name must be less than 50 characters'),
});

type FormData = z.infer<typeof formSchema>;

interface CreateBoardDialogProps {
    ownerId: number;
    onBoardCreated: () => void;
}

export function CreateBoardDialog({ ownerId, onBoardCreated }: CreateBoardDialogProps) {
    const [open, setOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

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
                ownerId,
            });
            toast.success('Board created successfully!');
            reset();
            setOpen(false);
            onBoardCreated();
        } catch (error) {
            console.error('Failed to create board:', error);
            toast.error('Failed to create board. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button size="lg">
                    <Plus className="mr-2 h-4 w-4" />
                    Create New Board
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogHeader>
                        <DialogTitle>Create New Board</DialogTitle>
                        <DialogDescription>
                            Enter a name for your new whiteboard. Click create when you're done.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="boardName">Board Name</Label>
                            <Input
                                id="boardName"
                                placeholder="My Awesome Board"
                                {...register('boardName')}
                            />
                            {errors.boardName && (
                                <p className="text-sm text-destructive">{errors.boardName.message}</p>
                            )}
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit" disabled={isLoading}>
                            {isLoading ? 'Creating...' : 'Create Board'}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
