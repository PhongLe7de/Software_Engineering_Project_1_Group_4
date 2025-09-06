import type {DrawingEvent} from "@/types.ts";
import {Client} from "@stomp/stompjs";
import {useEffect, useRef, useState} from "react";

// TODO: api destinations for broadcasting
const useWebSocket = () => {
    const clientRef = useRef<Client | null>(null);
    const [isConnected, setIsConnected] = useState(false);

    // https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html
    // Initialize stomp client and add subs and publish routes
    useEffect(() => {
        const client = new Client({
            brokerURL: import.meta.env.WS_API_URL,
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
        console.log("Sending event: ", event);
    };

    const sendCursorPosition = (displayName: string, x: number, y: number) => {
    };

    return { isConnected, sendDrawingEvent, sendCursorPosition };
}

export default useWebSocket;