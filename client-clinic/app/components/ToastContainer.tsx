"use client";
/**
 * FILE: ToastContainer.tsx
 * MÔ TẢ: Thành phần quản lý hiển thị các thông báo nổi (Toast notifications) như Thành công, Lỗi, Thông tin.
 */
import React from 'react';
import { useToast } from '../context/ToastContext';
import { X, CheckCircle, AlertCircle, Info } from 'lucide-react';

/**
 * COMPONENT: ToastContainer
 * MÔ TẢ: Container chứa danh sách các Toast đang hiển thị, đặt ở góc trên bên phải màn hình.
 */
export default function ToastContainer() {
    const { toasts, removeToast } = useToast();

    if (toasts.length === 0) return null;

    return (
        <div className="fixed top-6 right-6 z-[9999] flex flex-col gap-3 pointer-events-none">
            {toasts.map((toast) => (
                <ToastItem 
                    key={toast.id} 
                    toast={toast} 
                    onClose={() => removeToast(toast.id)} 
                />
            ))}
        </div>
    );
}

/**
 * COMPONENT CON: ToastItem
 * MÔ TẢ: Hiển thị một thông báo đơn lẻ với icon và màu sắc tương ứng với loại thông báo (success/error).
 */
function ToastItem({ toast, onClose }: { toast: any, onClose: () => void }) {
    const getStyles = () => {
        switch (toast.type) {
            case 'success':
                return {
                    bg: 'bg-white',
                    border: 'border-green-100',
                    icon: <CheckCircle className="w-5 h-5 text-green-500" />,
                    bar: 'bg-green-500'
                };
            case 'error':
                return {
                    bg: 'bg-white',
                    border: 'border-red-100',
                    icon: <AlertCircle className="w-5 h-5 text-red-500" />,
                    bar: 'bg-red-500'
                };
            default:
                return {
                    bg: 'bg-white',
                    border: 'border-blue-100',
                    icon: <Info className="w-5 h-5 text-blue-500" />,
                    bar: 'bg-blue-500'
                };
        }
    };

    const styles = getStyles();

    return (
        <div className={`
            ${styles.bg} ${styles.border} border
            min-w-[320px] max-w-md p-4 rounded-2xl shadow-xl shadow-black/5
            flex items-center gap-3 animate-in slide-in-from-right duration-300
            pointer-events-auto relative overflow-hidden group
        `}>
            {/* ICON HIỂN THỊ THEO LOẠI THÔNG BÁO (Thành công, Lỗi...) */}
            <div className="flex-shrink-0">
                {styles.icon}
            </div>
            
            {/* PHẦN NỘI DUNG THÔNG BÁO VĂN BẢN */}
            <div className="flex-grow">
                <p className="text-sm font-medium text-gray-800">
                    {toast.message}
                </p>
            </div>

            {/* NÚT ĐÓNG THÔNG BÁO NHANH */}
            <button 
                onClick={onClose}
                className="flex-shrink-0 p-1 hover:bg-gray-100 rounded-lg transition-colors text-gray-400"
            >
                <X className="w-4 h-4" />
            </button>

            {/* THANH THỜI GIAN CHỜ (Progress Bar) - Tự động chạy trong 5 giây trước khi đóng */}
            <div className="absolute bottom-0 left-0 h-1 bg-gray-100 w-full opacity-30" />
            <div 
                key={toast.updatedAt || toast.id}
                className={`absolute bottom-0 left-0 h-1 ${styles.bar} animate-toast-progress`} 
                style={{ animationDuration: '5s', animationFillMode: 'forwards' }}
            />
        </div>
    );
}

