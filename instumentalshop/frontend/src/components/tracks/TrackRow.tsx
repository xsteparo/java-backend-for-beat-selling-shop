import { FC } from 'react';
import HeartIcon from './icons/HeartIcon';
import BagIcon from './icons/BagIcon';
import {TrackDto} from "../../dto/TrackDto.ts";

interface TrackRowProps {
    track: TrackDto;
    role: string;
    onPlay: (id: string) => void;
    onBuy: (id: string) => void;
    onRemove: (id: string) => void;
    onToggleLike: (id: string) => void;
}

export const TrackRow: FC<TrackRowProps> = ({
                                                track: t,
                                                role,
                                                onPlay,
                                                onBuy,
                                                onRemove,
                                                onToggleLike,
                                            }) => (
    <tr className="bg-gray-800 rounded-xl">
        <td className="px-6 py-4">
            <button onClick={() => onPlay(String(t.id))} className="text-teal-400 hover:text-teal-300">
                ▶
            </button>
        </td>
        <td className="px-6 py-4">
            <img src="/images/note-icon.svg" alt="" className="w-8 h-8" />
        </td>
        <td className="px-6 py-4 align-top">
            <div className="text-white font-semibold">{t.name}</div>
            <div className="text-gray-400 text-sm">{t.producerUsername}</div>
        </td>
        <td className="px-6 py-4 text-center text-gray-300">{t.rating}</td>
        <td className="px-6 py-4 text-center text-gray-300">{t.genreType}</td>
        <td className="px-6 py-4 text-center text-gray-300">{t.length}</td>
        <td className="px-6 py-4 text-center text-gray-300">{t.key}</td>
        <td className="px-6 py-4 text-center text-gray-300">{t.bpm}</td>
        <td className="px-6 py-4 text-right space-x-3">
            <button onClick={() => onToggleLike(String(t.id))}>
                <HeartIcon filled={t.liked} className="w-5 h-5" />
            </button>
            {role === 'admin' ? (
                <>
                    <a
                        href={`/api/v1/tracks/${String(t.id)}/download`}
                        className="text-blue-500 hover:text-blue-400"
                    >
                        <BagIcon />
                    </a>
                    <button onClick={() => onRemove(String(t.id))} className="text-red-500 hover:text-red-400">
                        ✕
                    </button>
                </>
            ) : t.purchased ? (
                <a
                    href={`/api/v1/tracks/${t.id}/download`}
                    className="text-blue-500 hover:text-blue-400"
                >
                    <BagIcon />
                </a>
            ) : (
                <button onClick={() => onBuy(String(t.id))} className="text-blue-500 hover:text-blue-400">
                    <BagIcon />
                </button>
            )}
            <audio id={`audio-${String(t.id)}`} src={t.urlNonExclusive} preload="none" className="hidden" />
        </td>
    </tr>
);
