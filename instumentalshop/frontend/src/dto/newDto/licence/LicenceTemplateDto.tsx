export type LicenceType = 'NON_EXCLUSIVE' | 'PREMIUM' | 'EXCLUSIVE';

export interface LicenceTemplateDto {
    id: number;
    licenceType: LicenceType;
    price: number;
    validityPeriodDays: number | null;
}