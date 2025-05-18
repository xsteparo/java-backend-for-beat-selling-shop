import {FC, useEffect, useRef, useState} from "react";
import {ChatWebSocketService} from "../controller/newControllers/chat/ChatWebSocketService.tsx";
import {useAuth} from "../context/AuthContext.tsx";
import {ChatRoomDto} from "../dto/newDto/chat/ChatRoomDto.tsx";
import {ChatMessageDto} from "../dto/newDto/chat/ChatMessageDto.tsx";
import {ChatController} from "../controller/newControllers/chat/ChatController.tsx";
import {useParams} from "react-router-dom";

const wsService = new ChatWebSocketService();

const Chats: FC = () => {
    // 1) Теперь захватываем именно username, а не role
    const {user} = useAuth();
    const {roomId} = useParams<{ roomId: string }>();
    const [rooms, setRooms] = useState<ChatRoomDto[]>([]);
    const [activeRoom, setActiveRoom] = useState<ChatRoomDto | null>(null);
    const [messages, setMessages] = useState<ChatMessageDto[]>([]);
    const [input, setInput] = useState<string>('');
    const scrollRef = useRef<HTMLDivElement>(null);

    // 1. WS-коннект при монтировании
    useEffect(() => {
        wsService.connect();
        return () => wsService.disconnect();
    }, []);

    // 2. Список комнат
    useEffect(() => {
        ChatController.listRooms().then(setRooms).catch(console.error);
    }, []);

    // 3. Открываем комнату по URL
    useEffect(() => {
        if (!roomId) return;
        const id = Number(roomId);
        const existing = rooms.find(r => r.id === id);
        if (existing) {
            setActiveRoom(existing);
        } else {
            ChatController.openRoom(id)
                .then(roomDto => {
                    setRooms(prev => [...prev, roomDto]);
                    setActiveRoom(roomDto);
                })
                .catch(console.error);
        }
    }, [roomId, rooms]);

    // 4. При смене activeRoom: отписка, загрузка истории и подписка
    useEffect(() => {
        if (!activeRoom) return;
        wsService.unsubscribe(activeRoom.id);

        ChatController.getMessages(activeRoom.id)
            .then(msgs => {
                setMessages(msgs);
                setTimeout(() => {
                    scrollRef.current?.scrollTo(0, scrollRef.current.scrollHeight);
                }, 50);
            })
            .catch(console.error);

        wsService.subscribe(activeRoom.id, incoming => {
            setMessages(prev => {
                // 2) фильтруем по id, чтобы не было дубликатов
                if (prev.some(m => m.id === incoming.id)) return prev;
                return [...prev, incoming];
            });
            // скроллим в следующий тик
            setTimeout(() => {
                scrollRef.current?.scrollTo(0, scrollRef.current.scrollHeight);
            }, 0);
        });
    }, [activeRoom]);

    // 5. Отправка по WS — без REST
    const sendMessage = () => {
        if (!activeRoom || !input.trim()) return;
        const content = input.trim();
        setInput('');
        wsService.send(activeRoom.id, content);
        // локально не пушим — ждём прихода по WS
    };

    return (
        <main className="flex h-screen bg-gray-900 text-white">
            <aside className="w-1/4 border-r border-gray-700 p-4 overflow-y-auto">
                <h2 className="text-xl mb-4">Chats</h2>
                {rooms.map(room => {
                    // 3) Находим «другого» участника корректно
                    const other = room.participants.find(p => p.username !== user?.username)
                        ?? room.participants[0];
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

            <div className="flex-1 flex flex-col">
                {activeRoom ? (
                    <>
                        <header className="p-4 border-b border-gray-700">
                            <h3 className="text-lg">
                                {/* тоже исправлено: сравниваем на username */}
                                Conversation
                                with {activeRoom.participants.find(p => p.username !== user?.username)?.username}
                            </h3>
                        </header>
                        <div
                            ref={scrollRef}
                            className="flex-1 p-4 overflow-y-auto flex flex-col space-y-4"
                        >
                            {messages.map(msg => (
                                <div
                                    key={msg.id}
                                    className={`max-w-xs p-2 rounded  mb-4 ${
                                        msg.senderUsername === user?.username
                                            ? 'bg-green-600 self-end'
                                            : 'bg-gray-800 self-start'
                                    }`}
                                >
                                    <div className="text-sm font-semibold">{msg.senderUsername}</div>
                                    <div>{msg.content}</div>
                                    <div
                                        className={`text-xs mt-1 ${
                                            msg.senderUsername === user?.username
                                                ? 'text-gray-200'   /* на зелёном фоне — светлее */
                                                : 'text-gray-400'   /* на сером — как было */
                                        }`}
                                    >
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