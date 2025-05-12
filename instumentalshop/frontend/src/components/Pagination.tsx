import { FC } from 'react';

interface PaginationProps {
    page: number;
    totalPages: number;
    onPageChange: (newPage: number) => void;
}

const Pagination: FC<PaginationProps> = ({ page, totalPages, onPageChange }) => {
    // if (totalPages <= 1) return null;

    return (
        <div className="flex justify-center mt-4 space-x-2">
            <button
                disabled={page <= 1}
                onClick={() => onPageChange(page - 1)}
                className="px-3 py-1 bg-gray-700 text-gray-300 rounded disabled:opacity-50"
            >
                ‹
            </button>

            {Array.from({ length: totalPages }, (_, i) => (
                <button
                    key={i + 1}
                    onClick={() => onPageChange(i + 1)}
                    className={`
            px-3 py-1 rounded
            ${page === i + 1
                        ? 'bg-green-600 text-white'
                        : 'bg-gray-700 text-gray-300 hover:bg-gray-600'}
          `}
                >
                    {i + 1}
                </button>
            ))}

            <button
                disabled={page >= totalPages}
                onClick={() => onPageChange(page + 1)}
                className="px-3 py-1 bg-gray-700 text-gray-300 rounded disabled:opacity-50"
            >
                ›
            </button>
        </div>
    );
};

export default Pagination;
