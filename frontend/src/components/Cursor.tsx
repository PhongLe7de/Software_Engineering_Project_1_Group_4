import type {CursorPosition} from "@/hooks/useWebSocket";
import {useEffect, useState} from "react";

interface CursorsProps {
    cursors: CursorPosition[];
    userData: { user_id: number, display_name: string; photo_url: string; } | undefined;
}

const Cursor = ({display_name, photo_url, x, y}: CursorPosition) => {
    const [userColour, setUserColor] = useState('#000000');
    const colors = [
        '#FF6B6B',
        '#4ECDC4',
        '#45B7D1',
        '#96CEB4',
        '#FFEAA7',
        '#DDA0DD',
        '#98D8C8',
        '#F7DC6F',
        '#BB8FCE',
        '#85C1E9',
    ];

    useEffect(() => {
        setUserColor(colors[Math.floor(Math.random() * colors.length)] || '#000000');
    }, [display_name]);

    const userName = display_name ? display_name.replace('user-', 'User ') : 'Unknown User';
    return (
        <div
            className="absolute pointer-events-none z-50 transition-all duration-75 ease-out"
            style={{
                left: x,
                top: y,
                transform: 'translate(-2px, -2px)',
            }}
        >
            {/* Cursor pointer */}
            <div className="relative">
                <svg
                    width="20"
                    height="20"
                    viewBox="0 0 20 20"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        d="M2 2L18 8L8 10L2 18L2 2Z"
                        fill={userColour}
                        stroke="white"
                        strokeWidth="1"
                    />
                </svg>

                {/* User name label & avatar */}
                <div
                    className="absolute top-5 left-2 px-2 py-1 rounded text-xs font-medium text-white whitespace-nowrap flex items-center gap-1"
                    style={{
                        backgroundColor: userColour,
                        fontSize: '20px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                    }}
                >
                    {photo_url && (
                        <div
                            className=" flex items-center justify-center text-xs"
                            style={{fontSize: '45px'}}
                        >
                            {photo_url}
                        </div>
                    )}
                    {userName}
                </div>
            </div>
        </div>
    );
};

const Cursors = ({userData, cursors}: CursorsProps) => {
    // Filter out the current user's cursor
    const remoteCursors = cursors.filter(cursor =>
        cursor.display_name !== userData?.display_name
    );
    return (
        <div className="absolute inset-0 pointer-events-none z-40">
            {cursors.map((cursor, index) => (
                <Cursor
                    key={`${cursor.display_name}-${index}`}
                    display_name={cursor.display_name}
                    x={cursor.x}
                    y={cursor.y}
                    photo_url={cursor.photo_url}
                />
            ))}
        </div>
    );
};
export default Cursors;
