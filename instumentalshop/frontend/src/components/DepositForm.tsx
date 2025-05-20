import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import {ProfileController} from "../controller/newControllers/ProfileController.tsx";

export const DepositForm = () => {
    const [amount, setAmount] = useState('');
    const [error, setError] = useState('');
    const { token, user } = useAuth();

    const handleDeposit = async () => {
        const num = parseFloat(amount);
        if (isNaN(num) || num <= 0) {
            setError('Enter a valid amount');
            return;
        }

        try {
            const updatedUser = await ProfileController.depositBalance(num);
            window.location.reload(); // или обнови useAuth() через setUser
        } catch (e) {
            setError((e as Error).message);
        }
    };

    if (!token) return null;

    return (
        <div className="space-y-2">
            <input
                type="number"
                min="0.01"
                step="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="p-2 rounded border"
                placeholder="Enter amount"
            />
            <button onClick={handleDeposit} className="bg-[#0088a9] text-white px-4 py-2 rounded">
                Deposit
            </button>
            {error && <div className="text-red-500">{error}</div>}
        </div>
    );
};
