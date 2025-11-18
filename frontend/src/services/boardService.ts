import type {BoardDto, CreateBoardRequest} from '@/types';

const API_URL = import.meta.env.VITE_API_URL;

const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        ...(token && {'Authorization': `Bearer ${token}`}),
    };
};

export const fetchAllBoards = async (): Promise<BoardDto[]> => {
    const response = await fetch(`${API_URL}api/board/all`, {
        method: 'GET',
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error(`Failed to fetch boards: ${response.statusText}`);
    }

    return response.json();
};

export const createBoard = async (request: CreateBoardRequest): Promise<BoardDto> => {
    const response = await fetch(`${API_URL}api/board/create`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(request),
    });

    if (!response.ok) {
        throw new Error(`Failed to create board: ${response.statusText}`);
    }

    return response.json();
};

export const joinBoard = async (boardId: number, userId: number): Promise<BoardDto> => {
    const response = await fetch(`${API_URL}api/board/${boardId}/edit/addUser`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({userId}),
    });

    if (!response.ok) {
        throw new Error(`Failed to join board: ${response.statusText}`);
    }

    return response.json();
};

export const leaveBoard = async (boardId: number, userId: number): Promise<BoardDto> => {
    const response = await fetch(`${API_URL}api/board/${boardId}/edit/removeUser`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({userId}),
    });

    if (!response.ok) {
        throw new Error(`Failed to leave board: ${response.statusText}`);
    }

    return response.json();
};

export const updateBoard = async (boardId: number, boardName: string): Promise<BoardDto> => {
    const response = await fetch(`${API_URL}api/board/update/${boardId}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify({boardName}),
    });

    if (!response.ok) {
        throw new Error(`Failed to update board: ${response.statusText}`);
    }

    return response.json();
};

export const getBoardById = async (boardId: number): Promise<BoardDto> => {
    const response = await fetch(`${API_URL}api/board/${boardId}`, {
        method: 'GET',
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error(`Failed to fetch board: ${response.statusText}`);
    }

    return response.json();
};
