// src/pages/Tracks.tsx
import { FC, useState, useEffect, FormEvent } from 'react'
import { useAuth } from '../context/AuthContext'
import { Filters } from '../components/Filters'
import '../index.css'
import {TrackController} from "../controller/TrackController.tsx";
import {TrackDto} from "../dto/TrackDto.ts";  // Tailwind



export const Tracks: FC = () => {
    const { role } = useAuth();

    const tabs = [
        { key: 'top',      label: 'Nejlepší beaty' },
        { key: 'trending', label: 'Na vzestupu' },
        { key: 'new',      label: 'Novinky' },
    ] as const;

    const [activeTab, setActiveTab]   = useState<typeof tabs[number]['key']>('trending');
    const [tracks, setTracks]         = useState<TrackDto[]>([]);
    const [search, setSearch]         = useState('');
    const [page, setPage]             = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [filters, setFilters]       = useState({ genre:'', bpm:'', key:'', sort:'' });

    useEffect(() => {
        async function load() {
            try {
                const data = await TrackController.listTracks({
                    tab:        activeTab,
                    search,
                    genre:      filters.genre,
                    tempoRange: filters.bpm,
                    key:        filters.key,
                    sort:       filters.sort,
                    page,
                    size: 10,
                });
                setTracks(data.content);
                setTotalPages(data.totalPages);
            } catch (err) {
                console.error(err);
            }
        }
        load();
    }, [activeTab, page, search, filters]);

    const onSearch = (e: FormEvent) => {
        e.preventDefault();
        setPage(1);
    };

    const play   = (id: string) => (document.getElementById(`audio-${id}`) as HTMLAudioElement)?.play();
    const buy    = async (id: string) => { /* … */ };
    const remove = async (id: string) => { /* … */ };

    return (
        <main className="flex flex-col bg-gray-900 min-h-screen p-6">
            <h1 className="text-3xl text-white text-center mb-6">All beats</h1>

            {/* Табы + Поиск */}
            <div className="mb-6 grid grid-cols-[1fr_auto_1fr] items-center">
                <div/>
                <div className="flex space-x-4">
                    {tabs.map(t => (
                        <button
                            key={t.key}
                            className={`px-4 py-2 rounded-t-lg ${
                                activeTab === t.key
                                    ? 'bg-green-600 text-white'
                                    : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                            }`}
                            onClick={() => { setActiveTab(t.key); setPage(1); }}
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
                {/* Фильтры */}
                <Filters onChange={setFilters} />

                {/* Список */}
                <div className="flex-1 flex flex-col">
                    {tracks.length === 0 ? (
                        <div className="flex-1 flex items-center justify-center text-gray-400">
                            No tracks found.
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 gap-4 flex-1 overflow-auto">
                            {tracks.map(t => (
                                <div
                                    key={t.id}
                                    className="grid grid-cols-[auto_auto_1fr_auto_auto_auto_auto_auto_auto] items-center bg-gray-800 p-3 rounded-lg"
                                >
                                    <button onClick={() => play(t.id)} className="p-1">▶</button>
                                    <img src="/images/note-icon.svg" alt="" className="w-6 h-6" />
                                    <div className="pl-2">
                                        <div className="text-white font-semibold">{t.name}</div>
                                        <div className="text-gray-400 text-sm">{t.producerUsername}</div>
                                    </div>
                                    <div className="text-center text-gray-300">{t.rating}</div>
                                    <div className="text-center text-gray-300">{t.genreType}</div>
                                    <div className="text-center text-gray-300">{t.length}</div>
                                    <div className="text-center text-gray-300">{t.key}</div>
                                    <div className="text-center text-gray-300">{t.bpm}</div>
                                    <div className="flex space-x-2">
                                        {role === 'admin' ? (
                                            <>
                                                <a
                                                    href={`/api/v1/tracks/${t.id}/download`}
                                                    className="px-2 py-1 bg-green-600 text-white rounded text-xs"
                                                >
                                                    Download
                                                </a>
                                                <button className="px-2 py-1 bg-yellow-500 text-black rounded text-xs">
                                                    Edit
                                                </button>
                                                <button
                                                    onClick={() => remove(t.id)}
                                                    className="px-2 py-1 bg-red-600 text-white rounded text-xs"
                                                >
                                                    Delete
                                                </button>
                                            </>
                                        ) : t.purchased ? (
                                            <a
                                                href={`/api/v1/tracks/${t.id}/download`}
                                                className="px-2 py-1 bg-green-600 text-white rounded text-xs"
                                            >
                                                Download
                                            </a>
                                        ) : (
                                            <button
                                                onClick={() => buy(t.id)}
                                                className="px-2 py-1 bg-green-500 text-white rounded text-xs"
                                            >
                                                Buy
                                            </button>
                                        )}
                                    </div>
                                    <audio id={`audio-${t.id}`} src={t.urlNonExclusive} preload="none" />
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Пагинация */}
                    <div className="flex justify-center mt-4 space-x-2">
                        <button
                            disabled={page <= 1}
                            onClick={() => setPage(page - 1)}
                            className="px-3 py-1 bg-gray-700 text-gray-300 rounded disabled:opacity-50"
                        >
                            ‹
                        </button>
                        {Array.from({ length: totalPages }, (_, i) => (
                            <button
                                key={i+1}
                                onClick={() => setPage(i+1)}
                                className={`px-3 py-1 rounded ${
                                    page === i+1
                                        ? 'bg-green-600 text-white'
                                        : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                                }`}
                            >
                                {i+1}
                            </button>
                        ))}
                        <button
                            disabled={page >= totalPages}
                            onClick={() => setPage(page + 1)}
                            className="px-3 py-1 bg-gray-700 text-gray-300 rounded disabled:opacity-50"
                        >
                            ›
                        </button>
                    </div>
                </div>
            </div>
        </main>
    );
};
