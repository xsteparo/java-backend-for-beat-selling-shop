import {LicenceType} from "../dto/newDto/enums/LicenceType";
import {useAuth} from "../context/AuthContext.tsx";
import {FC, useEffect, useState} from "react";
import {PurchaseDto} from "../dto/newDto/purchase/PurchaseDto.tsx";
import {format} from 'date-fns';
import {PurchaseController} from "../controller/newControllers/PurchaseController.tsx";
import {LicenceDownloadController} from "../controller/newControllers/LicenceDownloadController.tsx";
import {ChatController} from "../controller/newControllers/chat/ChatController.tsx";
import {useNavigate} from "react-router-dom";

const licenceLabels: Record<LicenceType, string> = {
    NON_EXCLUSIVE: 'Non-exclusive',
    PREMIUM:       'Premium',
    EXCLUSIVE:     'Exclusive',
};

const Purchases: FC = () => {
    const { role } = useAuth();
    const navigate = useNavigate();
    const [purchases, setPurchases] = useState<PurchaseDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        PurchaseController.getMyPurchases()
            .then(data => setPurchases(data))
            .catch(e => setError(e.message || 'Error'))
            .finally(() => setLoading(false));
    }, []);

    const handleDownloadLicence = async (purchaseId: number) => {
        try {
            const { data, filename } = await LicenceDownloadController.downloadLicence(purchaseId);
            const url = URL.createObjectURL(data);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            console.error('Download licence error', e);
        }
    };

    const handleDownloadTrack = async (purchaseId: number) => {
        try {
            const { data, filename } = await PurchaseController.downloadTrack(purchaseId);
            const url = URL.createObjectURL(data);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            console.error('Download track error', e);
        }
    };

    const handleStartChat = async (producerId: number) => {
        try {
            const room = await ChatController.openRoom(producerId);
            navigate('/chats', { state: { openRoomId: room.id } });
        } catch (e) {
            console.error('Start chat error', e);
        }
    };

    if (loading) {
        return <div className="text-center text-gray-400 mt-10">Loading purchases…</div>;
    }
    if (error) {
        return <div className="text-center text-red-500 mt-10">Error: {error}</div>;
    }
    if (purchases.length === 0) {
        return <div className="text-center text-gray-400 mt-10">You have no purchases yet.</div>;
    }

    return (
        <main className="min-h-screen p-6 bg-gray-900 text-white">
            <h1 className="text-3xl font-semibold mb-6 text-center">My Purchases</h1>
            <div className="overflow-x-auto">
                <table className="w-full table-auto border-collapse">
                    <thead>
                    <tr className="bg-gray-800">
                        <th className="px-4 py-2 text-left">Track ID</th>
                        <th className="px-4 py-2 text-left">Producer</th>
                        <th className="px-4 py-2 text-center">License</th>
                        <th className="px-4 py-2 text-right">Price</th>
                        <th className="px-4 py-2 text-left">Purchased</th>
                        <th className="px-4 py-2 text-left">Expires</th>
                        <th className="px-4 py-2 text-center">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {purchases.map(p => (
                        <tr key={p.purchaseId} className="odd:bg-gray-800 even:bg-gray-700">
                            <td className="px-4 py-2">
                                <a
                                    href={`/tracks/${p.trackId}`}
                                    className="text-green-400 hover:underline"
                                >
                                    {p.trackId}
                                </a>
                            </td>
                            <td className="px-4 py-2">{p.producer}</td>
                            <td className="px-4 py-2 text-center">{licenceLabels[p.licenceType]}</td>
                            <td className="px-4 py-2 text-right">${p.price.toFixed(2)}</td>
                            <td className="px-4 py-2">{format(new Date(p.purchaseDate), 'dd.MM.yyyy')}</td>
                            <td className="px-4 py-2">{format(new Date(p.expiredDate), 'dd.MM.yyyy')}</td>
                            <td className="px-4 py-2 text-center space-x-2">
                                <button
                                    onClick={() => handleDownloadLicence(p.purchaseId)}
                                    className="px-3 py-1 bg-blue-600 rounded hover:bg-blue-500 text-sm mr-2"
                                >
                                    Download PDF
                                </button>
                                <button
                                    onClick={() => handleDownloadTrack(p.purchaseId)}
                                    className="px-3 py-1 bg-green-600 rounded hover:bg-green-500 text-sm mr-2"
                                >
                                    Download Track
                                </button>
                                <button
                                    onClick={() => handleStartChat(p.producerId)}
                                    className="px-3 py-1 bg-purple-600 rounded hover:bg-purple-500 text-sm"
                                >
                                    Start Chat
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </main>
    );
};

export default Purchases;