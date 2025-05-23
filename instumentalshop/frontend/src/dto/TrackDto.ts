import {ProducerTrackInfoDto} from "./ProducerTrackInfoDto.ts";
import {GenreType} from "./newDto/enums/GenreType.ts";

export interface TrackDto {
    id: number
    name: string
    genreType: GenreType
    bpm: number
    allProducersAgreedForSelling: boolean

    urlNonExclusive: string
    urlPremium?: string
    urlExclusive?: string

    producerTrackInfoDtoList: ProducerTrackInfoDto[]
    rating: number;
    length: String
    key: String
    producerUsername: String
    purchased: boolean;
}