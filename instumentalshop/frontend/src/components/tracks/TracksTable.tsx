import { FC } from 'react';
import { TrackRow } from './TrackRow';
import {TrackDto} from "../../dto/TrackDto.ts";
import Pagination from "../Pagination.tsx";

interface TracksTableProps {
    tracks: TrackDto[];
    role: string;
    page: number;
    totalPages: number;
    onPlay: (id: string) => void;
    onBuy: (id: string) => void;
    onRemove: (id: string) => void;
    onToggleLike: (id: string) => void;
    onPageChange: (newPage: number) => void;
}

export const TracksTable: FC<TracksTableProps> = ({
                                                      tracks,
                                                      role,
                                                      page,
                                                      totalPages,
                                                      onPlay,
                                                      onBuy,
                                                      onRemove,
                                                      onToggleLike,
                                                      onPageChange,
                                                  }) => (
    <div className="flex-1 overflow-auto">
        <table className="min-w-full table-auto border-separate border-spacing-2">
            <thead>
            <tr className="text-xs text-gray-400 uppercase tracking-wider border-b border-gray-700">
                <th className="px-6 py-2" />
                <th className="px-6 py-2" />
                <th className="px-6 py-2 text-left">NAZEV</th>
                <th className="px-6 py-2 text-center">HODNOCENÍ</th>
                <th className="px-6 py-2 text-center">ŽÁNR</th>
                <th className="px-6 py-2 text-center">DÉLKA</th>
                <th className="px-6 py-2 text-center">TÓNINA</th>
                <th className="px-6 py-2 text-center">BPM</th>
                <th className="px-6 py-2 text-right" />
            </tr>
            </thead>
            <tbody className="space-y-2">
            {tracks.map(t => (
                <TrackRow
                    key={t.id}
                    track={t}
                    role={role}
                    onPlay={onPlay}
                    onBuy={onBuy}
                    onRemove={onRemove}
                    onToggleLike={onToggleLike}
                />
            ))}

             <Pagination
               page={page}
               totalPages={totalPages}
               onPageChange={onPageChange}
            />
            </tbody>
        </table>
    </div>
);
