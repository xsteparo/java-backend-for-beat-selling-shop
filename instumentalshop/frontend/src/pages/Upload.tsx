import { FC, FormEvent, useState } from 'react'
import {TrackController} from "../controller/newControllers/TrackController.tsx";
import {TrackRequestDto} from "../dto/TrackRequestDto.ts";
import {GenreType} from "../dto/newDto/enums/GenreType.ts";

const GENRES = [
    'hyper-pop','rock','pop','hip-hop','jazz',
    'classical','drill','phonk','trap','edm',
    'techno','ambient','lo-fi','synthwave'
]

const KEYS = [
    'C','C#','D','D#','E','F','F#','G','G#','A','A#','B'
]

export const Upload: FC = () => {
    const [name, setName]                     = useState('')
    const [genre, setGenre]                   = useState<GenreType | ''>('')
    const [bpm, setBpm]                       = useState<number | ''>('')
    const [key, setKey]                       = useState('')
    const [priceNonExclusive, setPriceNonExclusive] = useState<number | ''>('')
    const [pricePremium, setPricePremium]           = useState<number | ''>('')
    const [priceExclusive, setPriceExclusive]       = useState<number | ''>('')

    const [mp3File, setMp3File] = useState<File | null>(null)
    const [wavFile, setWavFile] = useState<File | null>(null)
    const [zipFile, setZipFile] = useState<File | null>(null)

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        if (!name.trim()) {
            alert('Please enter a track name.')
            return
        }
        if (!genre) {
            alert('Please select a genre.')
            return
        }
        if (!bpm || bpm <= 0) {
            alert('Please enter a valid BPM.')
            return
        }
        if (!key) {
            alert('Please select a key.')
            return
        }
        if (!mp3File) {
            alert('Please upload an MP3 file for the non-exclusive license.')
            return
        }
        if (priceNonExclusive === '' || priceNonExclusive < 0) {
            alert('Please enter a valid price for Non-Exclusive license.')
            return
        }
        if (wavFile && (pricePremium === '' || pricePremium < 0)) {
            alert('Please enter a valid price for Premium license.')
            return
        }
        if (zipFile && (priceExclusive === '' || priceExclusive < 0)) {
            alert('Please enter a valid price for Exclusive license.')
            return
        }

        const dto: TrackRequestDto = {
            name,
            genreType: genre as GenreType,
            bpm,
            key,

            priceNonExclusive: typeof priceNonExclusive === 'number' ? priceNonExclusive : 0,
            pricePremium: typeof pricePremium === 'number' ? pricePremium : undefined,
            priceExclusive: typeof priceExclusive === 'number' ? priceExclusive : undefined,

            nonExclusiveFile: mp3File!,
            premiumFile: wavFile ?? undefined,
            exclusiveFile: zipFile ?? undefined,
        }

        try {
            const created = await TrackController.createTrack(dto)
            alert(`Track "${created.name}" uploaded successfully!`)
        } catch (err: any) {
            console.error('Upload error:', err)
            alert(err.message)
        }
    }

    return (
        <main className="bg-gray-900 text-white min-h-screen p-6">
            <h1 className="text-2xl font-semibold mb-6 text-center">Upload New Track</h1>
            <form
                onSubmit={handleSubmit}
                className="max-w-4xl mx-auto bg-gray-800 p-6 rounded-lg shadow-md grid grid-cols-2 gap-x-6 gap-y-8"
                encType="multipart/form-data" // обязательно для файлов
            >
                {/* Левый столбец: основные поля */}
                <div className="space-y-10 ">
                    <div>
                        <label className="block mb-2 text-sm font-medium">Name</label>
                        <input
                            type="text"
                            required
                            value={name}
                            onChange={e => setName(e.target.value)}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-medium">Genre</label>
                        <select
                            required
                            value={genre}
                            onChange={e => setGenre(e.target.value as GenreType)}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">Select genre…</option>
                            {GENRES.map(g => (
                                <option key={g} value={g}>
                                    {g}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-medium">BPM</label>
                        <input
                            type="number"
                            required
                            min={20}
                            max={300}
                            value={bpm}
                            onChange={e => setBpm(e.target.valueAsNumber || '')}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-medium">Key</label>
                        <select
                            required
                            value={key}
                            onChange={e => setKey(e.target.value)}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">Select key…</option>
                            {KEYS.map(k => (
                                <option key={k} value={k}>{k}</option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-medium">Price Non-Exclusive (USD) – MP3</label>
                        <input
                            type="number"
                            required
                            min={0}
                            step="1"
                            value={priceNonExclusive}
                            onChange={e => setPriceNonExclusive(e.target.valueAsNumber || '')}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-medium">Price Premium (USD) – WAV</label>
                        <input
                            type="number"
                            min={0}
                            step="1"
                            value={pricePremium}
                            onChange={e => setPricePremium(e.target.valueAsNumber || '')}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-medium">Price Exclusive (USD) – ZIP</label>
                        <input
                            type="number"
                            min={0}
                            step="1"
                            value={priceExclusive}
                            onChange={e => setPriceExclusive(e.target.valueAsNumber || '')}
                            className="w-full p-2 bg-gray-700 border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>

                <div className="space-y-6">
                    <div className="border border-gray-600 p-4 rounded-lg">
                        <h2 className="text-lg font-medium mb-3">Non-Exclusive (MP3)</h2>
                        <input
                            type="file"
                            accept=".mp3"
                            onChange={e => setMp3File(e.target.files?.[0] || null)}
                            className="block w-full text-gray-300 bg-gray-700 rounded cursor-pointer p-2"
                        />
                    </div>
                    <div className="border border-gray-600 p-4 rounded-lg">
                        <h2 className="text-lg font-medium mb-3">Premium (WAV)</h2>
                        <input
                            type="file"
                            accept=".wav"
                            onChange={e => setWavFile(e.target.files?.[0] || null)}
                            className="block w-full text-gray-300 bg-gray-700 rounded cursor-pointer p-2"
                        />
                    </div>
                    <div className="border border-gray-600 p-4 rounded-lg">
                        <h2 className="text-lg font-medium mb-3">Exclusive (ZIP)</h2>
                        <input
                            type="file"
                            accept=".zip"
                            onChange={e => setZipFile(e.target.files?.[0] || null)}
                            className="block w-full text-gray-300 bg-gray-700 rounded cursor-pointer p-2"
                        />
                    </div>
                </div>

                <div className="col-span-2 text-right">
                    <button
                        type="submit"
                        className="px-6 py-3 bg-blue-600 hover:bg-blue-500 rounded text-white font-medium transition"
                    >
                        Upload
                    </button>
                </div>
            </form>
        </main>
    )
}