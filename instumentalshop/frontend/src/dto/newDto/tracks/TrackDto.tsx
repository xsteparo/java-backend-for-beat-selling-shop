import {GenreType} from "../enums/GenreType.ts";
import {LicenceTemplateDto} from "../licence/LicenceTemplateDto.tsx";

export interface TrackDto {
    id: number;
    name: string;
    genreType: GenreType;
    bpm: number;
    licenceTemplates: LicenceTemplateDto[];
    urlNonExclusive: string;
    urlPremium: string;
    urlExclusive: string;
    rating: number;
    length: string;
    keyType: string;
    producerUsername: string;
    purchased: boolean;
}