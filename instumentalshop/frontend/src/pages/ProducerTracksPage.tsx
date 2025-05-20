import { useEffect, useState } from "react";
import { TrackController } from "../controller/newControllers/TrackController";
import { TrackDto } from "../dto/newDto/tracks/TrackDto";
import { TrackEditModal } from "../components/TrackEditModal";

export const ProducerTracksPage = () => {
    const [tracks, setTracks] = useState<TrackDto[]>([]);
    const [editingTrack, setEditingTrack] = useState<TrackDto | null>(null);

    const loadTracks = async () => {
        const list = await TrackController.getMyTracks();
        setTracks(list);
    };

    useEffect(() => {
        loadTracks();
    }, []);

    return (
        <div className="max-w-6xl mx-auto px-6 py-8 text-white relative">
            <h1 className="text-3xl font-bold mb-6">My Tracks</h1>

            <table className="w-full text-sm mb-6">
                <thead>
                <tr className="text-left text-gray-400 border-b border-gray-700">
                    <th>Name</th>
                    <th>Genre</th>
                    <th>BPM</th>
                    <th>Key</th>
                    <th>Price</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {tracks.map(track => (
                    <tr key={track.id} className="border-b border-gray-700 hover:bg-gray-800">
                        <td className="py-2">{track.name}</td>
                        <td>{track.genreType}</td>
                        <td>{track.bpm}</td>
                        <td>{track.key}</td>
                        <td>${track.price}</td>
                        <td>
                            <button
                                onClick={() => setEditingTrack(track)}
                                className="text-blue-500 hover:underline"
                            >
                                Edit
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {editingTrack && (
                <TrackEditModal
                    track={editingTrack}
                    onClose={() => setEditingTrack(null)}
                    onSuccess={loadTracks}
                />
            )}
        </div>
    );
};
