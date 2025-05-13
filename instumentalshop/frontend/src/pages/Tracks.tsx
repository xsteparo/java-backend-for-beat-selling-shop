// src/pages/Tracks.tsx
import {FC, FormEvent, useEffect, useRef, useState} from 'react'
import {useAuth} from '../context/AuthContext'
import {Filters} from '../components/Filters'
import '../index.css'
import {TrackController} from "../controller/TrackController.tsx";
import {TrackDto} from "../dto/TrackDto.ts"; // Tailwind
import Pagination from '../components/Pagination.tsx';
import {TracksTable} from "../components/tracks/TracksTable.tsx";
import {CartItem, LicenseType} from "../dto/CartItem.tsx";
import {LicenseModal} from "../components/LicenseModal.tsx";
import {Cart} from "../components/Cart.tsx";


export const Tracks: FC = () => {
    const { role } = useAuth();

    const tabs = [
        { key: 'top',      label: 'Nejlepší beaty' },
        { key: 'trending', label: 'Na vzestupu'  },
        { key: 'new',      label: 'Novinky'      },
    ] as const;

    const [activeTab, setActiveTab]   = useState<(typeof tabs)[number]['key']>(tabs[1].key);
    const [tracks, setTracks]         = useState<TrackDto[]>([]);
    const [search, setSearch]         = useState('');
    const [page, setPage]             = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [filters, setFilters]       = useState({ genre:'', bpm:'', key:'', sort:'' });

    const [liked, setLiked] = useState<Set<string>>(new Set());
    const toggleLike = (id: string) => {
        setLiked(prev => {
            const next = new Set(prev);
            next.has(id) ? next.delete(id) : next.add(id);
            return next;
        });
    };

    const [cart, setCart] = useState<CartItem[]>([]);
    const addToCart = (track: TrackDto, license: LicenseType, price: number) =>
        setCart(c => [...c, { track, license, price }]);
    const removeFromCart = (idx: number) =>
        setCart(c => c.filter((_, i) => i !== idx));

    const [modalTrack, setModalTrack] = useState<TrackDto | null>(null);

    // ----- НОВОЕ: аудио-менеджер -----
    const audioRef = useRef<HTMLAudioElement>(null);
    const [currentTrackId, setCurrentTrackId] = useState<string | null>(null);

    const play = (id: string) => {
        const audio = audioRef.current;
        if (!audio) return;

        // если кликнули на тот же трек — пауза
        if (currentTrackId === id) {
            audio.pause();
            setCurrentTrackId(null);
            return;
        }

        // иначе — меняем источник и играем
        const track = tracks.find(t => String(t.id) === id);
        if (!track) return;

        audio.src = track.urlNonExclusive;
        audio
            .play()
            .then(() => setCurrentTrackId(id))
            .catch(err => {
                console.error('Ошибка при воспроизведении:', err);
            });
    };
    // -------------------------------------

    useEffect(() => {
        TrackController.listTracks({
            tab:        activeTab,
            search,
            genre:      filters.genre,
            tempoRange: filters.bpm,
            key:        filters.key,
            sort:       filters.sort,
            page,
            size:       10,
        })
            .then(data => {
                setTracks(data.content);
                setTotalPages(data.totalPages);
            })
            .catch(console.error);
    }, [activeTab, page, search, filters]);

    const onSearch = (e: FormEvent) => {
        e.preventDefault();
        setPage(1);
    };

    const buy    = (id: string) => {
        const track = tracks.find(t => String(t.id) === id);
        if (track) setModalTrack(track);
    };
    const remove = async (id: string) => { /* … */ };

    return (
        <main className="flex flex-col bg-gray-900 min-h-screen p-6">
            <h1 className="text-3xl text-white text-center mb-6">All beats</h1>

            {/* Tabs + Search */}
            <div className="mb-6 grid grid-cols-[1fr_auto_1fr] items-center">
                <div />
                <div className="flex space-x-4">
                    {tabs.map(t => (
                        <button
                            key={t.key}
                            className={`px-4 py-2 rounded-t-lg ${
                                activeTab === t.key
                                    ? 'bg-green-600 text-white'
                                    : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                            }`}
                            onClick={() => { setActiveTab(t.key as (typeof tabs)[number]['key']); setPage(1); }}
                        >
                            {t.label}
                        </button>
                    ))}
                </div>
                <form onSubmit={onSearch} className="justify-self-end flex space-x-2">
                    <input
                        type="text"
                        placeholder="Search…"
                        className="px-3 py-2 rounded bg-gray-800 text-white focus:outline-none"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                    />
                    <button
                        type="submit"
                        className="px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-500"
                    >
                        Go
                    </button>
                </form>
            </div>

            <div className="flex flex-1 gap-6">
                <Filters onChange={setFilters} />

                <div className="flex-1 flex flex-col">
                    {tracks.length === 0 ? (
                        <div className="flex-1 flex items-center justify-center text-gray-400">
                            No tracks found.
                        </div>
                    ) : (
                        <>
                            <TracksTable
                                tracks={tracks}
                                role={role}
                                likedSet={liked}
                                onPlay={play}
                                onBuy={buy}
                                onRemove={remove}
                                onToggleLike={toggleLike}
                                // **передаём currentTrackId, если захотите визуально отмечать играющий трек**
                                currentTrackId={currentTrackId}
                            />
                            <Pagination
                                page={page}
                                totalPages={totalPages}
                                onPageChange={setPage}
                            />
                        </>
                    )}
                </div>
            </div>

            {modalTrack && (
                <LicenseModal
                    track={modalTrack}
                    onClose={() => setModalTrack(null)}
                    onChoose={(t, lic, price) => {
                        addToCart(t, lic, price);
                        setModalTrack(null);
                    }}
                />
            )}

            {cart.length > 0 && (
                <Cart items={cart} onRemove={removeFromCart} />
            )}

            {/* Скрытый плеер */}
            <audio ref={audioRef} style={{ display: 'none' }} />
        </main>
    );
};