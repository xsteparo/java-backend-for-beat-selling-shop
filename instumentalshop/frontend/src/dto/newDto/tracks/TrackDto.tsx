import {GenreType} from "../enums/GenreType.ts";
import {LicenceTemplateDto} from "../licence/LicenceTemplateDto.tsx";

export interface TrackDto {
    id: number;
    name: string;
    genreType: GenreType;
    key: string;
    price: number;
    length: string;
    bpm: number;
    licenceTemplates: LicenceTemplateDto[];
    urlNonExclusive: string;
    urlPremium: string;
    urlExclusive: string;
    rating: number;
    likes: number;
    plays: number;
    keyType: string;
    producerUsername: string;
    purchased: boolean;
}