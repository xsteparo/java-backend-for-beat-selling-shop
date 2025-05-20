import { useEffect, useState } from "react";

import { useAuth } from "../context/AuthContext";
import {PurchaseDto} from "../dto/newDto/purchase/PurchaseDto.tsx";
import {ProducerController} from "../controller/newControllers/ProducerController.tsx";
import {ProducerPurchaseStatisticDto} from "../dto/newDto/producer/ProducerPurchaseStatisticDto.tsx";

export const SalesPage = () => {
    const { role } = useAuth();
    const [sales, setSales] = useState<PurchaseDto[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [stats, setStats] = useState<ProducerPurchaseStatisticDto[]>([]);

    useEffect(() => {
        ProducerController.getSales().then(setSales).catch((e) => setError(e.message));
        ProducerController.getCustomerStats().then(setStats).catch((e) => setError(e.message));
    }, []);

    useEffect(() => {
        ProducerController.getSales()
            .then(setSales)
            .catch((e) => setError(e.message));
    }, []);

    if (role !== "producer") {
        return <div className="text-white p-6">Access denied. Only producers can view this page.</div>;
    }

    return (
        <div className="max-w-5xl mx-auto px-6 py-8 text-white">
            <h1 className="text-3xl font-bold mb-6">Your Sales</h1>

            {error && <div className="text-red-500 mb-4">{error}</div>}

            {sales.length === 0 ? (
                <p>No sales yet.</p>
            ) : (
                <table className="w-full text-sm text-left border border-gray-700">
                    <thead className="bg-[#1f1f1f] text-gray-300 uppercase">
                    <tr>
                        <th className="px-4 py-2">Track ID</th>
                        <th className="px-4 py-2">License</th>
                        <th className="px-4 py-2">Price</th>
                        <th className="px-4 py-2">Purchase Date</th>
                        <th className="px-4 py-2">Expires</th>
                        {/*<th className="px-4 py-2">Platforms</th>*/}
                    </tr>
                    </thead>
                    <tbody>
                    {sales.map((sale) => (
                        <tr key={sale.purchaseId} className="border-t border-gray-700 hover:bg-[#1e1e1e]">
                            <td className="px-4 py-2">{sale.trackId}</td>
                            <td className="px-4 py-2">{sale.licenceType}</td>
                            <td className="px-4 py-2">${sale.price.toFixed(2)}</td>
                            <td className="px-4 py-2">{new Date(sale.purchaseDate).toLocaleString()}</td>
                            <td className="px-4 py-2">{new Date(sale.expiredDate).toLocaleString()}</td>
                            {/*<td className="px-4 py-2">*/}
                            {/*    {sale.availablePlatforms.map(p => p.name).join(", ")}*/}
                            {/*</td>*/}
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

            <div className="mt-10">
                <h2 className="text-2xl font-semibold mb-4">Customer Statistics</h2>

                {stats.length === 0 ? (
                    <p>No customer purchases yet.</p>
                ) : (
                    <table className="w-full text-sm text-left border border-gray-700">
                        <thead className="bg-[#1f1f1f] text-gray-300 uppercase">
                        <tr>
                            <th className="px-4 py-2">Customer</th>
                            <th className="px-4 py-2">Total Purchases</th>
                            <th className="px-4 py-2">Last Purchase</th>
                        </tr>
                        </thead>
                        <tbody>
                        {stats.map((s) => (
                            <tr key={s.customerId} className="border-t border-gray-700 hover:bg-[#1e1e1e]">
                                <td className="px-4 py-2">{s.customerUsername}</td>
                                <td className="px-4 py-2">{s.totalPurchases}</td>
                                <td className="px-4 py-2">{new Date(s.lastPurchaseDate).toLocaleString()}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};
