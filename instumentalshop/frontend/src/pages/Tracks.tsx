// src/pages/Tracks.tsx
import {FC, FormEvent, useEffect, useRef, useState} from 'react'
import {useAuth} from '../context/AuthContext'
import {Filters} from '../components/Filters'
import '../index.css'
import {TrackController} from "../controller/newControllers/TrackController.tsx";
import {TrackDto} from "../dto/newDto/tracks/TrackDto.ts"; // Tailwind
import Pagination from '../components/Pagination.tsx';
import {TracksTable} from "../components/tracks/TracksTable.tsx";
import {LicenseModal} from "../components/LicenseModal.tsx";
import PlayerBar from "../components/PlayerBar.tsx";
import { AnimatePresence, motion } from 'framer-motion';
import { useCart } from '../context/CartContext.tsx';
import {TrackFilter} from "../dto/newDto/tracks/TrackFilter.tsx";
import {LikeController} from "../controller/newControllers/LikeController.tsx";


export const Tracks: FC = () => {
    const { role } = useAuth()
    const { addItem } = useCart()

    // ───── Tabs ─────
    const tabs = [
        { key: 'top', label: 'Nejlepší beaty' },
        { key: 'trending', label: 'Na vzestupu' },
        { key: 'new', label: 'Novinky' },
    ] as const
    const [activeTab, setActiveTab] = useState<(typeof tabs)[number]['key']>('trending')

    // ───── список треков, поиск, фильтры ─────
    const [tracks, setTracks] = useState<TrackDto[]>([])
    const [search, setSearch] = useState('')
    const [page, setPage] = useState(1)
    const [totalPages, setTotalPages] = useState(1)
    const [filters, setFilters] = useState<Pick<TrackFilter, 'genre' | 'tempoRange' | 'key' | 'sort'>>({
        genre: '',
        tempoRange: '',
        key: '',
        sort: '',
    })

    // ───── лайки ─────
    const [liked, setLiked] = useState<Set<string>>(new Set())
    // загрузка лайков из localStorage
    useEffect(() => {
        const stored = localStorage.getItem('likedTracks')
        if (stored) {
            try {
                setLiked(new Set(JSON.parse(stored)))
            } catch {}
        }
    }, [])

    const toggleLike = async (id: string) => {
        try {
            if (liked.has(id)) {
                await LikeController.unlike(Number(id))
                setLiked(prev => {
                    const next = new Set(prev)
                    next.delete(id)
                    localStorage.setItem('likedTracks', JSON.stringify(Array.from(next)))
                    return next
                })
            } else {
                await LikeController.like(Number(id))
                setLiked(prev => {
                    const next = new Set(prev)
                    next.add(id)
                    localStorage.setItem('likedTracks', JSON.stringify(Array.from(next)))
                    return next
                })
            }
        } catch (e) {
            console.error('Like toggle error:', e)
        }
    }

    // ───── модалка лицензий ─────
    const [modalTrack, setModalTrack] = useState<TrackDto | null>(null)

    // ═════════ АУДИО ═════════
    const audioRef = useRef<HTMLAudioElement>(null)
    const [currentTrack, setCurrentTrack] = useState<TrackDto | null>(null)

    const play = async (id: string) => {
        const audio = audioRef.current
        if (!audio) return
        const track = tracks.find(t => String(t.id) === id)
        if (!track) return

        // пауза / воспроизведение
        if (currentTrack?.id === track.id) {
            audio.pause()
            setCurrentTrack(null)
            return
        }

        audio.src = `/api/v1/tracks/${id}/stream`
        audio.currentTime = 0
        try {
            await audio.play()
            setCurrentTrack(track)
        } catch (e) {
            console.error(e)
        }
    }

    // закрываем бар, когда закончился трек
    useEffect(() => {
        const a = audioRef.current
        if (!a) return
        const ended = () => setCurrentTrack(null)
        a.addEventListener('ended', ended)
        return () => a.removeEventListener('ended', ended)
    }, [])

    // ───── загрузка треков ─────
    useEffect(() => {
        TrackController.listTracks(
            {
                tab: activeTab,
                search,
                genre: filters.genre,
                tempoRange: filters.tempoRange,
                key: filters.key,
                sort: filters.sort,
            },
            page,
            10
        )
            .then(data => {
                setTracks(data.content)
                setTotalPages(data.totalPages)

                if (currentTrack && !data.content.some(t => t.id === currentTrack.id)) {
                    audioRef.current?.pause()
                    setCurrentTrack(null)
                }
            })
            .catch(console.error)
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [activeTab, page, search, filters])

    const onSearch = (e: FormEvent) => {
        e.preventDefault()
        setPage(1)
    }

    const buy = (id: string) => {
        const track = tracks.find(t => String(t.id) === id)
        if (track) setModalTrack(track)
    }
    const remove = async (id: string) => {
        // реализация удаления
    }

    // ═════════ RENDER ═════════
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
                            onClick={() => {
                                setActiveTab(t.key)
                                setPage(1)
                            }}
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

            {/* контент */}
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
                                currentTrackId={currentTrack?.id?.toString() ?? null}
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

            {/* модалка лицензии */}
            {modalTrack && (
                <LicenseModal
                    track={modalTrack}
                    onClose={() => setModalTrack(null)}
                    onChoose={(t, lic, price) => {
                        addItem({ track: t, license: lic, price })
                        setModalTrack(null)
                    }}
                />
            )}

            {/* скрытый <audio> */}
            <audio ref={audioRef} preload="none" />

            {/* плеер-бар */}
            <AnimatePresence>
                {currentTrack && (
                    <motion.div
                        initial={{ y: 80, opacity: 0 }}
                        animate={{ y: 0, opacity: 1 }}
                        exit={{ y: 80, opacity: 0 }}
                        transition={{ duration: 0.25 }}
                    >
                        <PlayerBar
                            audio={audioRef.current}
                            track={{
                                title: currentTrack.name,
                                producer: currentTrack.producerUsername || '',
                            }}
                        />
                    </motion.div>
                )}
            </AnimatePresence>
        </main>
    )
}
