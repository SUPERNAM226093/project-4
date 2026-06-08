import { useEffect, useState } from 'react';
import api from '../../services/api';

interface Role { id: number; name: string; isActive?: boolean; }

/**
 * RolePage — Trang hiển thị danh sách Vai trò hệ thống (chỉ đọc).
 * Hệ thống cố định 4 vai trò: ADMIN, DOCTOR, PATIENT, STAFF.
 * Không cho phép thêm, sửa hoặc xóa vai trò.
 */
export default function RolePage() {
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get(`/roles?t=${Date.now()}`)
            .then(res => setRoles(res.data))
            .catch(() => {})
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    const roleColors: Record<string, string> = {
        ADMIN: '#dc2626',
        DOCTOR: '#2563eb',
        PATIENT: '#16a34a',
        STAFF: '#d97706',
    };

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1>{'Vai trò'}</h1>
                    <p>{'Hệ thống cố định 4 vai trò: ADMIN, DOCTOR, PATIENT, STAFF'}</p>
                </div>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>{'ID'}</th>
                                <th>{'Tên vai trò'}</th>
                                <th>{'Trạng thái'}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roles.map(r => (
                                <tr key={r.id}>
                                    <td>{r.id}</td>
                                    <td style={{ color: roleColors[r.name] ?? 'var(--text-primary)', fontWeight: 600 }}>
                                        {r.name}
                                    </td>
                                    <td>
                                        <span className={`badge ${r.isActive !== false ? 'badge-success' : 'badge-error'}`}>
                                            {r.isActive !== false ? 'Đang hoạt động' : 'Bị khóa'}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                            {roles.length === 0 && (
                                <tr>
                                    <td colSpan={3} className="empty-state">{'Không có vai trò nào'}</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
