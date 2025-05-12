import { FC } from 'react';

import {TrackDto} from "../../dto/TrackDto.ts";
import HeartIcon from '../icons/HeartIcon.tsx';
import BagIcon from "../icons/BagIcon.tsx";

interface TrackRowProps {
    track: TrackDto;
    role: string;
    liked: boolean;
    onPlay: (id: string) => void;
    onBuy: (id: string) => void;
    onRemove: (id: string) => void;
    onToggleLike: (id: string) => void;
}

export const TrackRow: FC<TrackRowProps> = ({
                                                track,
                                                role,
                                                liked,
                                                onPlay,
                                                onBuy,
                                                onRemove,
                                                onToggleLike,
                                            }) => (
    <div className="grid grid-cols-[auto_auto_1fr_repeat(5,auto)_auto] gap-x-4 items-center bg-gray-800 rounded-xl px-6 py-4">
        <button onClick={() => onPlay(String(track.id))} className="p-1 text-teal-400 hover:text-teal-300">▶</button>
        <img src="/images/note-icon.svg" alt="" className="w-8 h-8" />
        <div className="flex flex-col self-start">
            <span className="text-white font-semibold">{track.name}</span>
            <span className="text-gray-400 text-sm">{track.producerUsername}</span>
        </div>
        <div className="text-center text-gray-300">{track.rating}</div>
        <div className="text-center text-gray-300">{track.genreType}</div>
        <div className="text-center text-gray-300">{track.length}</div>
        <div className="text-center text-gray-300">{track.key}</div>
        <div className="text-center text-gray-300">{track.bpm}</div>
        <div className="flex items-center justify-end space-x-3">
            <button onClick={() => onToggleLike(String(track.id))}>
                <HeartIcon filled={liked} className="w-5 h-5 text-red-500" />
            </button>
            {role === 'admin' ? (
                <>
                    <a href={`/api/v1/tracks/${String(track.id)}/download`} className="p-1 text-blue-500 hover:text-blue-400">
                        <BagIcon className="w-5 h-5" />
                    </a>
                    <button onClick={() => onRemove(String(track.id))} className="text-red-500 hover:text-red-400">✕</button>
                </>
            ) : track.purchased ? (
                        <a href={`/api/v1/tracks/${String(track.id)}/download`} className="p-1 text-blue-500 hover:text-blue-400">
                    <BagIcon className="w-5 h-5" />
                </a>
            ) : (
                <button onClick={() => onBuy(String(track.id))} className="p-1 text-blue-500 hover:text-blue-400">
                    <BagIcon className="w-5 h-5" />
                </button>
            )}
        </div>
    </div>
);