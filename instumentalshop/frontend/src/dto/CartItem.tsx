import {TrackDto} from "./TrackDto.ts";

export type LicenseType = 'nonexclusive' | 'premium' | 'exclusive';

export interface CartItem {
    track: TrackDto;
    license: LicenseType;
    price: number;
}