import { FC } from 'react';

import {TrackDto} from "../../dto/TrackDto.ts";
import HeartIcon from '../icons/HeartIcon.tsx';
import BagIcon from "../icons/BagIcon.tsx";


interface TrackRowProps {
    track: TrackDto;
    role: string;
    liked: boolean;
    currentTrackId?: string | null;
    onPlay: (id: string) => void;
    onBuy: (id: string) => void;
    onRemove: (id: string) => void;
    onToggleLike: (id: string) => void;
}

export const TrackRow: FC<TrackRowProps> = ({
                                                track,
                                                role,
                                                liked,
                                                currentTrackId,
                                                onPlay,
                                                onBuy,
                                                onRemove,
                                                onToggleLike,
                                            }) => {
    const idStr = String(track.id);
    const isPlaying = idStr === currentTrackId;

    return (
        <div className="flex w-full items-center bg-gray-800 rounded-xl px-6 py-4 space-x-4">
            <button
                onClick={() => onPlay(idStr)}
                className="flex-none w-10 h-10 flex items-center justify-center text-teal-400 hover:text-teal-300"
            >
                {isPlaying ? '❚❚' : '▶'}
            </button>

            <img
                src="/images/note-icon.svg"
                alt="icon"
                className="flex-none w-10 h-10 rounded-full object-cover"
            />

            <div className="flex-grow min-w-0  flex flex-col">
        <span className="text-white font-semibold truncate">
          {track.name}
        </span>
                <span className="text-gray-400 text-sm truncate">
          {track.producerUsername}
        </span>
            </div>

            <div className="flex-none w-16 text-center text-gray-300">
                {track.rating}
            </div>
            <div className="flex-none w-16 text-center text-gray-300">
                {track.genreType}
            </div>
            <div className="flex-none w-16 text-center text-gray-300">
                {track.length}
            </div>
            <div className="flex-none w-16 text-center text-gray-300">
                {track.key}
            </div>
            <div className="flex-none w-16 text-center text-gray-300">
                {track.bpm}
            </div>

            <div className="flex-none w-24 flex items-center justify-end space-x-3">
                <button onClick={() => onToggleLike(idStr)} className="p-1">
                    <HeartIcon filled={liked} className="w-5 h-5 text-red-500" />
                </button>

                {role === 'admin' ? (
                    <>
                        <a
                            href={`/api/v1/tracks/${track.id}/download`}
                            className="p-1 hover:text-blue-400"
                        >
                            <BagIcon className="w-5 h-5 text-blue-500" />
                        </a>
                        <button
                            onClick={() => onRemove(idStr)}
                            className="text-red-500 hover:text-red-400"
                        >
                            ✕
                        </button>
                    </>
                ) : track.purchased ? (
                    <a
                        href={`/api/v1/tracks/${track.id}/download`}
                        className="p-1 hover:text-blue-400"
                    >
                        <BagIcon className="w-5 h-5 text-blue-500" />
                    </a>
                ) : (
                    <button
                        onClick={() => onBuy(idStr)}
                        className="p-1 hover:text-blue-400"
                    >
                        <BagIcon className="w-5 h-5 text-blue-500" />
                    </button>
                )}
            </div>
        </div>
    );
};