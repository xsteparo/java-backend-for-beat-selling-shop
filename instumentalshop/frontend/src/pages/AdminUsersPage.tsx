import { useEffect, useState } from "react";
import { AdminController } from "../controller/newControllers/AdminController";
import {UserDto} from "../dto/newDto/auth/UserDto.tsx";

export const AdminUsersPage = () => {
    const [users, setUsers] = useState<UserDto[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        AdminController.getAllUsers()
            .then(setUsers)
            .catch(e => setError(e.message));
    }, []);

    const handleDelete = async (userId: number) => {
        if (!confirm("Are you sure you want to delete this user?")) return;
        try {
            await AdminController.deleteUser(userId);
            setUsers(users => users.filter(u => u.userId !== userId));
        } catch (e) {
            alert("Failed to delete user");
        }
    };

    const handleRoleChange = async (userId: number, newRole: UserDto["role"]) => {
        try {
            const updatedUser = await AdminController.updateUserRole(userId, newRole);
            setUsers(users =>
                users.map(u => (u.userId === userId ? { ...u, role: updatedUser.role } : u))
            );
        } catch (e) {
            alert("Failed to update role");
        }
    };

    return (
        <div className="max-w-6xl mx-auto px-6 py-8 text-white">
            <h1 className="text-3xl font-bold mb-6">User Management</h1>

            {error && <p className="text-red-500 mb-4">{error}</p>}

            <table className="w-full text-sm text-left border border-gray-700">
                <thead className="bg-[#1f1f1f] text-gray-300 uppercase">
                <tr>
                    <th className="px-4 py-2">ID</th>
                    <th className="px-4 py-2">Username</th>
                    <th className="px-4 py-2">Email</th>
                    <th className="px-4 py-2">Role</th>
                    <th className="px-4 py-2">Registered</th>
                    <th className="px-4 py-2">Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.userId} className="border-t border-gray-700 hover:bg-[#1e1e1e]">
                        <td className="px-4 py-2">{user.userId}</td>
                        <td className="px-4 py-2">{user.username}</td>
                        <td className="px-4 py-2">{user.email}</td>
                        <td className="px-4 py-2">
                            <select
                                className="bg-gray-800 border border-gray-600 text-white px-2 py-1 rounded"
                                value={user.role}
                                onChange={(e) =>
                                    handleRoleChange(user.userId, e.target.value as UserDto["role"])
                                }
                            >
                                <option value="CUSTOMER">CUSTOMER</option>
                                <option value="PRODUCER">PRODUCER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </td>
                        <td className="px-4 py-2">
                            {new Date(user.registrationDate).toLocaleDateString()}
                        </td>
                        <td className="px-4 py-2">
                            <button
                                onClick={() => handleDelete(user.userId)}
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
