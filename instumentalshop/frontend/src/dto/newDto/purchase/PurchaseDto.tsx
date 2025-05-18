import {Platform} from "../enums/Platform.tsx";
import {LicenceType} from "../enums/LicenceType.tsx";

export interface PurchaseDto {
    purchaseId: number;
    trackId: number;
    licenceType: LicenceType;
    producer: String;
    price: number;
    purchaseDate: string;
    expiredDate: string;
    validityPeriodDays: number;
    availablePlatforms: Platform[];
}