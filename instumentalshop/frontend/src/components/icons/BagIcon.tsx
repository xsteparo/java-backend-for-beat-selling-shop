// src/components/icons/BagIcon.tsx
import { FC } from 'react';

export interface BagIconProps {
    className?: string;
}

const BagIcon: FC<BagIconProps> = ({ className = '' }) => (
    <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth={2}
        strokeLinecap="round"
        strokeLinejoin="round"
        className={className}
    >
        <path d="M9 8V6a3 3 0 016 0v2" />
        <path d="M20 8H4l1 12a2 2 0 002 2h10a2 2 0 002-2l1-12z" />
    </svg>
);

export default BagIcon;
