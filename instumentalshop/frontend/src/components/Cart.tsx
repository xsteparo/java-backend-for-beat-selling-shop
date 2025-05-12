// src/components/Cart.tsx
import { FC } from 'react';
import {CartItem} from "../dto/CartItem.tsx";
// import { CartItem } from '../types/cart';
// import { HeartIcon } from './icons/HeartIcon';
// import { BagIcon } from './icons/BagIcon';

interface CartProps {
    items: CartItem[];
    onRemove: (index: number) => void;
}

export const Cart: FC<CartProps> = ({ items, onRemove }) => {
    const subtotal   = items.reduce((sum, i) => sum + i.price, 0);
    const fee        = items.length * 3;         // например, $3 за трек
    const grandTotal = subtotal + fee;

    return (
        <div className="fixed top-1/2 right-4 transform -translate-y-1/2 bg-gray-800 p-6 rounded-xl w-80 shadow-lg z-40">
            <h3 className="text-white text-lg mb-4">Souhrn košíku</h3>
            <div className="space-y-4">
                {items.map((ci, idx) => (
                    <div key={idx} className="flex items-center border-b border-gray-700 pb-2">
                        <div className="w-10 h-10 bg-gray-600 rounded mr-3 flex items-center justify-center">
                            <BagIcon className="w-6 h-6 text-white" />
                        </div>
                        <div className="flex-1">
                            <div className="text-white font-medium">{ci.track.name}</div>
                            <div className="text-gray-400 text-sm">Licence: {ci.license}</div>
                        </div>
                        <div className="text-white mr-3">${ci.price}</div>
                        <button onClick={() => onRemove(idx)} className="text-gray-400 hover:text-red-500">
                            ✕
                        </button>
                    </div>
                ))}
            </div>
            <div className="mt-4 text-gray-300">
                <div className="flex justify-between"><span>Celková cena položek</span><span>${subtotal.toFixed(2)}</span></div>
                <div className="flex justify-between"><span>Poplatek za službu</span><span>${fee.toFixed(2)}</span></div>
                <hr className="my-2 border-gray-700"/>
                <div className="flex justify-between font-semibold text-white"><span>Mezisoučet</span><span>${grandTotal.toFixed(2)}</span></div>
            </div>
        </div>
    );
};
