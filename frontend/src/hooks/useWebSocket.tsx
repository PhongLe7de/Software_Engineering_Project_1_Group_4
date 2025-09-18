import type {DrawingEvent} from "@/types.ts";
import {Client} from "@stomp/stompjs";
import {useEffect, useRef, useState} from "react";

type UseWebSocketProps = {
    sidebarVisible: boolean;
    userData: {user_id: number, display_name: string, photo_url: string  } | undefined;
};
export interface CursorPosition {
    display_name: string;
    photo_url: string;
    x: number;
    y: number;
}
const useWebSocket = ({sidebarVisible, userData}: UseWebSocketProps) => {
    const clientRef = useRef<Client | null>(null);
    const [isConnected, setIsConnected] = useState(false);
    const [remoteEvents, setRemoteEvents] = useState<DrawingEvent[]>([]);
    const [remoteCursors, setRemoteCursors] = useState<Map<string, CursorPosition>>(new Map());


    // https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html
    // Initialize stomp client and add subs and publish routes
    useEffect(() => {
        const client = new Client({
            brokerURL: import.meta.env.VITE_WS_API_URL,
            // debug: function (str) {
            //     console.log("STOMP Debug:", str);
            // },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            console.log("Connected to STOMP");
            setIsConnected(true);

            client.subscribe(`topic/draw`, (message) =>{
                const event = JSON.parse(message.body);
                console.log("Received event: ", event);
                setRemoteEvents(prevEvents => [...prevEvents, event]);
            })

            // TODO: initial destination, no backend impl yet
            client.subscribe("/topic/cursors", (message) => {
                const cursorData: CursorPosition = JSON.parse(message.body);
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
            console.error("STOMP Error:", frame);
        };

        client.activate();
        clientRef.current = client;

        // Cleanup on unmount
        return () => {
            client.deactivate();
        };
    }, []);

    const sendDrawingEvent = (event: DrawingEvent) => {
        if (clientRef.current?.connected) {
            clientRef.current.publish({
                destination: "app/draw",
                body: JSON.stringify(event),
            });
        }
    };

    // TODO: initial destination, no backend impl yet
    const sendCursorPosition = (x: number, y: number) => {
        if (clientRef.current?.connected && sidebarVisible && userData) { // start broadcasting cursor pos only after user is created
            clientRef.current.publish({
                destination: "app/cursor",
                body: JSON.stringify({ display_name: userData.display_name, photo_url: userData.photo_url, x, y }),
            });
        }
    };

    return { isConnected, remoteEvents, remoteCursors: Array.from(remoteCursors.values()), sendDrawingEvent, sendCursorPosition };
}

export default useWebSocket;