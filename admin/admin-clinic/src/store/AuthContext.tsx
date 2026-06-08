import { createContext, useContext, useState, useCallback, useEffect, type ReactNode } from 'react';
import api from '../services/api';

interface User {
    userId: number;
    email: string;
    fullName: string;
    role: string;
}

interface AuthContextType {
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    isAdmin: boolean;
    isDoctor: boolean;
    isStaff: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    updateUser: (user: Partial<User>) => void;
    allowedPaths: Set<string>;
    isPathAllowed: (path: string) => boolean;
    isPermissionsLoaded: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(() => {
        try {
            const stored = localStorage.getItem('user');
            return stored ? (JSON.parse(stored) as User) : null;
        } catch {
            localStorage.removeItem('user');
            return null;
        }
    });

    const [token, setToken] = useState<string | null>(() => {
        return localStorage.getItem('token');
    });

    const [allowedPaths, setAllowedPaths] = useState<Set<string>>(new Set());
    const [isPermissionsLoaded, setIsPermissionsLoaded] = useState<boolean>(false);

    // ── Quyền cố định (hardcode) cho từng vai trò ──────────────────────────
    // STAFF: Dịch vụ & Phòng, Đăng ký dịch vụ, Gói khám sức khỏe,
    //        Đăng ký gói khám → Xem/Thêm/Sửa  |  Tư vấn, Lịch hẹn → Xem/Sửa
    const STAFF_PATHS = new Set([
        '/rooms',
        '/room-bookings',
        '/health-packages',
        '/health-package-bookings',
        '/online-consultations',
        '/appointments',
    ]);

    // DOCTOR: Tư vấn trực tuyến, Lịch hẹn, Hồ sơ bệnh án, Đơn thuốc → Xem/Sửa
    const DOCTOR_PATHS = new Set([
        '/online-consultations',
        '/appointments',
        '/medical-records',
        '/prescriptions',
    ]);

    const fetchAllowedPaths = useCallback(async (roleName: string) => {
        if (roleName === 'ADMIN') {
            setAllowedPaths(new Set(['*']));
            setIsPermissionsLoaded(true);
            return;
        }
        if (roleName === 'STAFF') {
            setAllowedPaths(STAFF_PATHS);
            setIsPermissionsLoaded(true);
            return;
        }
        if (roleName === 'DOCTOR') {
            setAllowedPaths(DOCTOR_PATHS);
            setIsPermissionsLoaded(true);
            return;
        }
        // Mọi vai trò không xác định → không cấp quyền
        setAllowedPaths(new Set(['__no_permission__']));
        setIsPermissionsLoaded(true);
    }, []);

    useEffect(() => {
        if (!user?.role) {
            setAllowedPaths(new Set());
            setIsPermissionsLoaded(true);
            return;
        }

        setIsPermissionsLoaded(false);
        fetchAllowedPaths(user.role);
    }, [user?.role, fetchAllowedPaths]);

    const isPathAllowed = useCallback((path: string) => {
        // Always allowed paths
        if (path === '/' || path === '/profile' || path === '/login') return true;
        
        if (allowedPaths.has('*')) return true;
        if (allowedPaths.has('__no_permission__')) return false;
        
        // Match exact or prefix if allowedPaths has it
        for (const p of allowedPaths) {
            if (p === path || path.startsWith(p + '/')) {
                return true;
            }
        }
        return false;
    }, [allowedPaths]);

    const login = useCallback(async (email: string, password: string) => {
        const res = await api.post('/auth/login', { email, password });
        const data = res.data;

        if (data.role === 'PATIENT' || data.role === 'USER') {
            throw new Error('Access denied. Regular users cannot access the administration portal.');
        }

        const u: User = {
            userId: data.userId,
            email: data.email,
            fullName: data.fullName,
            role: data.role,
        };

        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(u));
        setToken(data.token);
        setUser(u);
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
        // Force reload and redirect to login
        window.location.href = '/login';
    }, []);

    const updateUser = useCallback((updatedFields: Partial<User>) => {
        setUser(prev => {
            if (!prev) return null;
            const updated = { ...prev, ...updatedFields };
            localStorage.setItem('user', JSON.stringify(updated));
            return updated;
        });
    }, []);

    const isAuthenticated = !!token && !!user;
    const isAdmin = user?.role === 'ADMIN';
    const isDoctor = user?.role === 'DOCTOR';
    const isStaff = user?.role === 'STAFF';

    return (
        <AuthContext.Provider value={{ 
            user, 
            token, 
            isAuthenticated, 
            isAdmin, 
            isDoctor, 
            isStaff, 
            login, 
            logout, 
            updateUser,
            allowedPaths,
            isPathAllowed,
            isPermissionsLoaded
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}
