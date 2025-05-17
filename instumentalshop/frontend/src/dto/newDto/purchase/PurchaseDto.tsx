import {Platform} from "../enums/Platform.tsx";
import {ProducerDto} from "../producer/ProducerDto.tsx";
import {LicenceType} from "../enums/LicenceType.tsx";

export interface PurchaseDto {
    purchaseId: number;
    trackId: number;
    licenceType: LicenceType;
    producer: ProducerDto;
    price: number;
    purchaseDate: string;
    expiredDate: string;
    validityPeriodDays: number;
    availablePlatforms: Platform[];
}