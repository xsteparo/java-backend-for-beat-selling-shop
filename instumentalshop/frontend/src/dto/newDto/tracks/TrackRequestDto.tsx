import {GenreType} from "../enums/GenreType.ts";

export interface TrackRequestDto {
    name: string;
    genreType: GenreType;
    bpm: number;
    key?: string;
    price?: number;
    nonExclusiveFile: File;
    premiumFile?: File;
    exclusiveFile?: File;
}