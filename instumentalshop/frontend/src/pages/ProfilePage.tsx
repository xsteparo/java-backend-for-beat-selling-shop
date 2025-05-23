import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ProfileController } from "../controller/newControllers/ProfileController";
import {DepositForm} from "../components/DepositForm.tsx";
import { Link } from "react-router-dom";

export const ProfilePage = () => {
    const { user, token } = useAuth();
    const [username, setUsername] = useState(user?.username ?? "");
    const [email, setEmail] = useState(user?.email ?? "");
    const [bio, setBio] = useState(user?.bio ?? "");
    const [status, setStatus] = useState<"idle" | "saving" | "saved" | "error">("idle");

    if (!user) return <div className="text-center text-white mt-10">You are not logged in.</div>;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setStatus("saving");
        try {
            await ProfileController.updateProfile({ username, email, bio });
            setStatus("saved");
            user.username = username;
            user.email = email;
            user.bio = bio;
        } catch {
            setStatus("error");
        }
    };

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
                    <p className="text-sm text-gray-400">
                        Registered: {new Date(user.registrationDate).toLocaleDateString()}
                    </p>
                </div>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4 mb-8">
                <div>
                    <label className="block text-sm text-gray-300 mb-1">Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        className="w-full p-2 bg-gray-700 border border-gray-600 rounded"
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-300 mb-1">Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        className="w-full p-2 bg-gray-700 border border-gray-600 rounded"
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-300 mb-1">Bio</label>
                    <textarea
                        value={bio}
                        onChange={e => setBio(e.target.value)}
                        className="w-full p-2 bg-gray-700 border border-gray-600 rounded"
                        rows={3}
                    />
                </div>
                <button
                    type="submit"
                    className="px-5 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded"
                >
                    Save Changes
                </button>
                {status === "saved" && <p className="text-green-400 text-sm mt-2">Profile updated.</p>}
                {status === "error" && <p className="text-red-400 text-sm mt-2">Failed to update profile.</p>}
            </form>

            <div className="mb-6">
                <p className="mt-2"><span className="font-medium">Balance:</span> ${user.balance?.toFixed(2) ?? '0.00'}</p>
            </div>
            <div className="mb-6">
                <h2 className="text-xl font-semibold mb-2">Deposit Funds</h2>
                <DepositForm />
            </div>

            <Link to="/" className="inline-block mt-4 text-[#0088a9] hover:underline">
                ← Back to Home
            </Link>
        </div>
    );
};
