import { createFileRoute } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { SidebarProvider, SidebarInset } from '@/components/ui/sidebar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ScrollArea } from '@/components/ui/scroll-area';
import { toast } from 'sonner';
import { BoardListSidebar } from '@/components/BoardListSidebar';
import { BoardCard } from '@/components/BoardCard';
import { CreateBoardDialog } from '@/components/CreateBoardDialog';
import UserRegisterModal from '@/components/UserRegisterModal';
import type { BoardDto } from '@/types';
import { fetchAllBoards, joinBoard, leaveBoard } from '@/services/boardService';
import { Loader2 } from 'lucide-react';

export const Route = createFileRoute('/home')({
    component: RouteComponent,
});

function RouteComponent() {
    const { user } = useAuth();
    const [boards, setBoards] = useState<BoardDto[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    const loadBoards = async () => {
        try {
            setIsLoading(true);
            const fetchedBoards = await fetchAllBoards();
            setBoards(fetchedBoards);
        } catch (error) {
            console.error('Failed to load boards:', error);
            toast.error('Failed to load boards. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (user) {
            loadBoards();
        }
    }, [user]);

    const handleJoinBoard = async (boardId: number) => {
        if (!user) return;
        try {
            await joinBoard(boardId, user.id);
            toast.success('Welcome!');
            await loadBoards();
        } catch (error) {
            console.error('Failed to join board:', error);
            toast.error('Failed to join board. Please try again.');
        }
    };

    const handleLeaveBoard = async (boardId: number) => {
        if (!user) return;

        try {
            await leaveBoard(boardId, user.id);
            toast.success('Left board successfully!');
            await loadBoards(); // Refresh the board list
        } catch (error) {
            console.error('Failed to leave board:', error);
            toast.error('Failed to leave board. Please try again.');
        }
    };

    const handleBoardCreated = () => {
        loadBoards(); // Refresh the board list after creating a new board
    };

    // Show login modal if not authenticated
    if (!user) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100">
                <UserRegisterModal />
            </div>
        );
    }

    // Filter boards
    const myBoards = boards.filter(
        (board) => board.userIds.includes(user.id) || board.ownerId === user.id
    );
    const allBoards = boards;

    return (
        <SidebarProvider>
            <BoardListSidebar user={user} />
            <SidebarInset>
                <div className="flex flex-1 flex-col gap-4 p-4">
                    {/* Header with Create Board button */}
                    <div className="flex items-center justify-between">
                        <div>
                            <h1 className="text-3xl font-bold tracking-tight">Board Manager</h1>
                            <p className="text-muted-foreground">
                                Manage your whiteboards and collaborate with others
                            </p>
                        </div>
                        <CreateBoardDialog ownerId={user.id} onBoardCreated={handleBoardCreated} />
                    </div>

                    {/* Tabs for My Boards and All Boards */}
                    <Tabs defaultValue="all-boards" className="flex-1">
                        <TabsList className="grid w-full max-w-md grid-cols-2">
                            <TabsTrigger value="all-boards">
                                All Boards ({allBoards.length})
                            </TabsTrigger>
                            <TabsTrigger value="my-boards">
                                My Boards ({myBoards.length})
                            </TabsTrigger>
                        </TabsList>

                        {/* My Boards Tab */}
                        <TabsContent value="my-boards" className="flex-1">
                            {isLoading ? (
                                <div className="flex items-center justify-center h-64">
                                    <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                                </div>
                            ) : myBoards.length === 0 ? (
                                <div className="flex flex-col items-center justify-center h-64 text-center">
                                    <p className="text-lg font-medium text-muted-foreground">
                                        No boards yet
                                    </p>
                                    <p className="text-sm text-muted-foreground mt-2">
                                        Create your first board to get started!
                                    </p>
                                </div>
                            ) : (
                                <ScrollArea className="h-[calc(100vh-16rem)]">
                                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 pb-4">
                                        {myBoards.map((board) => (
                                            <BoardCard
                                                key={board.id}
                                                board={board}
                                                currentUserId={user.id}
                                                onJoinBoard={handleJoinBoard}
                                                onLeaveBoard={handleLeaveBoard}
                                            />
                                        ))}
                                    </div>
                                </ScrollArea>
                            )}
                        </TabsContent>

                        {/* All Boards Tab */}
                        <TabsContent value="all-boards" className="flex-1">
                            {isLoading ? (
                                <div className="flex items-center justify-center h-64">
                                    <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                                </div>
                            ) : allBoards.length === 0 ? (
                                <div className="flex flex-col items-center justify-center h-64 text-center">
                                    <p className="text-lg font-medium text-muted-foreground">
                                        No boards available
                                    </p>
                                    <p className="text-sm text-muted-foreground mt-2">
                                        Be the first to create a board!
                                    </p>
                                </div>
                            ) : (
                                <ScrollArea className="h-[calc(100vh-16rem)]">
                                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 pb-4">
                                        {allBoards.map((board) => (
                                            <BoardCard
                                                key={board.id}
                                                board={board}
                                                currentUserId={user.id}
                                                onJoinBoard={handleJoinBoard}
                                                onLeaveBoard={handleLeaveBoard}
                                            />
                                        ))}
                                    </div>
                                </ScrollArea>
                            )}
                        </TabsContent>
                    </Tabs>
                </div>
            </SidebarInset>
        </SidebarProvider>
    );
}
