import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';
import './MainLayout.css';

/**
 * COMPONENT: MainLayout
 * MÔ TẢ: Bố cục chính (Layout) của trang quản trị.
 * - Chứa thanh Sidebar điều hướng bên trái.
 * - Chứa thanh Header trên cùng hiển thị lời chào và thông tin tài khoản.
 * - Phần chính (main-body) chứa `<Outlet />` để render nội dung của các router con một cách động.
 */
export default function MainLayout() {
    return (
        <div className="main-layout">
            {/* Thanh menu điều hướng bên trái */}
            <Sidebar />
            <div className="main-content">
                {/* Thanh tiêu đề trên cùng */}
                <Header />
                {/* Phần nội dung chính hiển thị các trang */}
                <main className="main-body">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
