// src/components/LicenseModal.tsx
import { FC } from 'react';
import { TrackDto } from '../dto/newDto/tracks/TrackDto';
import {LicenseType} from "../dto/CartItem.tsx";
import {LicenceTemplateDto, LicenceType} from "../dto/newDto/licence/LicenceTemplateDto.tsx";

const FEATURES_MAP: Record<LicenceType, Record<string, boolean | number>> = {
    NON_EXCLUSIVE: {
        MP3: true,
        WAV: false,
        'Oddělené stopy': false,
        'Vysílací práva': false,
        'Audio streamy': 75000,
    },
    PREMIUM: {
        MP3: true,
        WAV: true,
        'Oddělené stopy': false,
        'Vysílací práva': false,
        'Audio streamy': 150000,
    },
    EXCLUSIVE: {
        MP3: true,
        WAV: true,
        'Oddělené stopy': true,
        'Vysílací práva': true,
        'Audio streamy': Infinity,
    },
};

/**
 * Человекочитаемые заголовки для каждой лицензии.
 * Ключи должны точно повторять значения LicenceType
 * (т. е. 'NON_EXCLUSIVE', 'PREMIUM', 'EXCLUSIVE').
 */
const TITLES_MAP: Record<LicenceType, string> = {
    NON_EXCLUSIVE: 'Neexkluzivní',
    PREMIUM: 'Prémiové',
    EXCLUSIVE: 'Exkluzivní',
};

interface LicenseModalProps {
    track: TrackDto;
    onClose: () => void;
    onChoose: (track: TrackDto, licenceType: LicenceType, price: number) => void;
}

export const LicenseModal: FC<LicenseModalProps> = ({ track, onClose, onChoose }) => {
    // Если вдруг по какой-то причине у трека пустой массив licenceTemplates, не рендерим
    if (!track.licenceTemplates || track.licenceTemplates.length === 0) {
        return null;
    }

    return (
        <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50">
            <div className="bg-gray-800 rounded-xl p-6 w-[90%] max-w-4xl relative">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-400 hover:text-white"
                    aria-label="Close modal"
                >
                    ✕
                </button>
                <h2 className="text-xl text-white mb-6">
                    Vyberte licenci pro&nbsp;«{track.name}»
                </h2>

                <div className="flex flex-col md:flex-row gap-4">
                    {track.licenceTemplates.map((lt: LicenceTemplateDto) => {
                        const type = lt.licenceType;
                        const price = lt.price; // Преобразованное BigDecimal → number

                        // Из словаря берём фичи, которые нужно отобразить
                        const features = FEATURES_MAP[type];
                        // Из TITLES_MAP получаем заголовок карточки
                        const title = TITLES_MAP[type];

                        return (
                            <div
                                key={lt.id}
                                className="flex-1 bg-gray-700 rounded-lg p-4 flex flex-col"
                                data-testid={`license-option-${type.toLowerCase()}`}
                            >
                                <h3 className="text-lg text-white mb-1">{title}</h3>
                                <div className="flex items-center text-gray-300 mb-4">
                                    {/* Можно вставить иконку валюты, например, */}
                                    <span className="text-2xl">${price.toFixed(2)}</span>
                                </div>

                                <ul className="flex-1 space-y-1 text-gray-300 text-sm mb-4">
                                    {Object.entries(features).map(([featName, featVal]) => (
                                        <li key={featName} className="flex justify-between">
                                            <span>{featName}</span>
                                            <span>
                        {featVal === true && (
                            <span className="text-green-400">✔</span>
                        )}
                                                {featVal === false && (
                                                    <span className="text-red-500">✕</span>
                                                )}
                                                {typeof featVal === 'number' &&
                                                    featVal !== Infinity &&
                                                    featVal}
                                                {featVal === Infinity && '∞'}
                      </span>
                                        </li>
                                    ))}
                                </ul>

                                <button
                                    onClick={() => onChoose(track, type, price)}
                                    className="mt-auto bg-blue-600 text-white py-2 rounded hover:bg-blue-500"
                                    data-testid="add-to-cart"
                                >
                                    Do košíku
                                </button>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
};