import { useEffect, useState } from 'react';

import { toast } from 'react-toastify';
import api from '../../services/api';
import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash } from 'react-icons/hi2';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { t } from '../../utils/i18n';
import { useAuth } from '../../store/AuthContext';

interface HealthPackageBooking {
    id: number;
    patientId: number;
    patientName: string;
    healthPackageId: number;
    healthPackageName: string;
    packagePrice: number;
    bookingDate: string;
    bookingTime: string;
    status: string;
    note: string;
    createdAt: string;
}

interface UserOpt { id: number; fullName: string; email: string; }
interface PackageOpt { id: number; name: string; price: number; }

const emptyForm = { patientId: '', healthPackageId: '', bookingDate: '', bookingTime: '', status: 'PENDING', note: '' };
const statusColors: Record<string, string> = { PENDING: 'warning', CONFIRMED: 'info', COMPLETED: 'success', CANCELLED: 'danger' };

export default function HealthPackageBookingPage() {
    
    const [bookings, setBookings] = useState<HealthPackageBooking[]>([]);
    const [patients, setPatients] = useState<UserOpt[]>([]);
    const [packages, setPackages] = useState<PackageOpt[]>([]);
    
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
        setLoading(true);
        try {
            const endpoints: Promise<any>[] = [
                api.get('/health-package-bookings'),
                api.get('/users'),
                api.get('/health-packages')
            ];
            const results = await Promise.all(endpoints);
            setBookings(results[0].data);
            setPatients(results[1].data);
            setPackages(results[2].data);
        } catch {
            toast.error("Có lỗi xảy ra");
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

    const openEdit = (b: HealthPackageBooking) => {
        setForm({
            patientId: String(b.patientId),
            healthPackageId: String(b.healthPackageId),
            bookingDate: b.bookingDate || '',
            bookingTime: b.bookingTime || '',
            status: b.status || 'PENDING',
            note: b.note || ''
        });
        setEditingId(b.id);
        setShowModal(true);
    };

    const handleSubmit = async () => {
        try {
            const payload = {
                patientId: Number(form.patientId),
                healthPackageId: Number(form.healthPackageId),
                bookingDate: form.bookingDate,
                bookingTime: form.bookingTime,
                status: form.status,
                note: form.note
            };
            if (editingId) {
                await api.put(`/health-package-bookings/${editingId}`, payload);
                toast.success('Đã cập nhật thành công');
            } else {
                await api.post('/health-package-bookings', payload);
                toast.success('Đã tạo mới thành công');
            }
            setShowModal(false);
            fetchData();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Lỗi khi lưu dữ liệu');
        }
    };

    const handleDelete = async () => {
        if (!deleteId) return;
        try {
            await api.delete(`/health-package-bookings/${deleteId}`);
            toast.success('Đã xóa thành công');
            setDeleteId(null);
            fetchData();
        } catch {
            toast.error('Lỗi khi xóa');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1>Quản lý đặt gói khám</h1>
                    <p className="text-secondary">Theo dõi và cập nhật trạng thái các đơn đặt gói khám sức khỏe</p>
                </div>
                {canAdd && (
                    <button className="btn btn-primary" onClick={openCreate}>
                        <HiOutlinePlus /> Thêm mới
                    </button>
                )}
            </div>

            <div className="card">
                <div className="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Khách hàng</th>
                                <th>Gói khám</th>
                                <th>Ngày</th>
                                <th>Giờ</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bookings.length === 0 ? (
                                <tr><td colSpan={6} className="text-center py-8">Không có dữ liệu</td></tr>
                            ) : (
                                bookings.map(b => (
                                    <tr key={b.id}>
                                        <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>
                                            {b.patientName}
                                            {b.note && <div className="text-xs text-gray-400 mt-1">💬 {b.note}</div>}
                                        </td>
                                        <td>{b.healthPackageName}</td>
                                        <td>{b.bookingDate}</td>
                                        <td>{b.bookingTime?.substring(0, 5)}</td>
                                        <td>
                                            <span className={`badge badge-${statusColors[b.status] || 'secondary'}`}>
                                                {t(`status.${b.status.toLowerCase()}`, b.status)}
                                            </span>
                                        </td>
                                        <td>
                                            <div className="table-actions">
                                                {canEdit && (
                                                    <button className="btn-icon" onClick={() => openEdit(b)}>
                                                        <HiOutlinePencil />
                                                    </button>
                                                )}
                                                {canDelete && (
                                                    <button className="btn-icon" onClick={() => setDeleteId(b.id)}>
                                                        <HiOutlineTrash />
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {showModal && (
                <Modal title={editingId ? 'Cập nhật lịch gói khám' : 'Thêm lịch gói khám'} onClose={() => setShowModal(false)}>
                    <div className="form-group">
                        <label>Khách hàng</label>
                        <select name="patientId" className="form-control" value={form.patientId} onChange={handleChange}>
                            <option value="">Chọn khách hàng</option>
                            {patients.map(p => <option key={p.id} value={p.id}>{p.fullName} ({p.email})</option>)}
                        </select>
                    </div>
                    <div className="form-group">
                        <label>Gói khám</label>
                        <select name="healthPackageId" className="form-control" value={form.healthPackageId} onChange={handleChange}>
                            <option value="">Chọn gói khám</option>
                            {packages.map(p => <option key={p.id} value={p.id}>{p.name} - {new Intl.NumberFormat('vi-VN').format(p.price)}đ</option>)}
                        </select>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Ngày</label>
                            <input name="bookingDate" type="date" className="form-control" value={form.bookingDate} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>Giờ</label>
                            <input name="bookingTime" type="time" className="form-control" value={form.bookingTime} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label>Trạng thái</label>
                        <select name="status" className="form-control" value={form.status} onChange={handleChange}>
                            <option value="PENDING">Chờ duyệt</option>
                            <option value="CONFIRMED">Xác nhận</option>
                            <option value="COMPLETED">Hoàn thành</option>
                            <option value="CANCELLED">Hủy</option>
                        </select>
                    </div>
                    <div className="form-group">
                        <label>Ghi chú</label>
                        <textarea name="note" className="form-control" value={form.note} onChange={handleChange} />
                    </div>
                    <div className="form-actions">
                        <button className="btn btn-ghost" onClick={() => setShowModal(false)}>Hủy</button>
                        <button className="btn btn-primary" onClick={handleSubmit}>{editingId ? 'Cập nhật' : 'Tạo mới'}</button>
                    </div>
                </Modal>
            )}

            {deleteId && (
                <ConfirmDialog 
                    title="Xóa lịch gói khám" 
                    message="Bạn có chắc chắn muốn xóa đơn đặt gói khám này không?" 
                    onConfirm={handleDelete} 
                    onCancel={() => setDeleteId(null)} 
                />
            )}
        </div>
    );
}
