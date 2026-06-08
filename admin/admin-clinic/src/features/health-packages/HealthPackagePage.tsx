import { useEffect, useState } from 'react';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { toast } from 'react-toastify';
import { useAuth } from '../../store/AuthContext';

import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash, HiOutlinePhoto } from 'react-icons/hi2';

interface HealthPackage {
    id: number;
    name: string;
    description: string;
    price: number;
    status: string;
    featureImageUrl: string | null;
    createdAt: string;
}

const emptyForm = { name: '', description: '', price: '', status: 'ACTIVE' };

function formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price) + 'đ';
}

export default function HealthPackagePage() {
    const [packages, setPackages] = useState<HealthPackage[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    
    const [editingId, setEditingId] = useState<number | null>(null);
    const [form, setForm] = useState(emptyForm);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const { isAdmin, isStaff } = useAuth();
    const canAdd = isAdmin || isStaff;
    const canEdit = isAdmin || isStaff;
    const canDelete = isAdmin;

    const fetchData = async () => {
        try {
            const res = await api.get('/health-packages?includeInactive=true');
            setPackages(res.data);
        } catch {
            toast.error('Failed to load health packages');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchData(); }, []);

    const openCreate = () => { setForm(emptyForm); setEditingId(null); setShowModal(true); };
    const openEdit = (hp: HealthPackage) => {
        setForm({
            name: hp.name || '',
            description: hp.description || '',
            price: hp.price ? String(hp.price) : '',
            status: hp.status || 'ACTIVE',
        });
        setEditingId(hp.id);
        setShowModal(true);
    };

    const handleSubmit = async () => {
        try {
            const payload = {
                name: form.name,
                description: form.description,
                price: form.price ? Number(form.price) : undefined,
                status: form.status,
            };
            if (editingId) {
                await api.put(`/health-packages/${editingId}`, payload);
                toast.success('Gói khám đã được cập nhật');
            } else {
                await api.post('/health-packages', payload);
                toast.success('Gói khám mới đã được tạo');
            }
            setShowModal(false);
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi lưu gói khám');
        }
    };

    const handleStatusChange = async (id: number, newStatus: string) => {
        try {
            await api.put(`/health-packages/${id}`, { status: newStatus });
            toast.success(newStatus === 'ACTIVE' ? 'Đã kích hoạt lại gói khám' : 'Đã ngưng hoạt động gói khám');
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Không thể thay đổi trạng thái');
        }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try {
            await api.delete(`/health-packages/${deleteId}`);
            toast.success('Health package deleted');
            setDeleteId(null);
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Failed to delete');
        }
    };

    const handleImageUpload = async (pkgId: number, file: File) => {
        const fd = new FormData();
        fd.append('file', file);
        try {
            await api.post(`/health-packages/${pkgId}/feature-image`, fd);
            toast.success('Image uploaded');
            fetchData();
        } catch (err: any) {
            toast.error(err.displayMessage || 'Image upload failed');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div><h1>{"Gói khám"}</h1><p>{"Quản lý gói khám sức khỏe"}</p></div>
                {canAdd && <button className="btn btn-primary" onClick={openCreate}><HiOutlinePlus /> {"Thêm Gói"}</button>}
            </div>

            <div className="table-container"><div className="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>{"Image"}</th>
                            <th>{"Name"}</th>
                            <th>{"Mô tả"}</th>
                            <th>{"Giá"}</th>
                            <th>{"Trạng thái"}</th>
                            <th>{"Actions"}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {packages.map(hp => (
                            <tr key={hp.id}>
                                <td>{hp.featureImageUrl ? <img src={hp.featureImageUrl} alt="" style={{ width: 48, height: 48, borderRadius: 8, objectFit: 'cover' }} /> : <div style={{ width: 48, height: 48, borderRadius: 8, background: 'var(--bg-hover)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><HiOutlinePhoto size={20} /></div>}</td>
                                <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{hp.name}</td>
                                <td><span style={{ maxWidth: 300, display: 'inline-block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{hp.description || '—'}</span></td>
                                <td><span className="badge badge-primary">{hp.price ? formatPrice(hp.price) : '—'}</span></td>
                                <td>
                                    <select
                                        disabled={!canEdit}
                                        value={hp.status || 'ACTIVE'}
                                        onChange={(e) => handleStatusChange(hp.id, e.target.value)}
                                        style={{
                                            padding: '4px 12px',
                                            borderRadius: '20px',
                                            fontSize: '13px',
                                            fontWeight: '600',
                                            border: 'none',
                                            cursor: 'pointer',
                                            background: hp.status === 'ACTIVE' ? '#d1fae5' : '#fee2e2',
                                            color: hp.status === 'ACTIVE' ? '#065f46' : '#991b1b',
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
                                    {canEdit && <label className="btn-icon" style={{ cursor: 'pointer' }}><HiOutlinePhoto /><input type="file" accept="image/*" hidden onChange={e => { if (e.target.files?.[0]) handleImageUpload(hp.id, e.target.files[0]); }} /></label>}
                                    {canEdit && <button className="btn-icon" onClick={() => openEdit(hp)}><HiOutlinePencil /></button>}
                                    {canDelete && <button className="btn-icon" onClick={() => setDeleteId(hp.id)}><HiOutlineTrash /></button>}
                                </div></td>
                            </tr>
                        ))}
                        {packages.length === 0 && <tr><td colSpan={6} className="empty-state">{"Không có gói khám nào"}</td></tr>}
                    </tbody>
                </table>
            </div></div>

            {showModal && (
                <Modal title={editingId ? "Sửa Gói Khám" : "Thêm Gói Khám"} onClose={() => setShowModal(false)}>
                    <div className="form-group"><label>{"Name"}</label>
                        <input name="name" className="form-control" value={form.name} onChange={handleChange} placeholder="Package name" />
                    </div>
                    <div className="form-group"><label>{"Mô tả"}</label>
                        <textarea name="description" className="form-control" value={form.description} onChange={handleChange} placeholder="Package description" rows={3} />
                    </div>
                    <div className="form-group"><label>{"Giá"}</label>
                        <input name="price" type="number" className="form-control" value={form.price} onChange={handleChange} placeholder="e.g. 500000" />
                    </div>
                    <div className="form-group">
                        <label>{"Trạng thái"}</label>
                        <select name="status" className="form-control" value={form.status} onChange={handleChange}>
                            <option value="ACTIVE">ĐANG HOẠT ĐỘNG</option>
                            <option value="INACTIVE">NGƯNG HOẠT ĐỘNG</option>
                        </select>
                    </div>
                    {!editingId && (
                        <div className="form-group">
                            <label>Hình ảnh gói khám</label>
                            <input type="file" accept="image/*" className="form-control" onChange={() => {
                                // We'll handle this separately or just store it in state
                                // For now, the user can upload after creating, but let's make it possible here too
                            }} />
                            <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '4px' }}>
                                Bạn có thể tải lên ảnh sau khi tạo gói bằng icon hình ảnh ở bảng danh sách.
                             </p>
                        </div>
                    )}
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>{"Hủy"}</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>{editingId ? "Cập nhật" : "Tạo mới"}</button>
                    </div>
                </Modal>
            )}

            {deleteId && <ConfirmDialog title={"Xóa Gói Khám"} message={"Bạn có chắc chắn muốn xóa gói khám này?"} onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />}
        </div>
    );
}
