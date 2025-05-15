import {CheckoutItemDto} from "../dto/CheckoutItemDto.tsx";
import {CheckoutResponseDto} from "../dto/CheckoutResponseDto.tsx";

export default class PurchaseController {
    private static readonly BASE = '/api/v1/licence-purchases';

    /** оплатить корзину */
    static async checkout(items: CheckoutItemDto[]): Promise<CheckoutResponseDto> {
        const res = await fetch(`${this.BASE}/checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ items }),
        });

        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Checkout failed');
        }
        return res.json() as Promise<CheckoutResponseDto>;
    }
}