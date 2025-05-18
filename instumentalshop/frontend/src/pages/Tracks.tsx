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
    const [activeTab, setActiveTab] = useState<typeof tabs[number]['key']>('trending')

    // ───── list, search, filters ─────
    const [tracks, setTracks] = useState<TrackDto[]>([])
    const [search, setSearch] = useState<string>('')
    const [page, setPage] = useState<number>(1)
    const [totalPages, setTotalPages] = useState<number>(1)
    const [filters, setFilters] = useState<Pick<TrackFilter, 'genre' | 'tempoRange' | 'key' | 'sort'>>({
        genre: '',
        tempoRange: '',
        key: '',
        sort: '',
    })

    // ───── likes ─────
    const [liked, setLiked] = useState<Set<string>>(new Set())

    // ───── modal track ─────
    const [modalTrack, setModalTrack] = useState<TrackDto | null>(null)

    // ═════════ AUDIO ═════════
    const audioRef = useRef<HTMLAudioElement>(null)
    const [currentTrack, setCurrentTrack] = useState<TrackDto | null>(null)

    // ───── load tracks & likes ─────
    useEffect(() => {
        let mounted = true
        Promise.all([
            TrackController.listTracks(
                {
                    tab: activeTab,
                    search: search || undefined,
                    genre: filters.genre || undefined,
                    tempoRange: filters.tempoRange || undefined,
                    key: filters.key || undefined,
                    sort: filters.sort || undefined,
                },
                page,
                10
            ),
            LikeController.getMyLikes(),
        ])
            .then(([pageData, likedIds]) => {
                if (!mounted) return
                setTracks(pageData.content)
                setTotalPages(pageData.totalPages)
                setLiked(new Set(likedIds.map(String)))

                if (currentTrack && !pageData.content.some(t => t.id === currentTrack.id)) {
                    audioRef.current?.pause()
                    setCurrentTrack(null)
                }
            })
            .catch(console.error)

        return () => { mounted = false }
    }, [activeTab, page, search, filters])

    // ───── toggle like ─────
    const toggleLike = async (id: string) => {
        const trackId = Number(id)
        if (liked.has(id)) {
            // optimistic
            setLiked(prev => { const next = new Set(prev); next.delete(id); return next })
            try {
                await LikeController.unlike(trackId)
            } catch (e) {
                console.error('Unlike failed', e)
                // rollback
                setLiked(prev => { const next = new Set(prev); next.add(id); return next })
            }
        } else {
            setLiked(prev => { const next = new Set(prev); next.add(id); return next })
            try {
                await LikeController.like(trackId)
            } catch (e) {
                console.error('Like failed', e)
                // rollback
                setLiked(prev => { const next = new Set(prev); next.delete(id); return next })
            }
        }
    }

    // ═════════ audio controls ═════════
    const play = async (id: string) => {
        const audio = audioRef.current
        if (!audio) return
        const track = tracks.find(t => String(t.id) === id)
        if (!track) return

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

    useEffect(() => {
        const audio = audioRef.current
        if (!audio) return
        const onEnded = () => setCurrentTrack(null)
        audio.addEventListener('ended', onEnded)
        return () => audio.removeEventListener('ended', onEnded)
    }, [])

    // ═════════ render ═════════
    const onSearch = (e: FormEvent) => {
        e.preventDefault()
        setPage(1)
    }

    const buy = (id: string) => {
        const track = tracks.find(t => String(t.id) === id)
        if (track) setModalTrack(track)
    }

    const remove = async (id: string) => {
        // TODO: implement remove
    }

    return (
        <main className="flex flex-col bg-gray-900 min-h-screen p-6">
            <h1 className="text-3xl text-white text-center mb-6">All beats</h1>

            {/* Tabs + Search */}
            <div className="mb-6 grid grid-cols-[1fr_auto_1fr] items-center">
                <div />
                <div className="flex space-x-4">
                    {tabs.map(tab => (
                        <button
                            key={tab.key}
                            className={`px-4 py-2 rounded-t-lg ${
                                activeTab === tab.key
                                    ? 'bg-green-600 text-white'
                                    : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                            }`}
                            onClick={() => {
                                setActiveTab(tab.key)
                                setPage(1)
                            }}
                        >
                            {tab.label}
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
                    <button type="submit" className="px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-500">
                        Go
                    </button>
                </form>
            </div>

            {/* content */}
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
                            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
                        </>
                    )}
                </div>
            </div>

            {/* license modal */}
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

            {/* hidden audio */}
            <audio ref={audioRef} preload="none" />

            {/* player bar */}
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
                            track={{ title: currentTrack.name, producer: currentTrack.producerUsername || '' }}
                        />
                    </motion.div>
                )}
            </AnimatePresence>
        </main>
    )
}
