import {ProducerShareRequestDto} from "./ProducerShareRequestDto.tsx";
import {GenreType} from "./GenreType.ts";

export interface TrackRequestDto {
    name: string
    genreType: GenreType
    bpm: number
    key:String,
    price: number;
    mainProducerPercentage?: number
    producerShares?: ProducerShareRequestDto[]
    nonExclusiveFile: File
    premiumFile?: File
    exclusiveFile?: File
}