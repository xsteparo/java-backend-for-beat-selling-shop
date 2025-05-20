// src/components/LicenseModal.tsx
import { FC } from 'react';
import { TrackDto } from '../dto/newDto/tracks/TrackDto';
import {LicenseType} from "../dto/CartItem.tsx";

interface Option {
    type: LicenseType;
    title: string;
    price: number;
    features: Record<string, boolean | number>;
}

const OPTIONS: Option[] = [
    {
        type: 'nonexclusive',
        title: 'Neexkluzivní',
        price: 25,
        features: { MP3: true, WAV: false, 'Oddělené stopy': false, 'Vysílací práva': false, 'Audio streamy': 75000 },
    },
    {
        type: 'premium',
        title: 'Prémiové',
        price: 50,
        features: { MP3: true, WAV: true, 'Oddělené stopy': false, 'Vysílací práva': false, 'Audio streamy': 150000 },
    },
    {
        type: 'exclusive',
        title: 'Exkluzivní',
        price: 100,
        features: { MP3: true, WAV: true, 'Oddělené stopy': true, 'Vysílací práva': true, 'Audio streamy': Infinity },
    },
];

interface LicenseModalProps {
    track: TrackDto;
    onClose: () => void;
    onChoose: (track: TrackDto, license: LicenseType, price: number) => void;
}

export const LicenseModal: FC<LicenseModalProps> = ({ track, onClose, onChoose }) => (
    <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50">
        <div className="bg-gray-800 rounded-xl p-6 w-[90%] max-w-4xl relative">
            <button onClick={onClose} className="absolute top-4 right-4 text-gray-400 hover:text-white">
                ✕
            </button>
            <h2 className="text-xl text-white mb-6">Vyberte licenci pro&nbsp;«{track.name}»</h2>
            <div className="flex flex-col md:flex-row gap-4">
                {OPTIONS.map(o => (
                    <div key={o.type} className="flex-1 bg-gray-700 rounded-lg p-4 flex flex-col" data-testid={`license-option-${o.type}`} >
                        <h3 className="text-lg text-white mb-1">{o.title}</h3>
                        <div className="text-gray-300 mb-4">${o.price}</div>
                        <ul className="flex-1 space-y-1 text-gray-300 text-sm mb-4">
                            {Object.entries(o.features).map(([feat, ok]) => (
                                <li key={feat} className="flex justify-between">
                                    <span>{feat}</span>
                                    <span>
                    {ok === true && <span className="text-green-400">✔</span>}
                                        {ok === false && <span className="text-red-500">✕</span>}
                                        {typeof ok === 'number' && ok}
                                        {ok === Infinity && '∞'}
                  </span>
                                </li>
                            ))}
                        </ul>
                        <button
                            onClick={() => onChoose(track, o.type, o.price)}
                            className="mt-auto bg-blue-600 text-white py-2 rounded hover:bg-blue-500"
                            data-testid="add-to-cart"
                        >
                            Do košíku
                        </button>
                    </div>
                ))}
            </div>
        </div>
    </div>
);
