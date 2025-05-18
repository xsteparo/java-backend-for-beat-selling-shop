import {FC, useEffect, useRef, useState} from "react";
import {ChatWebSocketService} from "../controller/newControllers/chat/ChatWebSocketService.tsx";
import {useAuth} from "../context/AuthContext.tsx";
import {ChatRoomDto} from "../dto/newDto/chat/ChatRoomDto.tsx";
import {ChatMessageDto} from "../dto/newDto/chat/ChatMessageDto.tsx";
import {ChatController} from "../controller/newControllers/chat/ChatController.tsx";
import {useParams} from "react-router-dom";

const wsService = new ChatWebSocketService();

const Chats: FC = () => {
    const { role } = useAuth();
    const [rooms, setRooms] = useState<ChatRoomDto[]>([]);
    const [activeRoom, setActiveRoom] = useState<ChatRoomDto | null>(null);
    const [messages, setMessages] = useState<ChatMessageDto[]>([]);
    const [input, setInput] = useState<string>('');
    const scrollRef = useRef<HTMLDivElement>(null);
    const { roomId } = useParams<{ roomId: string }>();

    useEffect(() => {
        if (!roomId) return;
        const id = Number(roomId);

        // 1) Если комната уже загружена в rooms (например, кликом или другими способами), просто активируем её:
        const existing = rooms.find(r => r.id === id);
        if (existing) {
            setActiveRoom(existing);
        } else {
            // 2) Иначе просим бэкенд открыть/создать эту комнату
            ChatController.openRoom(id)
                .then(roomDto => {
                    setActiveRoom(roomDto);
                    // чтобы в будущем её можно было выбирать из списка
                    setRooms(prev => [...prev, roomDto]);
                })
                .catch(console.error);
        }
    }, [roomId, rooms]);


    // 1) Устанавливаем WS-соединение один раз при монтировании
    useEffect(() => {
        wsService.connect();
        return () => {
            wsService.disconnect();
        };
    }, []);

    // 2) Загружаем список комнат
    useEffect(() => {
        ChatController.listRooms()
            .then(setRooms)
            .catch(console.error);
    }, []);

    // 3) При смене активной комнаты: отписка, загрузка истории, подписка с retry
    useEffect(() => {
        if (!activeRoom) return;

        // Отписываемся от предыдущей комнаты
        wsService.unsubscribe(activeRoom.id);

        // Загружаем историю
        ChatController.getMessages(activeRoom.id)
            .then(msgs => {
                setMessages(msgs);
                setTimeout(() => scrollRef.current?.scrollTo(0, scrollRef.current.scrollHeight), 50);
            })
            .catch(console.error);

        // Подписываемся на новые сообщения (с повторной попыткой)
        const subscribeWithRetry = () => {
            try {
                wsService.subscribe(activeRoom.id, msg => {
                    setMessages(prev => [...prev, msg]);
                    scrollRef.current?.scrollTo(0, scrollRef.current.scrollHeight);
                });
            } catch (err) {
                console.warn('WS not connected yet, retrying in 500ms', err);
                setTimeout(subscribeWithRetry, 500);
            }
        };
        subscribeWithRetry();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [activeRoom]);

    // Отправка сообщения
    const sendMessage = async () => {
        if (!activeRoom || !input.trim()) return;
        const content = input.trim();
        setInput('');
        try {
            // Сохраняем в БД
            const saved: ChatMessageDto = await ChatController.postMessage(activeRoom.id, { content });
            // Добавляем в UI
            setMessages(prev => [...prev, saved]);
            // Рассылаем по WebSocket (для других участников)
            wsService.send(activeRoom.id, content);
            // автоскролл
            scrollRef.current?.scrollTo(0, scrollRef.current.scrollHeight);
        } catch (e) {
            console.error('Send message failed', e);
        }
    };

    return (
        <main className="flex h-screen bg-gray-900 text-white">
            {/* Список комнат */}
            <aside className="w-1/4 border-r border-gray-700 p-4 overflow-y-auto">
                <h2 className="text-xl mb-4">Chats</h2>
                {rooms.map(room => {
                    const other = room.participants.find(p => p.username !== role) ?? room.participants[0];
                    return (
                        <button
                            key={room.id}
                            onClick={() => setActiveRoom(room)}
                            className={`block w-full text-left px-3 py-2 rounded mb-2 hover:bg-gray-800 ${
                                activeRoom?.id === room.id ? 'bg-gray-800' : ''
                            }`}
                        >
                            {other.username}
                        </button>
                    );
                })}
            </aside>

            {/* Окно чата */}
            <div className="flex-1 flex flex-col">
                {activeRoom ? (
                    <>
                        <header className="p-4 border-b border-gray-700">
                            <h3 className="text-lg">
                                Conversation with{' '}
                                {activeRoom.participants.find(p => p.username !== role)?.username}
                            </h3>
                        </header>
                        <div ref={scrollRef} className="flex-1 p-4 overflow-y-auto space-y-2">
                            {messages.map(msg => (
                                <div
                                    key={msg.id}
                                    className={`max-w-xs p-2 rounded ${
                                        msg.senderUsername === role
                                            ? 'bg-green-600 self-end'
                                            : 'bg-gray-800 self-start'
                                    }`}
                                >
                                    <div className="text-sm font-semibold">{msg.senderUsername}</div>
                                    <div>{msg.content}</div>
                                    <div className="text-xs text-gray-400 mt-1">
                                        {new Date(msg.sentAt).toLocaleString()}
                                    </div>
                                </div>
                            ))}
                        </div>
                        <footer className="p-4 border-t border-gray-700 flex">
                            <input
                                type="text"
                                value={input}
                                onChange={e => setInput(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && sendMessage()}
                                placeholder="Type a message…"
                                className="flex-1 px-3 py-2 bg-gray-800 rounded-l focus:outline-none"
                            />
                            <button
                                onClick={sendMessage}
                                className="px-4 py-2 bg-blue-600 rounded-r hover:bg-blue-500"
                            >
                                Send
                            </button>
                        </footer>
                    </>
                ) : (
                    <div className="flex-1 flex items-center justify-center text-gray-400">
                        Select a chat to start messaging
                    </div>
                )}
            </div>
        </main>
    );
};

export default Chats;