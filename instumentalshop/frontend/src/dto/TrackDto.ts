import {ProducerTrackInfoDto} from "./ProducerTrackInfoDto.ts";
import {GenreType} from "./GenreType.ts";

export interface TrackDto {
    id: number
    name: string
    genreType: GenreType
    bpm: number
    allProducersAgreedForSelling: boolean

    urlNonExclusive: string
    urlPremium?: string
    urlExclusive?: string

    // список продюсеров и их статусов
    producerTrackInfoDtoList: ProducerTrackInfoDto[]
}