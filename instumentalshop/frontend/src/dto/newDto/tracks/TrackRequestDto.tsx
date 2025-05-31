import {GenreType} from "../enums/GenreType.ts";
import {ProducerShareRequestDto} from "../../ProducerShareRequestDto.tsx";

export interface TrackRequestDto {
    name: string
    genreType: GenreType
    bpm: number
    key:String,
    priceNonExclusive: number
    pricePremium?: number
    priceExclusive?: number
    mainProducerPercentage?: number
    producerShares?: ProducerShareRequestDto[]
    nonExclusiveFile: File
    premiumFile?: File
    exclusiveFile?: File
}