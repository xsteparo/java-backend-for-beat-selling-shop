import {ProducerShareRequestDto} from "./ProducerShareRequestDto.tsx";
import {GenreType} from "./newDto/enums/GenreType.ts";

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