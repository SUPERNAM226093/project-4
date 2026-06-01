import { useEffect, useState } from 'react';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { toast } from 'react-toastify';

import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash } from 'react-icons/hi2';

interface Role { id: number; name: string; isActive?: boolean; }

export default function RolePage() {
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    
    const [editingId, setEditingId] = useState<number | null>(null);
    const [name, setName] = useState('');
    const [isActive, setIsActive] = useState(true);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const fetchData = async () => {
        try { const res = await api.get(`/roles?t=${Date.now()}`); setRoles(res.data); }
        catch { toast.error('Failed to load roles'); }
        finally { setLoading(false); }
    };

    useEffect(() => { fetchData(); }, []);

    const openCreate = () => { setName(''); setIsActive(true); setEditingId(null); setShowModal(true); };
    const openEdit = (r: Role) => { setName(r.name); setIsActive(r.isActive ?? true); setEditingId(r.id); setShowModal(true); };

    const handleSubmit = async () => {
        try {
            if (editingId) { await api.put(`/roles/${editingId}`, { name, isActive }); toast.success('Đã cập nhật vai trò'); }
            else { await api.post('/roles', { name, isActive }); toast.success('Đã tạo vai trò mới'); }
            setShowModal(false); fetchData();
        } catch (err: any) { toast.error(err.response?.data?.message || 'Error saving role'); }
    };

    const toggleActive = async (r: Role) => {
        try {
            const newStatus = !(r.isActive ?? true);
            await api.put(`/roles/${r.id}`, { name: r.name, isActive: newStatus });
            toast.success(`Đã ${newStatus ? 'Kích hoạt' : 'Khóa'} vai trò ${r.name}`);
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Lỗi cập nhật trạng thái');
        }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try { await api.delete(`/roles/${deleteId}`); toast.success('Role deleted'); setDeleteId(null); fetchData(); }
        catch (err: any) { toast.error(err.response?.data?.message || 'Failed to delete role'); setDeleteId(null); }
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div><h1>{"Vai trò"}</h1><p>{"Quản lý vai trò hệ thống"}</p></div>
                <button className="btn btn-primary" onClick={openCreate}><HiOutlinePlus /> {"Thêm Vai trò"}</button>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table>
                        <thead><tr><th>{"ID"}</th><th>{"Tên vai trò"}</th><th>{"Trạng thái"}</th><th>{"Thao tác"}</th></tr></thead>
                        <tbody>
                            {roles.map(r => (
                                <tr key={r.id}>
                                    <td>{r.id}</td>
                                    <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{r.name}</td>
                                    <td>
                                        <span 
                                            className={`badge ${r.isActive !== false ? 'badge-success' : 'badge-error'}`}
                                            style={{ cursor: 'pointer' }}
                                            onClick={() => toggleActive(r)}
                                            title="Bấm để Đổi trạng thái"
                                        >
                                            {r.isActive !== false ? 'Đang hoạt động' : 'Bị khóa'}
                                        </span>
                                    </td>
                                    <td><div className="table-actions">
                                        <button className="btn-icon" title="Chỉnh sửa" onClick={() => openEdit(r)}><HiOutlinePencil /></button>
                                        <button className="btn-icon" title="Xóa" onClick={() => setDeleteId(r.id)}><HiOutlineTrash /></button>
                                    </div></td>
                                </tr>
                            ))}
                            {roles.length === 0 && <tr><td colSpan={3} className="empty-state">{"Không có vai trò nào"}</td></tr>}
                        </tbody>
                    </table>
                </div>
            </div>

            {showModal && (
                <Modal title={editingId ? "Sửa Vai trò" : "Tạo Vai trò"} onClose={() => setShowModal(false)}>
                    <div className="form-group"><label>{"Tên Vai trò"}</label><input className="form-control" value={name} onChange={e => setName(e.target.value)} placeholder="e.g. ADMIN" /></div>
                    <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '1rem' }}>
                        <input type="checkbox" id="isActive" checked={isActive} onChange={e => setIsActive(e.target.checked)} style={{ width: '1.25rem', height: '1.25rem', cursor: 'pointer' }} />
                        <label htmlFor="isActive" style={{ margin: 0, cursor: 'pointer' }}>{"Đang hoạt động"}</label>
                    </div>
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>{"Hủy"}</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>{editingId ? "Cập nhật" : "Tạo mới"}</button>
                    </div>
                </Modal>
            )}

            {deleteId && <ConfirmDialog title={"Xóa Vai trò"} message={"Bạn có chắc chắn muốn xóa vai trò này?"} onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />}
        </div>
    );
}
