import { FC, useState } from 'react'

interface FiltersProps {
    onChange: (filters: {
        genre: string
        tempoRange: string
        key: string
        sort: string
    }) => void
}

export const Filters: FC<FiltersProps> = ({ onChange }) => {
    const [open, setOpen] = useState({
        genre: false,
        tempoRange:   false,
        key:   false,
        sort:  false,
    })

    const options = {
        genre: ['hyperpop','rock','pop','hiphop','jazz','classical','drill','phonk','trap','edm','techno','ambient','lofi','synthwave'],
        tempoRange:   ['60-90','90-110','110-130','130-150','150-999'],
        key:   ['C','C#','D','D#','E','F','F#','G','G#','A','A#','B'],
        sort: [
            { value: '-rating',    label: 'Hodnocení: sestupně' },
            { value: 'rating',     label: 'Hodnocení: vzestupně' },
            { value: '-createdAt', label: 'Nejnovější nahoře' },
            { value: 'createdAt',  label: 'Nejstarší nahoře' },
            { value: '-plays',     label: 'Přehrání: sestupně' },
            { value: 'plays',      label: 'Přehrání: vzestupně' },
        ],
    }

    const [filters, setFilters] = useState({
        genre: '',
        tempoRange:   '',
        key:   '',
        sort:  '',
    })

    const toggle = (k: keyof typeof open) =>
        setOpen(o => ({ ...o, [k]: !o[k] }))

    const pick = (k: keyof typeof filters, v: string) => {
        const next = { ...filters, [k]: v }
        setFilters(next)
        onChange(next)
    }

    const resetAll = () => {
        const empty = { genre: '', tempoRange: '', key: '', sort: '' }
        setFilters(empty)
        onChange(empty)
    }

    return (
        <aside className="w-60 bg-gray-800 p-4 rounded-lg space-y-4">
            <div className="flex items-center justify-between">
                <h2 className="text-white text-xl">Filters</h2>
                <button
                    onClick={resetAll}
                    className="text-sm text-gray-400 hover:text-white"
                >
                    Reset
                </button>
            </div>

            {(['genre','tempoRange','key','sort'] as const).map(k => (
                <div key={k}>
                    <button
                        onClick={() => toggle(k)}
                        className="w-full text-left text-white py-2 flex justify-between items-center hover:bg-gray-700 rounded"
                    >
                        {k.charAt(0).toUpperCase() + k.slice(1)}
                        <span className={`transform transition-transform ${open[k] ? 'rotate-90' : ''}`}>
              ▸
            </span>
                    </button>
                    <ul
                        className={`${open[k] ? 'block' : 'hidden'} mt-1 max-h-40 overflow-auto`}
                    >
                        {k === 'sort'
                            ? options.sort.map(o => (
                                <li
                                    key={o.value}
                                    onClick={() => pick(k, o.value)}
                                    className={`px-2 py-1 cursor-pointer rounded ${
                                        filters[k] === o.value
                                            ? 'bg-gray-700/50 text-white'
                                            : 'text-gray-300 hover:bg-gray-700'
                                    }`}
                                >
                                    {o.label}
                                </li>
                            ))
                            : options[k].map((val: string) => (
                                <li
                                    key={val}
                                    onClick={() => pick(k, val)}
                                    className={`px-2 py-1 cursor-pointer rounded ${
                                        filters[k] === val
                                            ? 'bg-gray-700/50 text-white'
                                            : 'text-gray-300 hover:bg-gray-700'
                                    }`}
                                >
                                    {k === 'tempoRange' ? `${val} BPM` : val}
                                </li>
                            ))}
                    </ul>
                </div>
            ))}
        </aside>
    )
}