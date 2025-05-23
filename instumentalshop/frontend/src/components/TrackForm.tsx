import { FC, FormEvent, useState, useEffect } from 'react'
import { TrackRequestDto } from '../dto/TrackRequestDto'
import { GenreType } from '../dto/newDto/enums/GenreType'
import { TrackDto } from '../dto/newDto/tracks/TrackDto'
import { TrackController } from '../controller/newControllers/TrackController'

interface TrackFormProps {
    mode: 'create' | 'edit'
    initialTrack?: TrackDto
    onSuccess: () => void
}

const GENRES = [
    'hyper-pop', 'rock', 'pop', 'hip-hop', 'jazz',
    'classical', 'drill', 'phonk', 'trap', 'edm',
    'techno', 'ambient', 'lo-fi', 'synthwave'
]

const KEYS = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']

export const TrackForm: FC<TrackFormProps> = ({ mode, initialTrack, onSuccess }) => {
    const [name, setName] = useState('')
    const [genre, setGenre] = useState<GenreType | ''>('')
    const [bpm, setBpm] = useState<number | ''>('')
    const [key, setKey] = useState('')
    const [price, setPrice] = useState<number | ''>('')
    const [mp3File, setMp3File] = useState<File | null>(null)
    const [wavFile, setWavFile] = useState<File | null>(null)
    const [zipFile, setZipFile] = useState<File | null>(null)

    useEffect(() => {
        if (mode === 'edit' && initialTrack) {
            setName(initialTrack.name)
            setGenre(initialTrack.genreType)
            setBpm(initialTrack.bpm)
            setKey(initialTrack.key)
            setPrice(initialTrack.price)
        }
    }, [mode, initialTrack])

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        if (!genre || !bpm || !key) return alert('Please fill in all fields')

        const dto: TrackRequestDto = {
            name,
            genreType: genre as GenreType,
            bpm,
            key,
            price: typeof price === 'number' ? price : 0,
            nonExclusiveFile: mp3File!,
            premiumFile: wavFile!,
            exclusiveFile: zipFile!
        }

        try {
            if (mode === 'create') {
                await TrackController.createTrack(dto)
            } else if (mode === 'edit' && initialTrack) {
                await TrackController.updateTrack(initialTrack.id, dto)
            }
            onSuccess()
        } catch (err: any) {
            alert('Error: ' + err.message)
        }
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <div>
                <label className="block text-sm font-medium text-white mb-1">Name</label>
                <input
                    type="text"
                    value={name}
                    onChange={e => setName(e.target.value)}
                    className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Genre</label>
                    <select
                        value={genre}
                        onChange={e => setGenre(e.target.value as GenreType)}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    >
                        <option value="">Select genre…</option>
                        {GENRES.map(g => (
                            <option key={g}>{g}</option>
                        ))}
                    </select>
                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Key</label>
                    <select
                        value={key}
                        onChange={e => setKey(e.target.value)}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    >
                        <option value="">Select key…</option>
                        {KEYS.map(k => (
                            <option key={k}>{k}</option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">BPM</label>
                    <input
                        type="number"
                        value={bpm}
                        onChange={e => setBpm(e.target.valueAsNumber || '')}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Price</label>
                    <input
                        type="number"
                        value={price}
                        onChange={e => setPrice(e.target.valueAsNumber || '')}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    />
                </div>
            </div>

            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Non-exclusive (MP3)</label>
                    <input
                        type="file"
                        accept=".mp3"
                        onChange={e => setMp3File(e.target.files?.[0] || null)}
                        className="w-full p-2 rounded bg-gray-700 text-white file:bg-blue-600 file:text-white file:border-none file:px-3 file:py-1"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Premium (WAV)</label>
                    <input
                        type="file"
                        accept=".wav"
                        onChange={e => setWavFile(e.target.files?.[0] || null)}
                        className="w-full p-2 rounded bg-gray-700 text-white file:bg-blue-600 file:text-white file:border-none file:px-3 file:py-1"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Exclusive (ZIP)</label>
                    <input
                        type="file"
                        accept=".zip"
                        onChange={e => setZipFile(e.target.files?.[0] || null)}
                        className="w-full p-2 rounded bg-gray-700 text-white file:bg-blue-600 file:text-white file:border-none file:px-3 file:py-1"
                    />
                </div>
            </div>

            <div className="text-right pt-4">
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 hover:bg-blue-500 text-white font-medium rounded"
                >
                    {mode === 'edit' ? 'Update' : 'Upload'}
                </button>
            </div>
        </form>
    )}
