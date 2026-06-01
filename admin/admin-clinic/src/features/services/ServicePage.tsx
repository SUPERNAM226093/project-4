import { useEffect, useState } from 'react';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { toast } from 'react-toastify';

import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash, HiOutlinePhoto } from 'react-icons/hi2';

interface Service { id: number; name: string; description: string; price: number; type: string; durationMinutes: number; createdByName: string; featureImageUrl: string; }

const emptyForm = { name: '', description: '', price: '', type: '', durationMinutes: '', createdById: '' };

export default function ServicePage() {
    const [items, setItems] = useState<Service[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    
    const [editingId, setEditingId] = useState<number | null>(null);
    const [form, setForm] = useState(emptyForm);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const fetchData = async () => {
        try { const res = await api.get('/services'); setItems(res.data); }
        catch { toast.error('Failed to load'); }
        finally { setLoading(false); }
    };

    useEffect(() => { fetchData(); }, []);

    const openCreate = () => { setForm(emptyForm); setEditingId(null); setShowModal(true); };
    const openEdit = (s: Service) => {
        setForm({ name: s.name, description: s.description || '', price: s.price ? String(s.price) : '', type: s.type || '', durationMinutes: s.durationMinutes ? String(s.durationMinutes) : '', createdById: '' });
        setEditingId(s.id); setShowModal(true);
    };

    const handleSubmit = async () => {
        try {
            const payload = { name: form.name, description: form.description, price: form.price ? Number(form.price) : undefined, type: form.type, durationMinutes: form.durationMinutes ? Number(form.durationMinutes) : undefined, createdById: form.createdById ? Number(form.createdById) : undefined };
            if (editingId) { await api.put(`/services/${editingId}`, payload); toast.success('Updated'); }
            else { await api.post('/services', payload); toast.success('Created'); }
            setShowModal(false); fetchData();
        } catch (err: any) { toast.error(err.response?.data?.message || 'Error'); }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try { await api.delete(`/services/${deleteId}`); toast.success('Deleted'); setDeleteId(null); fetchData(); }
        catch { toast.error('Failed to delete'); }
    };

    const handleImageUpload = async (id: number, file: File) => {
        const fd = new FormData(); fd.append('file', file);
        try { await api.post(`/services/${id}/feature-image`, fd, { headers: { 'Content-Type': 'multipart/form-data' } }); toast.success('Image uploaded'); fetchData(); }
        catch { toast.error('Upload failed'); }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div><h1>{"Dịch vụ"}</h1><p>{"Quản lý dịch vụ và giá khám"}</p></div>
                <button className="btn btn-primary" onClick={openCreate}><HiOutlinePlus /> {"Thêm dịch vụ"}</button>
            </div>

            <div className="table-container"><div className="table-wrapper">
                <table>
                    <thead><tr><th>{"Image"}</th><th>{"Name"}</th><th>{"Loại dịch vụ"}</th><th>{"Giá"}</th><th>{"Thời lượng"}</th><th>{"Người tạo"}</th><th>{"Actions"}</th></tr></thead>
                    <tbody>
                        {items.map(s => (
                            <tr key={s.id}>
                                <td>{s.featureImageUrl ? <img src={s.featureImageUrl} alt="" style={{ width: 36, height: 36, borderRadius: 'var(--radius-sm)', objectFit: 'cover' }} /> : <div style={{ width: 36, height: 36, borderRadius: 'var(--radius-sm)', background: 'var(--bg-hover)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><HiOutlinePhoto size={16} /></div>}</td>
                                <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{s.name}</td>
                                <td><span className="badge badge-info">{s.type || '—'}</span></td>
                                <td>{s.price ? `$${s.price}` : '—'}</td>
                                <td>{s.durationMinutes ? `${s.durationMinutes} min` : '—'}</td>
                                <td>{s.createdByName || '—'}</td>
                                <td><div className="table-actions">
                                    <label className="btn-icon" style={{ cursor: 'pointer' }}><HiOutlinePhoto /><input type="file" accept="image/*" hidden onChange={e => { if (e.target.files?.[0]) handleImageUpload(s.id, e.target.files[0]); }} /></label>
                                    <button className="btn-icon" onClick={() => openEdit(s)}><HiOutlinePencil /></button>
                                    <button className="btn-icon" onClick={() => setDeleteId(s.id)}><HiOutlineTrash /></button>
                                </div></td>
                            </tr>
                        ))}
                        {items.length === 0 && <tr><td colSpan={7} className="empty-state">{"Không tìm thấy dịch vụ nào"}</td></tr>}
                    </tbody>
                </table>
            </div></div>

            {showModal && (
                <Modal title={editingId ? "Sửa dịch vụ" : "Thêm dịch vụ"} onClose={() => setShowModal(false)}>
                    <div className="form-group"><label>{"Name"}</label><input name="name" className="form-control" value={form.name} onChange={handleChange} /></div>
                    <div className="form-group"><label>{"Mô tả"}</label><textarea name="description" className="form-control" value={form.description} onChange={handleChange} /></div>
                    <div className="form-row">
                        <div className="form-group"><label>{"Giá"}</label><input name="price" type="number" step="0.01" className="form-control" value={form.price} onChange={handleChange} /></div>
                        <div className="form-group"><label>{"Thời lượng (phút)"}</label><input name="durationMinutes" type="number" className="form-control" value={form.durationMinutes} onChange={handleChange} /></div>
                    </div>
                    <div className="form-group"><label>{"Loại dịch vụ"}</label><input name="type" className="form-control" value={form.type} onChange={handleChange} placeholder="e.g. Examination, Surgery" /></div>
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>{"Hủy"}</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>{editingId ? "Cập nhật" : "Tạo mới"}</button>
                    </div>
                </Modal>
            )}

            {deleteId && <ConfirmDialog title={"Xóa dịch vụ"} message={"Bạn có chắc chắn không?"} onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />}
        </div>
    );
}
