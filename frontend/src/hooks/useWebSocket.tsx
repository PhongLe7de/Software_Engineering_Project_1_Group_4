import type { DrawingEvent } from "@/types.ts";
import { Client } from "@stomp/stompjs";
import { useEffect, useRef, useState } from "react";

type UseWebSocketProps = {
    sidebarVisible: boolean;
    userData: { user_id: number; display_name: string; photo_url: string } | undefined;
};

export interface CursorPosition {
    display_name: string;
    photo_url: string;
    x: number;
    y: number;
}

const useWebSocket = ({ sidebarVisible, userData }: UseWebSocketProps) => {
    const clientRef = useRef<Client | null>(null);
    const [isConnected, setIsConnected] = useState(false);
    const [remoteEvents, setRemoteEvents] = useState<DrawingEvent[]>([]);
    const [remoteCursors, setRemoteCursors] = useState<Map<string, CursorPosition>>(new Map());

    // Initialize STOMP client and subscriptions
    useEffect(() => {
        const client = new Client({
            brokerURL: import.meta.env.VITE_WS_API_URL, // e.g. ws://localhost:8084/ws
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            console.log("Connected to STOMP");
            setIsConnected(true);

            // Subscribe to drawing events
            client.subscribe("/topic/draw", (message) => {
                const event: DrawingEvent = JSON.parse(message.body);
                console.log("Received draw event:", event);
                setRemoteEvents((prevEvents) => [...prevEvents, event]);
            });

            // Subscribe to cursor updates
            client.subscribe("/topic/cursor", (message) => {
                const cursorData: CursorPosition = JSON.parse(message.body);
                console.log("Received cursor event:", cursorData);
                setRemoteCursors((prev) => {
                    const newMap = new Map(prev);
                    newMap.set(cursorData.display_name, cursorData);
                    return newMap;
                });
            });
        };

        client.onDisconnect = () => {
            console.log("Disconnected from STOMP");
            setIsConnected(false);
        };

        client.onStompError = (frame) => {
            console.error("STOMP Error:", frame.headers?.message, frame.body);
        };

        client.activate();
        clientRef.current = client;

        // Cleanup on unmount
        return () => {
            client.deactivate();
        };
    }, []);

    // Publish a drawing event to backend (/app/draw)
    const sendDrawingEvent = (event: DrawingEvent) => {
        if (clientRef.current?.connected) {
            clientRef.current.publish({
                destination: "/app/draw",
                body: JSON.stringify(event),
            });
            console.log("Sent draw event:", event);
        }
    };

    // Publish cursor position to backend (/app/cursor)
    const sendCursorPosition = (x: number, y: number) => {
        if (clientRef.current?.connected && sidebarVisible && userData) {
            const payload: CursorPosition = {
                display_name: userData.display_name,
                photo_url: userData.photo_url,
                x,
                y,
            };
            clientRef.current.publish({
                destination: "/app/cursor",
                body: JSON.stringify(payload),
            });
            console.log("Sent cursor position:", payload);
        }
    };

    return {
        isConnected,
        remoteEvents,
        remoteCursors: Array.from(remoteCursors.values()), // convert Map -> Array
        sendDrawingEvent,
        sendCursorPosition,
    };
};

export default useWebSocket;
