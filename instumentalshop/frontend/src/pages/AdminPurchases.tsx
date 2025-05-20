import { useEffect, useState } from "react";
import { PurchaseDto } from "../dto/newDto/purchase/PurchaseDto";
import {AdminController} from "../controller/newControllers/AdminController.tsx";


export const AdminPurchasesPage = () => {
    const [purchases, setPurchases] = useState<PurchaseDto[]>([]);
    const [error, setError] = useState<string | null>(null);

    const fetchData = () => {
        AdminController.getAllPurchases()
            .then(setPurchases)
            .catch((e) => setError(e.message));
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleDelete = async (id: number) => {
        if (!confirm("Are you sure you want to delete this purchase?")) return;
        try {
            await AdminController.deletePurchase(id);
            setPurchases((prev) => prev.filter((p) => p.purchaseId !== id));
        } catch (e) {
            alert("Failed to delete purchase");
        }
    };

    return (
        <div className="max-w-6xl mx-auto px-6 py-8 text-white">
            <h1 className="text-3xl font-bold mb-6">All Purchases</h1>

            {error && <p className="text-red-500 mb-4">{error}</p>}

            <table className="w-full text-sm text-left border border-gray-700">
                <thead className="bg-[#1f1f1f] text-gray-300 uppercase">
                <tr>
                    <th className="px-4 py-2">ID</th>
                    <th className="px-4 py-2">Track</th>
                    <th className="px-4 py-2">License</th>
                    <th className="px-4 py-2">Price</th>
                    <th className="px-4 py-2">Purchased</th>
                    <th className="px-4 py-2">Actions</th>
                </tr>
                </thead>
                <tbody>
                {purchases.map((p) => (
                    <tr key={p.purchaseId} className="border-t border-gray-700 hover:bg-[#1e1e1e]">
                        <td className="px-4 py-2">{p.purchaseId}</td>
                        <td className="px-4 py-2">{p.trackId}</td>
                        <td className="px-4 py-2">{p.licenceType}</td>
                        <td className="px-4 py-2">${p.price.toFixed(2)}</td>
                        <td className="px-4 py-2">{new Date(p.purchaseDate).toLocaleString()}</td>
                        <td className="px-4 py-2">
                            <button
                                onClick={() => handleDelete(p.purchaseId)}
                                className="text-red-500 hover:text-red-400"
                            >
                                ✕
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};
