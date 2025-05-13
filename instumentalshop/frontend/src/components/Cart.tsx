// src/components/Cart.tsx
import { FC } from 'react';
import {CartItem} from "../dto/CartItem.tsx";
import BagIcon from "./icons/BagIcon.tsx";
import {X} from "lucide-react";

interface CartProps {
    open: boolean;
    items: CartItem[];
    onRemove: (idx: number) => void;
    onClose: () => void;
}

export const Cart: FC<CartProps> = ({ open, items, onRemove, onClose }) => {
    const subtotal   = items.reduce((s, i) => s + i.price, 0);
    const fee        = items.length * 3;
    const grandTotal = subtotal + fee;

    return (
        <div
            className={`fixed top-0 right-0 z-50 w-96 h-screen bg-gray-800 shadow-lg
                  transform transition-transform duration-300
                  ${open ? 'translate-x-0' : 'translate-x-full'}`}
        >
            {/* ───── заголовок ───── */}
            <div className="flex items-center justify-between p-5 border-b border-gray-700">
                <h3 className="text-lg text-white">Souhrn košíku</h3>
                <button onClick={onClose} className="text-gray-400 hover:text-red-500">
                    <X size={20} />
                </button>
            </div>

            {/* ───── список позиций ───── */}
            <div className="h-[calc(100%-210px)] overflow-y-auto p-5 space-y-4">
                {items.length === 0 && (
                    <p className="text-gray-400 text-sm">Košík je prázdný.</p>
                )}

                {items.map((ci, idx) => (
                    <div key={idx} className="flex items-center border-b border-gray-700 pb-2">
                        <div className="w-10 h-10 bg-gray-600 rounded mr-3 flex items-center justify-center">
                            <BagIcon className="w-6 h-6 text-white" />
                        </div>

                        <div className="flex-1 min-w-0">
                            <div className="text-white font-medium truncate">{ci.track.name}</div>
                            <div className="text-gray-400 text-xs truncate">Licence: {ci.license}</div>
                        </div>

                        <div className="text-white mr-3 whitespace-nowrap">${ci.price}</div>

                        <button onClick={() => onRemove(idx)} className="text-gray-400 hover:text-red-500">
                            ✕
                        </button>
                    </div>
                ))}
            </div>

            {/* ───── итоги ───── */}
            <div className="p-5 border-t border-gray-700 text-gray-300 space-y-1">
                <div className="flex justify-between">
                    <span>Celková cena položek</span><span>${subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                    <span>Poplatek za službu</span><span>${fee.toFixed(2)}</span>
                </div>
                <hr className="my-2 border-gray-700" />
                <div className="flex justify-between font-semibold text-white">
                    <span>Mezisoučet</span><span>${grandTotal.toFixed(2)}</span>
                </div>
            </div>
        </div>
    );
};