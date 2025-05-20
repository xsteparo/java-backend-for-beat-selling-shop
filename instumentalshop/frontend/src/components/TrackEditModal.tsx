import { FC } from "react";
import { TrackDto } from "../dto/newDto/tracks/TrackDto";
import { TrackForm } from "./TrackForm";

interface Props {
    track: TrackDto;
    onClose: () => void;
    onSuccess: () => void;
}

export const TrackEditModal: FC<Props> = ({ track, onClose, onSuccess }) => {
    return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
            <div className="bg-gray-800 rounded-lg p-6 max-w-3xl w-full relative shadow-xl">
                <button
                    onClick={onClose}
                    className="absolute top-2 right-3 text-gray-400 hover:text-white text-xl"
                >
                    ✕
                </button>

                <h2 className="text-2xl font-bold mb-4">Edit Track: {track.name}</h2>

                <TrackForm
                    mode="edit"
                    initialTrack={track}
                    onSuccess={() => {
                        onSuccess();
                        onClose();
                    }}
                />
            </div>
        </div>
    );
};
