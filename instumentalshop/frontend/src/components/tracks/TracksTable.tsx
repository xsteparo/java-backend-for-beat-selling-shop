import { FC } from 'react';
import { TrackRow } from './TrackRow';
import {TrackDto} from "../../dto/TrackDto.ts";


interface TracksTableProps {
    tracks: TrackDto[];
    role: string;
    likedSet: Set<string>;
    onPlay: (id: string) => void;
    onBuy: (id: string) => void;
    onRemove: (id: string) => void;
    onToggleLike: (id: string) => void;
}

export const TracksTable: FC<TracksTableProps> = ({
                                                      tracks,
                                                      role,
                                                      likedSet,
                                                      onPlay,
                                                      onBuy,
                                                      onRemove,
                                                      onToggleLike,
                                                  }) => (
    <div className="flex-1 flex flex-col space-y-2">
        <div className="grid grid-cols-[auto_auto_1fr_repeat(5,auto)_auto] gap-x-4 px-6 py-2 text-xs text-gray-400 uppercase">
            <div />
            <div />
            <div>NAZEV</div>
            <div className="text-center">HODNOCENÍ</div>
            <div className="text-center">ŽÁNR</div>
            <div className="text-center">DÉLKA</div>
            <div className="text-center">TÓNINA</div>
            <div className="text-center">BPM</div>
            <div />
        </div>
        {tracks.map(track => (
            <TrackRow
                key={track.id}
                track={track}
                role={role}
                liked={likedSet.has(track.id?.toString())}
                onPlay={onPlay}
                onBuy={onBuy}
                onRemove={onRemove}
                onToggleLike={onToggleLike}
            />
        ))}
    </div>
);