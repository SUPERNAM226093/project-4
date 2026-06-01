import { useState, useEffect } from 'react';
import { useAuth } from '../../store/AuthContext';

import api from '../../services/api';
import { toast } from 'react-toastify';
import { HiOutlineUser, HiOutlinePhone, HiOutlineLockClosed, HiOutlineEye, HiOutlineEyeSlash } from 'react-icons/hi2';

export default function ProfilePage() {
    const { user, updateUser } = useAuth();
    
    const [loading, setLoading] = useState(false);
    const [fetching, setFetching] = useState(true);
    
    const [form, setForm] = useState({
        fullName: '',
        phone: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [initialForm, setInitialForm] = useState({ fullName: '', phone: '' });
    const [showPassword, setShowPassword] = useState(false);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await api.get('/users/profile');
                const data = {
                    fullName: res.data.fullName || '',
                    phone: res.data.phone || '',
                    newPassword: '',
                    confirmPassword: ''
                };
                setForm(data);
                setInitialForm({ fullName: data.fullName, phone: data.phone });
            } catch (err) {
                toast.error('Không thể tải thông tin hồ sơ');
            } finally {
                setFetching(false);
            }
        };
        fetchProfile();
    }, []);

    const isChanged = 
        form.fullName.trim() !== initialForm.fullName || 
        form.phone.trim() !== initialForm.phone || 
        form.newPassword.length > 0;

    const handleCancel = () => {
        setForm({
            ...form,
            fullName: initialForm.fullName,
            phone: initialForm.phone,
            newPassword: '',
            confirmPassword: ''
        });
        toast.info('Đã hoàn tác các thay đổi');
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (!isChanged) {
            toast.info('Không có thay đổi nào để lưu');
            return;
        }

        if (form.newPassword) {
            if (form.newPassword.length < 6) {
                toast.error('Mật khẩu mới phải có ít nhất 6 ký tự');
                return;
            }
            if (form.newPassword !== form.confirmPassword) {
                toast.error('Xác nhận mật khẩu không khớp');
                return;
            }
        }

        setLoading(true);
        try {
            const payload = {
                fullName: form.fullName.trim(),
                phone: form.phone.trim(),
                newPassword: form.newPassword || undefined
            };

            await api.put('/users/profile', payload);
            
            // Sync AuthContext
            updateUser({ fullName: payload.fullName });
            setInitialForm({ fullName: payload.fullName, phone: payload.phone });
            setForm(prev => ({ ...prev, newPassword: '', confirmPassword: '' }));
            
            toast.success('Cập nhật hồ sơ thành công!');
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi cập nhật hồ sơ');
        } finally {
            setLoading(false);
        }
    };

    if (fetching) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1>{"Hồ sơ cá nhân"}</h1>
                    <p>{"Quản lý thông tin tài khoản của bạn"}</p>
                </div>
            </div>

            <div className="table-container" style={{ padding: '2rem', maxWidth: '800px' }}>
                <form onSubmit={handleSubmit} className="profile-form-dedicated">
                    <div className="profile-section-header" style={{ marginBottom: '2rem', display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                        <div className="header-avatar" style={{ width: '80px', height: '80px', fontSize: '2rem' }}>
                            {form.fullName.charAt(0).toUpperCase() || 'U'}
                        </div>
                        <div>
                            <h2 style={{ margin: 0, color: 'var(--text-primary)' }}>{initialForm.fullName}</h2>
                            <p style={{ margin: 0, color: 'var(--text-secondary)' }}>{user?.email} • <span className="badge badge-info">{user?.role}</span></p>
                            <p style={{ margin: '0.5rem 0 0', fontSize: '0.85rem', color: 'var(--danger)' }}>* Email không thể thay đổi</p>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label><HiOutlineUser style={{ verticalAlign: 'middle', marginRight: '5px' }} /> {"Họ và tên"}</label>
                            <input 
                                type="text" 
                                className="form-control"
                                value={form.fullName}
                                onChange={e => setForm({...form, fullName: e.target.value})}
                                required
                                placeholder="Nhập họ tên đầy đủ"
                            />
                        </div>
                        <div className="form-group">
                            <label><HiOutlinePhone style={{ verticalAlign: 'middle', marginRight: '5px' }} /> {"Số điện thoại"}</label>
                            <input 
                                type="tel" 
                                className="form-control"
                                value={form.phone}
                                onChange={e => setForm({...form, phone: e.target.value})}
                                required
                                placeholder="Ví dụ: 0912345678"
                            />
                        </div>
                    </div>

                    <hr style={{ margin: '2rem 0', border: 0, borderTop: '1px solid var(--border-color)' }} />

                    <h3 style={{ marginBottom: '1.5rem', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <HiOutlineLockClosed /> {"Đổi mật khẩu"}
                    </h3>

                    <div className="form-row">
                        <div className="form-group">
                            <label>{"Mật khẩu mới"}</label>
                            <div style={{ position: 'relative' }}>
                                <input 
                                    type={showPassword ? "text" : "password"} 
                                    className="form-control"
                                    value={form.newPassword}
                                    onChange={e => setForm({...form, newPassword: e.target.value})}
                                    placeholder="Ít nhất 6 ký tự"
                                    minLength={6}
                                />
                                <button 
                                    type="button" 
                                    className="btn-icon"
                                    onClick={() => setShowPassword(!showPassword)}
                                    style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)' }}
                                >
                                    {showPassword ? <HiOutlineEyeSlash /> : <HiOutlineEye />}
                                </button>
                            </div>
                        </div>
                        <div className="form-group">
                            <label>{"Xác nhận mật khẩu mới"}</label>
                            <input 
                                type={showPassword ? "text" : "password"} 
                                className="form-control"
                                value={form.confirmPassword}
                                onChange={e => setForm({...form, confirmPassword: e.target.value})}
                                placeholder="Nhập lại mật khẩu mới"
                                required={form.newPassword.length > 0}
                            />
                        </div>
                    </div>

                    <div className="form-actions" style={{ marginTop: '3rem', justifyContent: 'flex-start', gap: '1rem' }}>
                        <button 
                            type="submit" 
                            className="btn btn-primary" 
                            disabled={loading || !isChanged}
                            style={{ minWidth: '150px' }}
                        >
                            {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
                        </button>
                        <button 
                            type="button" 
                            className="btn btn-ghost" 
                            onClick={handleCancel}
                            disabled={loading || !isChanged}
                        >
                            Hủy
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
