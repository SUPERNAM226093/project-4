import { useCallback, useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../../services/api';
import Modal from '../../components/ui/Modal';
import { toast } from 'react-toastify';

import PermissionMatrixTable from './PermissionMatrixTable';
import type {
    CatalogResponse,
    ModuleActions,
    PermissionMatrixResponse,
    Role,
} from './types';
import { emptyActions } from './types';
import './RoleUrlPage.css';

/**
 * Lấy message lỗi từ response API (nếu có).
 * Tránh crash khi server trả về lỗi không có body.
 */
function getApiErrorMessage(err: unknown): string | undefined {
    if (!err || typeof err !== 'object' || !('response' in err)) return undefined;
    const message = (err as { response?: { data?: { message?: unknown } } }).response?.data?.message;
    return typeof message === 'string' ? message : undefined;
}

/**
 * Điền đầy đủ tất cả module từ catalog vào state.
 * Các module không có trong `partial` sẽ được đặt = emptyActions() (chưa tick).
 */
function buildFullModuleState(
    catalog: CatalogResponse,
    partial: Record<string, ModuleActions>,
): Record<string, ModuleActions> {
    const out: Record<string, ModuleActions> = {};
    for (const group of catalog.groups) {
        for (const mod of group.modules) {
            out[mod.key] = partial[mod.key] ?? emptyActions();
        }
    }
    return out;
}

/**
 * So sánh trạng thái quyền trước và sau để hiện diff trước khi lưu.
 * Trả về mảng các dòng thêm mới và mảng các dòng bị xóa.
 */
function diffPermissions(
    before: Record<string, ModuleActions>,
    after: Record<string, ModuleActions>,
    catalog: CatalogResponse,
): { added: string[]; removed: string[] } {
    const added: string[] = [];
    const removed: string[] = [];

    // Tìm label hiển thị của một module từ catalog
    const label = (key: string) =>
        catalog.groups.flatMap(g => g.modules).find(m => m.key === key)?.label ?? key;

    // Mô tả trạng thái quyền của một module thành chuỗi dễ đọc
    const describe = (key: string, a: ModuleActions) => {
        const parts: string[] = [];
        if (a.view)   parts.push('Xem');
        if (a.create) parts.push('Thêm');
        if (a.edit)   parts.push('Sửa');
        if (a.delete) parts.push('Xóa');
        return `${label(key)}: ${parts.join(', ') || '(không quyền)'}`;
    };

    const keys = new Set([...Object.keys(before), ...Object.keys(after)]);
    keys.forEach(key => {
        const b = before[key] ?? emptyActions();
        const a = after[key]  ?? emptyActions();
        const changed =
            b.view !== a.view || b.create !== a.create ||
            b.edit !== a.edit || b.delete !== a.delete;
        if (!changed) return;

        const bEmpty = !b.view && !b.create && !b.edit && !b.delete;
        const aEmpty = !a.view && !a.create && !a.edit && !a.delete;

        if (bEmpty && !aEmpty)       added.push(describe(key, a));
        else if (!bEmpty && aEmpty)  removed.push(describe(key, b));
        else {
            added.push(describe(key, a));
            removed.push(`Trước — ${describe(key, b)}`);
        }
    });
    return { added, removed };
}

/**
 * RoleUrlPage — Trang Quyền truy cập.
 *
 * Giao diện đã được đơn giản hóa:
 * - Chỉ hiện bảng Ma trận chuẩn (không còn tab Nâng cao).
 * - Chỉ cho phép phân quyền STAFF và DOCTOR (không hiện ADMIN, PATIENT).
 * - ADMIN tự động có toàn quyền (hardcode trong Filter, không cần tick).
 */
export default function RoleUrlPage() {
    
    const [searchParams, setSearchParams] = useSearchParams();

    // Danh sách tất cả role lấy từ API (sẽ được lọc ở bước hiển thị)
    const [roles, setRoles] = useState<Role[]>([]);
    // Catalog module từ backend (17 module)
    const [catalog, setCatalog] = useState<CatalogResponse | null>(null);
    // ID role đang được chọn để phân quyền
    const [selectedRoleId, setSelectedRoleId] = useState<number | ''>('');
    // Trạng thái checkbox hiện tại (đang thay đổi, chưa lưu)
    const [modules, setModules] = useState<Record<string, ModuleActions>>({});
    // Trạng thái đã lưu vào DB (dùng để tính diff khi preview)
    const [savedModules, setSavedModules] = useState<Record<string, ModuleActions>>({});
    // Loading state
    const [loading, setLoading] = useState(true);
    const [matrixLoading, setMatrixLoading] = useState(false);
    // Hiện modal xác nhận trước khi lưu
    const [showPreview, setShowPreview] = useState(false);

    /**
     * Load danh sách role và catalog module cùng lúc khi trang khởi tạo.
     * Dùng Promise.all để giảm thời gian chờ.
     */
    const loadRolesAndCatalog = useCallback(async () => {
        const t = Date.now();
        const [rolesRes, catalogRes] = await Promise.all([
            api.get(`/roles?t=${t}`),
            api.get(`/permission-matrix/catalog?t=${t}`),
        ]);
        setRoles(rolesRes.data);
        setCatalog(catalogRes.data);
    }, []);

    /**
     * Tải trạng thái quyền hiện tại của một role từ backend.
     * Gọi lại mỗi khi người dùng đổi role trong dropdown.
     */
    const loadMatrix = useCallback(async (roleId: number) => {
        setMatrixLoading(true);
        try {
            const res = await api.get<PermissionMatrixResponse>(`/roles/${roleId}/permission-matrix`);
            setModules(res.data.modules);
            setSavedModules(res.data.modules);
        } catch {
            toast.error("Không tải được ma trận phân quyền");
        } finally {
            setMatrixLoading(false);
        }
    }, []);

    // Khởi tạo: load roles + catalog, đọc roleId từ URL query param
    useEffect(() => {
        (async () => {
            try {
                await loadRolesAndCatalog();
                const qId = searchParams.get('roleId');
                if (qId) {
                    const id = Number(qId);
                    if (!Number.isNaN(id)) setSelectedRoleId(id);
                }
            } catch {
                toast.error('Không tải được dữ liệu');
            } finally {
                setLoading(false);
            }
        })();
    }, [loadRolesAndCatalog, searchParams]);

    // Mỗi khi role thay đổi → tải lại ma trận và cập nhật URL
    useEffect(() => {
        if (selectedRoleId === '') return;
        loadMatrix(selectedRoleId);
        setSearchParams({ roleId: String(selectedRoleId) }, { replace: true });
    }, [selectedRoleId, loadMatrix, setSearchParams]);

    const selectedRole = roles.find(r => r.id === selectedRoleId);

    /**
     * Lọc danh sách role: Cho phép phân quyền tất cả các role ngoại trừ ADMIN, PATIENT, USER.
     * Các role mới tạo sẽ tự động xuất hiện ở đây.
     */
    const permissionableRoles = useMemo(() =>
        roles.filter(r => !['ADMIN', 'PATIENT', 'USER'].includes(r.name.toUpperCase())),
        [roles]
    );

    // Khi đổi role → reset modules về rỗng
    const handleRoleChange = (id: number | '') => {
        setSelectedRoleId(id);
        if (id === '') {
            setModules({});
            setSavedModules({});
        }
    };

    // Bấm Lưu → hiện modal xác nhận diff trước
    const handleSaveClick = () => {
        if (!catalog || selectedRoleId === '') {
            toast.error("Vui lòng chọn chức vụ");
            return;
        }
        setShowPreview(true);
    };

    /**
     * Xác nhận lưu → gửi toàn bộ module state lên backend.
     * Backend sẽ xóa quyền MATRIX cũ và insert lại theo state mới.
     */
    const handleConfirmSave = async () => {
        if (!catalog || selectedRoleId === '') return;
        const payload = buildFullModuleState(catalog, modules);
        try {
            const res = await api.put<PermissionMatrixResponse>(
                `/roles/${selectedRoleId}/permission-matrix`,
                { modules: payload },
            );
            setModules(res.data.modules);
            setSavedModules(res.data.modules);
            setShowPreview(false);
            toast.success("Đã lưu phân quyền");
        } catch (err: unknown) {
            const msg = getApiErrorMessage(err);
            toast.error(msg || "Lưu thất bại");
        }
    };

    // Tính diff để hiển thị trong modal xác nhận
    const previewDiff = useMemo(() => {
        if (!catalog) return { added: [] as string[], removed: [] as string[] };
        return diffPermissions(savedModules, modules, catalog);
    }, [catalog, savedModules, modules]);

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;

    return (
        <div className="page-container">
            {/* Header: tiêu đề + dropdown chọn role + nút Lưu */}
            <div className="page-header">
                <div>
                    <h1>{"Quyền truy cập"}</h1>
                    <p>{"Quản lý quyền truy cập URL theo vai trò"}</p>
                </div>
                <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', flexWrap: 'wrap' }}>
                    {/* Dropdown chỉ hiện STAFF và DOCTOR */}
                    <select
                        className="form-control"
                        style={{ minWidth: 200 }}
                        value={selectedRoleId}
                        onChange={e => handleRoleChange(e.target.value ? Number(e.target.value) : '')}
                    >
                        <option value="">{"Chọn một vai trò"}</option>
                        {permissionableRoles.map(r => (
                            <option key={r.id} value={r.id}>
                                {r.name === 'STAFF' ? 'Nhân viên (STAFF)' 
                                    : r.name === 'DOCTOR' ? 'Bác sĩ (DOCTOR)' 
                                    : r.name}
                            </option>
                        ))}
                    </select>

                    {/* Nút Lưu chỉ active khi đã chọn role */}
                    <button
                        type="button"
                        className="btn btn-primary"
                        disabled={selectedRoleId === '' || matrixLoading}
                        onClick={handleSaveClick}
                    >
                        {"Lưu"}
                    </button>
                </div>
            </div>

            {/* Ghi chú: ADMIN tự động có toàn quyền */}
            <div style={{
                background: 'var(--bg-secondary)',
                border: '1px solid var(--border-color)',
                borderRadius: '8px',
                padding: '0.75rem 1rem',
                marginBottom: '1rem',
                fontSize: '0.875rem',
                color: 'var(--text-secondary)',
            }}>
                💡 <strong>ADMIN</strong> tự động có toàn quyền truy cập tất cả tính năng — không cần cấu hình.
                Trang này chỉ dùng để phân quyền cho <strong>STAFF</strong> và <strong>DOCTOR</strong>.
            </div>

            {/* Trạng thái chưa chọn role */}
            {selectedRoleId === '' && (
                <p className="empty-state">{"Vui lòng chọn chức vụ"}</p>
            )}

            {/* Loading ma trận */}
            {matrixLoading && <div className="loading-spinner"><div className="spinner" /></div>}

            {/* Bảng ma trận quyền chuẩn */}
            {!matrixLoading && catalog && selectedRoleId !== '' && (
                <PermissionMatrixTable
                    catalog={catalog}
                    modules={modules}
                    onChange={setModules}
                />
            )}

            {/* Modal xác nhận: hiển thị diff trước khi lưu */}
            {showPreview && catalog && (
                <Modal
                    title={"Xác nhận thay đổi"}
                    onClose={() => setShowPreview(false)}
                >
                    <p style={{ marginBottom: '0.75rem' }}>
                        {"Chức vụ"}: <strong>{selectedRole?.name}</strong>
                    </p>
                    <div className="diff-list">
                        {previewDiff.added.length === 0 && previewDiff.removed.length === 0 && (
                            <p>{"Không có thay đổi"}</p>
                        )}
                        {previewDiff.added.map((line, i) => (
                            <div key={`a-${i}`} className="diff-add">+ {line}</div>
                        ))}
                        {previewDiff.removed.map((line, i) => (
                            <div key={`r-${i}`} className="diff-remove">− {line}</div>
                        ))}
                    </div>
                    <div className="form-actions" style={{ marginTop: '1rem' }}>
                        <button type="button" className="btn btn-ghost" onClick={() => setShowPreview(false)}>
                            {"Hủy"}
                        </button>
                        <button type="button" className="btn btn-primary" onClick={handleConfirmSave}>
                            {"Lưu"}
                        </button>
                    </div>
                </Modal>
            )}
        </div>
    );
}
