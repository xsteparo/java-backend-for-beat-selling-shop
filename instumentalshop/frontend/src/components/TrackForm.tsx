import { FC, FormEvent, useState, useEffect } from 'react'
import { TrackRequestDto } from '../dto/TrackRequestDto'
import { GenreType } from '../dto/newDto/enums/GenreType'
import { TrackDto } from '../dto/newDto/tracks/TrackDto'
import { TrackController } from '../controller/newControllers/TrackController'
interface TrackFormProps {
    mode: 'create' | 'edit';
    initialTrack?: TrackDto;
    onSuccess: () => void;
}

const GENRES: GenreType[] = [
    'hyperpop',
    'rock',
    'pop',
    'hiphop',
    'jazz',
    'classical',
    'drill',
    'phonk',
    'trap',
    'edm',
    'techno',
    'ambient',
    'lofi',
    'synthwave',
];

const KEYS = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B'] as const;

export const TrackForm: FC<TrackFormProps> = ({ mode, initialTrack, onSuccess }) => {
    // Основные поля
    const [name, setName] = useState('');
    const [genre, setGenre] = useState<GenreType | ''>('');
    const [bpm, setBpm] = useState<number | ''>('');
    const [key, setKey] = useState<string>('');

    // Три отдельных цены
    const [priceNonExclusive, setPriceNonExclusive] = useState<number | ''>('');
    const [pricePremium, setPricePremium] = useState<number | ''>('');
    const [priceExclusive, setPriceExclusive] = useState<number | ''>('');

    // Файлы (только если пользователь выбрал — иначе в режиме edit не отправляем)
    const [mp3File, setMp3File] = useState<File | null>(null);
    const [wavFile, setWavFile] = useState<File | null>(null);
    const [zipFile, setZipFile] = useState<File | null>(null);

    // При переходе в режим "edit" заполняем стейт значениями initialTrack
    useEffect(() => {
        if (mode === 'edit' && initialTrack) {
            setName(initialTrack.name);
            const normalized = initialTrack.genreType
                    .toLowerCase()       // "hip_hop" или "hyper_pop"
                    .replace(/_/g, '')   // "hiphop" или "hyperpop"
                // Если ещё есть дефисы, можно добавить .replace(/-/g, '')
            ;

            setGenre(normalized as GenreType);
            setBpm(initialTrack.bpm);
            setKey(initialTrack.key || '');

            // Ищем шаблоны лицензий в initialTrack.licenceTemplates
            const nonEx = initialTrack.licenceTemplates.find(t => t.licenceType === 'NON_EXCLUSIVE');
            setPriceNonExclusive(nonEx ? nonEx.price : '');

            const prem = initialTrack.licenceTemplates.find(t => t.licenceType === 'PREMIUM');
            setPricePremium(prem ? prem.price : '');

            const excl = initialTrack.licenceTemplates.find(t => t.licenceType === 'EXCLUSIVE');
            setPriceExclusive(excl ? excl.price : '');
        }
    }, [mode, initialTrack]);

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        // Простая валидация
        if (!name.trim()) {
            return alert('Please enter a track name.');
        }
        if (!genre) {
            return alert('Please select a genre.');
        }
        if (!bpm || bpm < 20 || bpm > 300) {
            return alert('Please enter BPM between 20 and 300.');
        }
        if (!key) {
            return alert('Please select a key.');
        }
        if (priceNonExclusive === '' || priceNonExclusive < 0) {
            return alert('Please enter a valid Non-Exclusive price.');
        }
        // Если пользователь выбрал WAV, то должна быть цена Premium
        if (wavFile && (pricePremium === '' || pricePremium < 0)) {
            return alert('Please enter a valid Premium price or uncheck the WAV file.');
        }
        // Аналогично для ZIP
        if (zipFile && (priceExclusive === '' || priceExclusive < 0)) {
            return alert('Please enter a valid Exclusive price or uncheck the ZIP file.');
        }

        // Собираем DTO
        const dto: TrackRequestDto = {
            name,
            genreType: genre as GenreType,
            bpm,
            key,
            // Используем только числа, иначе 0
            priceNonExclusive: typeof priceNonExclusive === 'number' ? priceNonExclusive : 0,
            pricePremium: typeof pricePremium === 'number' ? pricePremium : undefined,
            priceExclusive: typeof priceExclusive === 'number' ? priceExclusive : undefined,
            nonExclusiveFile: mp3File!,
            // premiumFile и exclusiveFile кладём только если не null
            premiumFile: wavFile ?? undefined,
            exclusiveFile: zipFile ?? undefined,
        };

        try {
            if (mode === 'create') {
                await TrackController.createTrack(dto);
            } else if (mode === 'edit' && initialTrack) {
                const updateDto: Partial<TrackRequestDto> & { nonExclusiveFile?: File; premiumFile?: File; exclusiveFile?: File } = {
                    name,
                    genreType: genre as GenreType,
                    bpm,
                    key,
                    priceNonExclusive: typeof priceNonExclusive === 'number' ? priceNonExclusive : 0,
                    pricePremium: typeof pricePremium === 'number' ? pricePremium : undefined,
                    priceExclusive: typeof priceExclusive === 'number' ? priceExclusive : undefined,
                };
                if (mp3File) updateDto.nonExclusiveFile = mp3File;
                if (wavFile) updateDto.premiumFile = wavFile;
                if (zipFile) updateDto.exclusiveFile = zipFile;

                await TrackController.updateTrack(initialTrack.id, updateDto as TrackRequestDto);
            }
            onSuccess();
        } catch (err: any) {
            alert('Error: ' + err.message);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            {/* Name */}
            <div>
                <label className="block text-sm font-medium text-white mb-1">Name</label>
                <input
                    type="text"
                    value={name}
                    onChange={e => setName(e.target.value)}
                    className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                    required
                />
            </div>

            {/* Genre + Key */}
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Genre</label>
                    <select
                        value={genre}
                        onChange={e => setGenre(e.target.value as GenreType)}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                        required
                    >
                        <option value="">Select genre…</option>
                        {GENRES.map(g => (
                            <option key={g} value={g}>
                                {g === 'hiphop'
                                    ? 'Hip-Hop'
                                    : g === 'lofi'
                                        ? 'Lo-Fi'
                                        : g.charAt(0).toUpperCase() + g.slice(1)}
                            </option>
                        ))}
                    </select>
                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Key</label>
                    <select
                        value={key}
                        onChange={e => setKey(e.target.value)}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                        required
                    >
                        <option value="">Select key…</option>
                        {KEYS.map(k => (
                            <option key={k} value={k}>
                                {k}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            {/* BPM + Price Non-Exclusive */}
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">BPM</label>
                    <input
                        type="number"
                        value={bpm}
                        onChange={e => setBpm(e.target.valueAsNumber || '')}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                        min={20}
                        max={300}
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">
                        Price Non-Exclusive (USD)
                    </label>
                    <input
                        type="number"
                        value={priceNonExclusive}
                        onChange={e => setPriceNonExclusive(e.target.valueAsNumber || '')}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                        min={0}
                        required
                    />
                </div>
            </div>

            {/* Price Premium + Price Exclusive */}
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">
                        Price Premium (USD) – WAV
                    </label>
                    <input
                        type="number"
                        value={pricePremium}
                        onChange={e => setPricePremium(e.target.valueAsNumber || '')}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                        min={0}
                    />

                </div>
                <div>
                    <label className="block text-sm font-medium text-white mb-1">
                        Price Exclusive (USD) – ZIP
                    </label>
                    <input
                        type="number"
                        value={priceExclusive}
                        onChange={e => setPriceExclusive(e.target.valueAsNumber || '')}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 text-white"
                        min={0}
                    />

                </div>
            </div>

            {/* Файлы */}
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-white mb-1">Non-Exclusive (MP3)</label>
                    <input
                        type="file"
                        accept=".mp3"
                        onChange={e => setMp3File(e.target.files?.[0] || null)}
                        className="w-full p-2 rounded bg-gray-700 text-white file:bg-blue-600 file:text-white file:border-none file:px-3 file:py-1"
                        // В режиме edit не обязателен, т.к. можем оставить старый
                        required={mode === 'create'}
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

            {/* Кнопка сабмита */}
            <div className="text-right pt-4">
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 hover:bg-blue-500 text-white font-medium rounded"
                >
                    {mode === 'edit' ? 'Update' : 'Upload'}
                </button>
            </div>
        </form>
    );
};