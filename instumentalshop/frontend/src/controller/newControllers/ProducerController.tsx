import { ProducerPurchaseStatisticDto } from "../../dto/newDto/producer/ProducerPurchaseStatisticDto";
import {PurchaseDto} from "../../dto/newDto/purchase/PurchaseDto.tsx";

const BASE = '/api/v1/producers';

function getAuthHeader(): Record<string, string> {
    const token = localStorage.getItem('beatshop_jwt');
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export class ProducerController {
    static async getSales(): Promise<PurchaseDto[]> {
        const res = await fetch(`${BASE}/me/sales`, {
            headers: getAuthHeader()
        });

        if (!res.ok) {
            throw new Error(`Failed to fetch sales: ${res.status}`);
        }

        return res.json();
    }

    static async getCustomerStats(): Promise<ProducerPurchaseStatisticDto[]> {
        const res = await fetch('/api/v1/producers/me/customer-stats', {
            headers: getAuthHeader(),
        });

        if (!res.ok) {
            throw new Error(`Failed to fetch stats: ${res.status}`);
        }

        return res.json();
    }
}
