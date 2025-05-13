import { FC } from 'react';
import { TrackRow } from './TrackRow';
import {TrackDto} from "../../dto/TrackDto.ts";


interface TracksTableProps {
    tracks: TrackDto[];
    role: string;
    likedSet: Set<string>;
    currentTrackId: string | null;
    onPlay: (id: string) => void;
    onBuy: (id: string) => void;
    onRemove: (id: string) => void;
    onToggleLike: (id: string) => void;
}

export const TracksTable: FC<TracksTableProps> = ({
                                                      tracks,
                                                      role,
                                                      likedSet,
                                                      currentTrackId,
                                                      onPlay,
                                                      onBuy,
                                                      onRemove,
                                                      onToggleLike,
                                                  }) => (
    <div className="w-full flex flex-col space-y-2">
        {/* Заголовок */}
        <div className="flex w-full items-center px-6 py-2 space-x-4 text-xs text-gray-400 uppercase">
            <div className="flex-none w-10" />
            <div className="flex-none w-8" />
            <div className="flex-grow min-w-0 ">NAZEV</div>
            <div className="flex-none w-16 text-center">HODNOCENÍ</div>
            <div className="flex-none w-16 text-center">ŽÁNR</div>
            <div className="flex-none w-16 text-center">DÉLKA</div>
            <div className="flex-none w-16 text-center">TÓNINA</div>
            <div className="flex-none w-16 text-center">BPM</div>
            <div className="flex-none w-24" />
        </div>

        {tracks.map(track => (
            <TrackRow
                key={track.id}
                track={track}
                role={role}
                liked={likedSet.has(String(track.id))}
                currentTrackId={currentTrackId}
                onPlay={onPlay}
                onBuy={onBuy}
                onRemove={onRemove}
                onToggleLike={onToggleLike}
            />
        ))}
    </div>
);