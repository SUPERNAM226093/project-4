import { useEffect, useState } from 'react';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { toast } from 'react-toastify';
import { t } from '../../utils/i18n';
import { useAuth } from '../../store/AuthContext';

import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash } from 'react-icons/hi2';

interface ServiceRegistration { id: number; userId: number; userName: string; serviceId: number; serviceName: string; status: string; createdAt: string; }

const statusColors: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', COMPLETED: 'info' };

export default function ServiceRegistrationPage() {
    const [items, setItems] = useState<ServiceRegistration[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    
    const [editingId, setEditingId] = useState<number | null>(null);
    const [form, setForm] = useState({ userId: '', serviceId: '', status: 'PENDING' });
    const [deleteId, setDeleteId] = useState<number | null>(null);
    const [users, setUsers] = useState<any[]>([]);
    const [services, setServices] = useState<any[]>([]);

    const { isAdmin, isStaff } = useAuth();
    const canAdd = isAdmin || isStaff;
    const canEdit = isAdmin || isStaff;
    const canDelete = isAdmin;

    const fetchData = async () => {
        try {
            const [rRes, uRes, sRes] = await Promise.all([api.get('/service-registrations'), api.get('/users'), api.get('/services')]);
            setItems(rRes.data); setUsers(uRes.data); setServices(sRes.data);
        } catch { toast.error('Failed to load'); }
        finally { setLoading(false); }
    };

    useEffect(() => { fetchData(); }, []);

    const openCreate = () => { setForm({ userId: '', serviceId: '', status: 'PENDING' }); setEditingId(null); setShowModal(true); };
    const openEdit = (r: ServiceRegistration) => {
        setForm({ userId: String(r.userId), serviceId: String(r.serviceId), status: r.status });
        setEditingId(r.id); setShowModal(true);
    };

    const handleSubmit = async () => {
        try {
            const payload = { userId: Number(form.userId), serviceId: Number(form.serviceId), status: form.status };
            if (editingId) { await api.put(`/service-registrations/${editingId}`, payload); toast.success('Updated'); }
            else { await api.post('/service-registrations', payload); toast.success('Created'); }
            setShowModal(false); fetchData();
        } catch (err: any) { toast.error(err.response?.data?.message || 'Error'); }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try { await api.delete(`/service-registrations/${deleteId}`); toast.success('Deleted'); setDeleteId(null); fetchData(); }
        catch { toast.error('Failed'); }
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div><h1>{"Đăng ký dịch vụ"}</h1><p>{"Quản lý yêu cầu đăng ký khám dịch vụ"}</p></div>
                {canAdd && <button className="btn btn-primary" onClick={openCreate}><HiOutlinePlus /> {"Thêm Đăng ký"}</button>}
            </div>

            <div className="table-container"><div className="table-wrapper">
                <table>
                    <thead><tr><th>{"ID"}</th><th>{"Người dùng"}</th><th>{"Dịch vụ"}</th><th>{"Status"}</th><th>{"Ngày tạo"}</th><th>{"Actions"}</th></tr></thead>
                    <tbody>
                        {items.map(r => (
                            <tr key={r.id}>
                                <td>{r.id}</td>
                                <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{r.userName || `User #${r.userId}`}</td>
                                <td>{r.serviceName || `Service #${r.serviceId}`}</td>
                                <td><span className={`badge badge-${statusColors[r.status] || 'info'}`}>{typeof t === 'function' ? t(`status.${r.status.toLowerCase()}`, r.status) : r.status}</span></td>
                                <td>{r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '—'}</td>
                                <td><div className="table-actions">
                                    {canEdit && <button className="btn-icon" onClick={() => openEdit(r)}><HiOutlinePencil /></button>}
                                    {canDelete && <button className="btn-icon" onClick={() => setDeleteId(r.id)}><HiOutlineTrash /></button>}
                                </div></td>
                            </tr>
                        ))}
                        {items.length === 0 && <tr><td colSpan={6} className="empty-state">{"Không có đăng ký nào"}</td></tr>}
                    </tbody>
                </table>
            </div></div>

            {showModal && (
                <Modal title={editingId ? "Sửa Đăng ký" : "Tạo Đăng ký"} onClose={() => setShowModal(false)}>
                    <div className="form-group"><label>{"Người dùng"}</label>
                        <select className="form-control" value={form.userId} onChange={e => setForm({ ...form, userId: e.target.value })}>
                            <option value="">{"Chọn Người dùng"}</option>{users.map((u: any) => <option key={u.id} value={u.id}>{u.fullName} ({u.email})</option>)}
                        </select>
                    </div>
                    <div className="form-group"><label>{"Dịch vụ"}</label>
                        <select className="form-control" value={form.serviceId} onChange={e => setForm({ ...form, serviceId: e.target.value })}>
                            <option value="">{"Chọn Dịch vụ"}</option>{services.map((s: any) => <option key={s.id} value={s.id}>{s.name}</option>)}
                        </select>
                    </div>
                    <div className="form-group"><label>{"Status"}</label>
                        <select className="form-control" value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}>
                            <option value="PENDING">{"Chờ duyệt"}</option><option value="APPROVED">{"Approved"}</option><option value="REJECTED">{"Rejected"}</option><option value="COMPLETED">{"Hoàn thành"}</option>
                        </select>
                    </div>
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>{"Hủy"}</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>{editingId ? "Cập nhật" : "Tạo mới"}</button>
                    </div>
                </Modal>
            )}

            {deleteId && <ConfirmDialog title={"Xóa Đăng ký"} message={"Bạn có chắc chắn không?"} onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />}
        </div>
    );
}
