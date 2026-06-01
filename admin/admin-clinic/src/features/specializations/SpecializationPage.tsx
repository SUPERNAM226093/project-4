import { useEffect, useState } from 'react';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { toast } from 'react-toastify';

import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash, HiOutlinePhoto } from 'react-icons/hi2';

interface Specialization { id: number; name: string; description: string; status: string; featureImageUrl: string; }

export default function SpecializationPage() {
    const [items, setItems] = useState<Specialization[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    
    const [editingId, setEditingId] = useState<number | null>(null);
    const [form, setForm] = useState({ name: '', description: '', status: 'ACTIVE' });
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const fetchData = async () => {
        try { 
            const res = await api.get('/specializations?includeInactive=true'); 
            setItems(res.data); 
        } catch { 
            toast.error('Failed to load'); 
        } finally { 
            setLoading(false); 
        }
    };

    useEffect(() => { fetchData(); }, []);

    const openCreate = () => { setForm({ name: '', description: '', status: 'ACTIVE' }); setEditingId(null); setShowModal(true); };
    const openEdit = (s: Specialization) => { setForm({ name: s.name, description: s.description || '', status: s.status || 'ACTIVE' }); setEditingId(s.id); setShowModal(true); };

    const handleSubmit = async () => {
        try {
            if (editingId) { 
                await api.put(`/specializations/${editingId}`, form); 
                toast.success('Cập nhật chuyên khoa thành công'); 
            } else { 
                await api.post('/specializations', form); 
                toast.success('Đã tạo chuyên khoa mới'); 
            }
            setShowModal(false); fetchData();
        } catch (err: any) { 
            toast.error(err.response?.data?.message || 'Có lỗi xảy ra'); 
        }
    };

    const handleStatusChange = async (id: number, newStatus: string) => {
        try {
            await api.put(`/specializations/${id}`, { status: newStatus });
            toast.success(newStatus === 'ACTIVE' ? 'Đã kích hoạt lại chuyên khoa' : 'Đã ngưng hoạt động chuyên khoa');
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Không thể thay đổi trạng thái');
        }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try { await api.delete(`/specializations/${deleteId}`); toast.success('Deleted'); setDeleteId(null); fetchData(); }
        catch (err: any) { 
            toast.error(err.response?.data?.message || 'Failed to delete'); 
        }
    };

    const handleImageUpload = async (id: number, file: File) => {
        const fd = new FormData(); fd.append('file', file);
        try { await api.post(`/specializations/${id}/feature-image`, fd, { headers: { 'Content-Type': 'multipart/form-data' } }); toast.success('Image uploaded'); fetchData(); }
        catch { toast.error('Upload failed'); }
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div><h1>{"Chuyên khoa"}</h1><p>{"Quản lý các chuyên khoa y tế"}</p></div>
                <button className="btn btn-primary" onClick={openCreate}><HiOutlinePlus /> {"Thêm chuyên khoa"}</button>
            </div>

            <div className="table-container"><div className="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>{"Image"}</th>
                            <th>{"Name"}</th>
                            <th>{"Mô tả"}</th>
                            <th>{"Trạng thái"}</th>
                            <th>{"Actions"}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {items.map(s => (
                            <tr key={s.id}>
                                <td>{s.featureImageUrl ? <img src={s.featureImageUrl} alt="" style={{ width: 36, height: 36, borderRadius: 'var(--radius-sm)', objectFit: 'cover' }} /> : <div style={{ width: 36, height: 36, borderRadius: 'var(--radius-sm)', background: 'var(--bg-hover)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><HiOutlinePhoto size={16} /></div>}</td>
                                <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{s.name}</td>
                                <td style={{ maxWidth: 300, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.description || '—'}</td>
                                <td>
                                    <select
                                        value={s.status || 'ACTIVE'}
                                        onChange={(e) => handleStatusChange(s.id, e.target.value)}
                                        style={{
                                            padding: '4px 12px',
                                            borderRadius: '20px',
                                            fontSize: '13px',
                                            fontWeight: '600',
                                            border: 'none',
                                            cursor: 'pointer',
                                            background: s.status === 'ACTIVE' ? '#d1fae5' : '#fee2e2',
                                            color: s.status === 'ACTIVE' ? '#065f46' : '#991b1b',
                                            outline: 'none',
                                            appearance: 'none',
                                            textAlign: 'center',
                                            boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)'
                                        }}
                                    >
                                        <option value="ACTIVE" style={{ background: '#fff', color: '#065f46', fontWeight: 'bold' }}>ĐANG HOẠT ĐỘNG</option>
                                        <option value="INACTIVE" style={{ background: '#fff', color: '#991b1b', fontWeight: 'bold' }}>NGƯNG HOẠT ĐỘNG</option>
                                    </select>
                                </td>
                                <td><div className="table-actions">
                                    <label className="btn-icon" style={{ cursor: 'pointer' }}><HiOutlinePhoto /><input type="file" accept="image/*" hidden onChange={e => { if (e.target.files?.[0]) handleImageUpload(s.id, e.target.files[0]); }} /></label>
                                    <button className="btn-icon" onClick={() => openEdit(s)}><HiOutlinePencil /></button>
                                    <button className="btn-icon" onClick={() => setDeleteId(s.id)}><HiOutlineTrash /></button>
                                </div></td>
                            </tr>
                        ))}
                        {items.length === 0 && <tr><td colSpan={5} className="empty-state">{"Không tìm thấy chuyên khoa nào"}</td></tr>}
                    </tbody>
                </table>
            </div></div>

            {showModal && (
                <Modal title={editingId ? "Sửa chuyên khoa" : "Thêm chuyên khoa"} onClose={() => setShowModal(false)}>
                    <div className="form-group"><label>{"Name"}</label><input className="form-control" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /></div>
                    <div className="form-group"><label>{"Mô tả"}</label><textarea className="form-control" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} /></div>
                    <div className="form-group">
                        <label>{"Trạng thái"}</label>
                        <select className="form-control" value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}>
                            <option value="ACTIVE">ĐANG HOẠT ĐỘNG</option>
                            <option value="INACTIVE">NGƯNG HOẠT ĐỘNG</option>
                        </select>
                    </div>
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>{"Hủy"}</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>{editingId ? "Cập nhật" : "Tạo mới"}</button>
                    </div>
                </Modal>
            )}

            {deleteId && <ConfirmDialog title={"Xóa chuyên khoa"} message={"Bạn có chắc chắn không?"} onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />}
        </div>
    );
}
