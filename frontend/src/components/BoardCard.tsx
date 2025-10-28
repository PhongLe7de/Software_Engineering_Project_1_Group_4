import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { BoardDto } from '@/types';
import { Users, Pencil, LogOut } from 'lucide-react';
import { useNavigate } from '@tanstack/react-router';

interface BoardCardProps {
    board: BoardDto;
    currentUserId: number;
    onJoinBoard: (boardId: number) => void;
    onLeaveBoard: (boardId: number) => void;
}

// Has all the board info, used to navigate into a board
export function BoardCard({ board, currentUserId, onJoinBoard, onLeaveBoard }: BoardCardProps) {
    const navigate = useNavigate();
    const isOwner = board.ownerId === currentUserId;
    const isMember = board.userIds.includes(currentUserId);

    // Navigate to joined board
    const handleJoinBoard = async () => {
        await onJoinBoard(board.id);
        navigate({ to: '/board/$boardId', params: { boardId: board.id.toString() } });
    };

    const handleOpenBoard = () => {
        navigate({ to: '/board/$boardId', params: { boardId: board.id.toString() } });
    };

    return (
        <Card className="hover:shadow-lg transition-shadow">
            <CardHeader>
                <div className="flex items-start justify-between">
                    <CardTitle className="text-xl">{board.boardName}</CardTitle>
                    {isOwner && (
                        <Badge variant="default">Owner</Badge>
                    )}
                </div>
                <CardDescription>
                    Board #{board.id}
                </CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Users className="h-4 w-4" />
                    <span>{board.amountOfUsers} {board.amountOfUsers === 1 ? 'member' : 'members'}</span>
                </div>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Pencil className="h-4 w-4" />
                    <span>{board.numberOfStrokes} {board.numberOfStrokes === 1 ? 'stroke' : 'strokes'}</span>
                </div>
            </CardContent>
            <CardFooter className="flex gap-2">
                {isMember ? (
                    <Button
                        onClick={handleOpenBoard}
                        className="flex-1"
                    >
                        Open Board
                    </Button>
                ) : (
                    <Button
                        onClick={handleJoinBoard}
                        className="flex-1"
                    >
                        Join Board
                    </Button>
                )}
                {isMember && !isOwner && (
                    <Button
                        onClick={() => onLeaveBoard(board.id)}
                        variant="outline"
                        size="icon"
                    >
                        <LogOut className="h-4 w-4" />
                    </Button>
                )}
            </CardFooter>
        </Card>
    );
}
