import { Pause, Play, Volume2 } from "lucide-react";
import { ChangeEvent, useEffect, useState } from "react";

interface PlayerBarProps {
    audio: HTMLAudioElement | null;
    track?: { title: string; producer: string };
}

const PlayerBar: React.FC<PlayerBarProps> = ({ audio, track }) => {
    const [progress, setProgress] = useState(0);
    const [current,  setCurrent]  = useState(0);
    const [duration, setDuration] = useState(0);
    const [volume,   setVolume]   = useState(1);

    useEffect(() => {
        if (!audio) return;

        const onTime = () => {
            setDuration(audio.duration || 0);
            setCurrent(audio.currentTime);
            setProgress(audio.duration ? (audio.currentTime / audio.duration) * 100 : 0);
        };
        audio.addEventListener("timeupdate", onTime);
        audio.addEventListener("loadedmetadata", onTime);

        return () => {
            audio.removeEventListener("timeupdate", onTime);
            audio.removeEventListener("loadedmetadata", onTime);
        };
    }, [audio]);

    const toggle = () => {
        if (!audio) return;
        audio.paused ? audio.play() : audio.pause();
    };

    const seek = (e: ChangeEvent<HTMLInputElement>) => {
        if (!audio) return;
        const pct = Number(e.target.value);
        audio.currentTime = (pct / 100) * duration;
    };

    const changeVol = (e: ChangeEvent<HTMLInputElement>) => {
        if (!audio) return;
        const v = Number(e.target.value);
        audio.volume = v;
        setVolume(v);
    };

    const fmt = (s: number) =>
        isNaN(s) ? "--:--" : new Date(s * 1000).toISOString().substring(14, 19);

    /* ─────── UI ─────── */
    return (
        <div
            className="fixed bottom-0 left-0 right-0 z-50
                 bg-gradient-to-r from-[#0F172A] to-[#0B1120]/90
                 backdrop-blur-md border-t border-slate-700/50"
        >
            <div className="mx-auto max-w-6xl px-4 sm:px-6 flex items-center gap-6 h-16">
                <button
                    onClick={toggle}
                    className="p-2 rounded-full bg-[#00e5ff]/10 hover:bg-[#00e5ff]/20
                     text-[#00e5ff] transition"
                >
                    {audio?.paused ? <Play size={20} /> : <Pause size={20} />}
                </button>

                <div className="flex flex-col max-w-[320px] truncate">
          <span className="text-slate-50 font-semibold truncate">
            {track?.title || "—"}
          </span>
                    <span className="text-xs text-slate-400 truncate">
            {track?.producer || ""}
          </span>
                </div>

                <span className="text-xs tabular-nums text-slate-400 w-12 text-right">
          {fmt(current)}
        </span>

                <input
                    type="range"
                    min={0}
                    max={100}
                    value={progress}
                    onChange={seek}
                    className="flex-grow h-1 accent-[#00c853] cursor-pointer
                     [&::-webkit-slider-runnable-track]:rounded-full
                     [&::-webkit-slider-thumb]:h-3 [&::-webkit-slider-thumb]:w-3
                     [&::-webkit-slider-thumb]:rounded-full
                     [&::-webkit-slider-thumb]:bg-[#00c853]"
                />

                <span className="text-xs tabular-nums text-slate-400 w-12">
          {fmt(duration)}
        </span>

                <Volume2 size={18} className="text-slate-400" />
                <input
                    type="range"
                    min={0}
                    max={1}
                    step={0.01}
                    value={volume}
                    onChange={changeVol}
                    className="w-24 h-1 accent-[#00e5ff] cursor-pointer
                     [&::-webkit-slider-runnable-track]:rounded-full
                     [&::-webkit-slider-thumb]:h-3 [&::-webkit-slider-thumb]:w-3
                     [&::-webkit-slider-thumb]:rounded-full
                     [&::-webkit-slider-thumb]:bg-[#00e5ff]"
                />
            </div>
        </div>
    );
};

export default PlayerBar;
