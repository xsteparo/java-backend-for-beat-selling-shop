/* src/context/CartContext.tsx */
import { createContext, useContext, useState, ReactNode } from 'react';
import { CartItem } from '../dto/CartItem';



interface CartState {
    items: CartItem[];
    open : boolean;
    addItem   : (item: CartItem) => void;
    removeIdx : (idx: number) => void;
    clear     : () => void;          // ← НОВОЕ
    toggle    : () => void;
    close     : () => void;
}

const CartContext = createContext<CartState | null>(null);

export const CartProvider = ({ children }: { children: ReactNode }) => {
    const [items, setItems] = useState<CartItem[]>([]);
    const [open,  setOpen]  = useState(false);

    const addItem = (item: CartItem) => {
        setItems(i => [...i, item]);
        setOpen(true);
    };

    const removeIdx = (idx: number) =>
        setItems(i => i.filter((_, i2) => i2 !== idx));

    const clear  = () => setItems([]);

    const toggle = () => setOpen(o => !o);
    const close  = () => setOpen(false);

    return (
        <CartContext.Provider
            value={{ items, open, addItem, removeIdx, clear, toggle, close }}
        >
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const ctx = useContext(CartContext);
    if (!ctx) throw new Error('useCart must be inside CartProvider');
    return ctx;
};
