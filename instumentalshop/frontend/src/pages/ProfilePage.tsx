import { useAuth } from "../context/AuthContext";
import { DepositForm } from "../components/DepositForm";
import { Link } from "react-router-dom";

export const ProfilePage = () => {
    const { user } = useAuth();

    if (!user) return <div className="text-center text-white mt-10">You are not logged in.</div>;

    return (
        <div className="max-w-2xl mx-auto p-6 text-white">
            <h1 className="text-3xl font-semibold mb-6">Your Profile</h1>

            <div className="flex items-center space-x-6 mb-6">
                <img
                    src={user.avatarUrl ? import.meta.env.VITE_API_URL + user.avatarUrl : '/images/default-avatar.png'}
                    alt="Avatar"
                    className="w-24 h-24 rounded-full object-cover border-2 border-white"
                />
                <div>
                    <h2 className="text-xl font-bold">{user.username}</h2>
                    {/*<p className="text-sm text-gray-300">{user.email}</p>*/}
                    <p className="text-sm text-gray-400">Registered: {new Date(user.registrationDate).toLocaleDateString()}</p>
                </div>
            </div>

            <div className="mb-6">
                {/*<p><span className="font-medium">Bio:</span> {user.bio || "No bio yet."}</p>*/}
                <p className="mt-2"><span className="font-medium">Balance:</span> ${user.balance?.toFixed(2) ?? '0.00'}</p>
            </div>

            <div className="mb-6">
                <h2 className="text-xl font-semibold mb-2">Deposit Funds</h2>
                <DepositForm />
            </div>

            <Link
                to="/"
                className="inline-block mt-4 text-[#0088a9] hover:underline"
            >
                ← Back to Home
            </Link>
        </div>
    );
};
