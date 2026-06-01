import { useEffect, useState } from 'react';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { toast } from 'react-toastify';

import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash, HiOutlinePhoto } from 'react-icons/hi2';
import { useAuth } from '../../store/AuthContext';

interface Room {
    id: number;
    roomCode: string;
    name: string;
    floor: string;
    bedType: string;
    maxCapacity: number;
    pricePerNight: number;
    cleaningFee: number;
    serviceFee: number;
    totalBeds: number;
    availableBeds: number;
    description: string;
    status: string;
    amenities: string[];
    images: string[];
    isActive: boolean;
}

const emptyForm = {
    roomCode: '',
    name: '',
    floor: '',
    bedType: '',
    maxCapacity: '1',
    pricePerNight: '',
    cleaningFee: '30000',
    serviceFee: '20000',
    totalBeds: '1',
    description: '',
    status: 'AVAILABLE',
    isActive: true
};

export default function RoomPage() {
    const { isAdmin, isStaff } = useAuth();
    const [items, setItems] = useState<Room[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    
    const [editingId, setEditingId] = useState<number | null>(null);
    const [form, setForm] = useState(emptyForm);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const fetchData = async () => {
        try {
            const res = await api.get('/rooms/all');
            setItems(res.data);
        } catch {
            toast.error('Failed to load rooms');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const openCreate = () => {
        setForm(emptyForm);
        setEditingId(null);
        setShowModal(true);
    };

    const openEdit = (r: Room) => {
        setForm({
            roomCode: r.roomCode,
            name: r.name,
            floor: r.floor || '',
            bedType: r.bedType || '',
            maxCapacity: String(r.maxCapacity),
            pricePerNight: String(r.pricePerNight),
            cleaningFee: String(r.cleaningFee || '0'),
            serviceFee: String(r.serviceFee || '0'),
            totalBeds: String(r.totalBeds || '1'),
            description: r.description || '',
            status: r.status || 'AVAILABLE',
            isActive: r.isActive
        });
        setEditingId(r.id);
        setShowModal(true);
    };

    const handleSubmit = async () => {
        try {
            const payload = {
                ...form,
                maxCapacity: Number(form.maxCapacity),
                pricePerNight: Number(form.pricePerNight),
                cleaningFee: Number(form.cleaningFee),
                serviceFee: Number(form.serviceFee),
                totalBeds: Number(form.totalBeds)
            };
            if (editingId) {
                await api.put(`/rooms/${editingId}`, payload);
                toast.success("Updated");
            } else {
                await api.post('/rooms', payload);
                toast.success("Created");
            }
            setShowModal(false);
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Error saving room');
        }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try {
            await api.delete(`/rooms/${deleteId}`);
            toast.success("Deleted");
            setDeleteId(null);
            fetchData();
        } catch {
            toast.error('Failed to delete room');
        }
    };

    const toggleStatus = async (room: Room) => {
        try {
            await api.put(`/rooms/${room.id}`, { ...room, isActive: !room.isActive });
            toast.success('Status updated');
            fetchData();
        } catch {
            toast.error('Failed to update status');
        }
    };

    const handleImageUpload = async (id: number, file: File) => {
        const fd = new FormData();
        fd.append('file', file);
        try {
            await api.post(`/rooms/${id}/image`, fd);
            toast.success('Image uploaded');
            fetchData();
        } catch (err: any) {
            toast.error(err.displayMessage || 'Upload failed');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const value = e.target.type === 'checkbox' ? (e.target as HTMLInputElement).checked : e.target.value;
        setForm({ ...form, [e.target.name]: value });
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1>{"Phòng bệnh"}</h1>
                    <p>{"Quản lý phòng và giường bệnh"}</p>
                </div>
                {(isAdmin || isStaff) && (
                    <button className="btn btn-primary" onClick={openCreate}>
                        <HiOutlinePlus /> {"Thêm phòng"}
                    </button>
                )}
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>{"Hình ảnh"}</th>
                                <th>{"Mã phòng"}</th>
                                <th>{"Tên"}</th>
                                <th>{"Tầng"}</th>
                                <th>{"Loại giường"}</th>
                                <th>Số giường trống</th>
                                <th>{"Giá/Đêm"}</th>
                                <th>{"Trạng thái"}</th>
                                <th>{"Thao tác"}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {items.map(r => (
                                <tr key={r.id}>
                                    <td>
                                        {r.images && r.images.length > 0 ? (
                                            <img src={r.images[0]} alt="" style={{ width: 48, height: 48, borderRadius: 'var(--radius-sm)', objectFit: 'cover' }} />
                                        ) : (
                                            <div style={{ width: 48, height: 48, borderRadius: 'var(--radius-sm)', background: 'var(--bg-hover)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                                <HiOutlinePhoto size={20} />
                                            </div>
                                        )}
                                    </td>
                                    <td style={{ fontWeight: 600 }}>{r.roomCode}</td>
                                    <td>{r.name}</td>
                                    <td>{r.floor || '—'}</td>
                                    <td>{r.bedType}</td>
                                    <td>
                                        <span className={`badge ${r.availableBeds > 0 ? 'badge-success' : 'badge-danger'}`}>
                                            {r.availableBeds} / {r.totalBeds}
                                        </span>
                                    </td>
                                    <td style={{ color: 'var(--primary)', fontWeight: 600 }}>
                                        {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(r.pricePerNight)}
                                    </td>
                                    <td>
                                        <button 
                                            className={`badge ${r.isActive ? 'badge-success' : 'badge-danger'}`}
                                            onClick={() => (isAdmin || isStaff) && toggleStatus(r)}
                                            style={{ cursor: (isAdmin || isStaff) ? 'pointer' : 'default', border: 'none' }}
                                        >
                                            {r.isActive ? "Hoạt động" : "Ngừng hoạt động"}
                                        </button>
                                    </td>
                                    <td>
                                        <div className="table-actions">
                                            {(isAdmin || isStaff) && (
                                                <>
                                                    <label className="btn-icon" title="Upload Image" style={{ cursor: 'pointer' }}>
                                                        <HiOutlinePhoto />
                                                        <input type="file" accept="image/*" hidden onChange={e => { if (e.target.files?.[0]) handleImageUpload(r.id, e.target.files[0]); }} />
                                                    </label>
                                                    <button className="btn-icon" onClick={() => openEdit(r)}><HiOutlinePencil /></button>
                                                    <button className="btn-icon" onClick={() => setDeleteId(r.id)}><HiOutlineTrash /></button>
                                                </>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            {items.length === 0 && (
                                <tr>
                                    <td colSpan={8} className="empty-state">{"Không tìm thấy phòng nào"}</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {showModal && (
                <Modal title={editingId ? "Sửa phòng" : "Thêm phòng mới"} onClose={() => setShowModal(false)}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>{"Mã phòng"}</label>
                            <input name="roomCode" className="form-control" value={form.roomCode} onChange={handleChange} placeholder="P101" />
                        </div>
                        <div className="form-group">
                            <label>{"Tên phòng"}</label>
                            <input name="name" className="form-control" value={form.name} onChange={handleChange} placeholder="Phòng Tiêu chuẩn" />
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label>{"Tầng"}</label>
                            <input name="floor" className="form-control" value={form.floor} onChange={handleChange} placeholder="1" />
                        </div>
                        <div className="form-group">
                            <label>{"Loại giường"}</label>
                            <input name="bedType" className="form-control" value={form.bedType} onChange={handleChange} placeholder="1 giường đơn" />
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label>{"Giá/Đêm"}</label>
                            <input name="pricePerNight" type="number" className="form-control" value={form.pricePerNight} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>{"Sức chứa"}</label>
                            <input name="maxCapacity" type="number" className="form-control" value={form.maxCapacity} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Tổng số giường (Total Beds)</label>
                            <input name="totalBeds" type="number" className="form-control" value={form.totalBeds} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>Trạng thái phòng</label>
                            <select name="status" className="form-control" value={form.status} onChange={handleChange}>
                                <option value="AVAILABLE">AVAILABLE</option>
                                <option value="UNAVAILABLE">UNAVAILABLE</option>
                                <option value="MAINTENANCE">MAINTENANCE</option>
                            </select>
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Phí vệ sinh (Cleaning Fee)</label>
                            <input name="cleaningFee" type="number" className="form-control" value={form.cleaningFee} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>Phí dịch vụ (Service Fee)</label>
                            <input name="serviceFee" type="number" className="form-control" value={form.serviceFee} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label>{"Mô tả"}</label>
                        <textarea name="description" className="form-control" value={form.description} onChange={handleChange} rows={3} />
                    </div>
                    <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <input type="checkbox" name="isActive" id="isActive" checked={form.isActive} onChange={handleChange} />
                        <label htmlFor="isActive" style={{ margin: 0 }}>{"Hoạt động"}</label>
                    </div>
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>{"Hủy"}</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>
                            {editingId ? "Cập nhật" : "Tạo mới"}
                        </button>
                    </div>
                </Modal>
            )}

            {deleteId && (
                <ConfirmDialog 
                    title={"Xóa phòng"} 
                    message={"Bạn có chắc chắn muốn xóa phòng này?"} 
                    onConfirm={handleDelete} 
                    onCancel={() => setDeleteId(null)} 
                />
            )}
        </div>
    );
}
