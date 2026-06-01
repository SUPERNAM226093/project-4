// ============================================================
// Types cho trang Quyền truy cập (RoleUrlPage)
// ============================================================

/** Thông tin cơ bản của một vai trò (Role) */
export interface Role {
    id: number;
    name: string;
}

/**
 * Trạng thái 4 hành động của một module.
 * view   → GET
 * create → POST
 * edit   → PUT + PATCH
 * delete → DELETE
 */
export interface ModuleActions {
    view: boolean;
    create: boolean;
    edit: boolean;
    delete: boolean;
}

/** Định nghĩa một module trong catalog (key, tên hiển thị, URL pattern) */
export interface ModuleDefinition {
    key: string;
    label: string;
    path: string;
}

/** Nhóm các module liên quan (Admin, Nhân sự, Đặt chỗ...) */
export interface ModuleGroup {
    id: string;
    label: string;
    modules: ModuleDefinition[];
}

/** Response từ GET /api/roles/{id}/permission-matrix */
export interface PermissionMatrixResponse {
    roleId: number;
    roleName: string;
    /** Map từ moduleKey → trạng thái 4 checkbox */
    modules: Record<string, ModuleActions>;
    /** Luôn rỗng sau khi đơn giản hóa (không còn MANUAL) */
    manualPermissions: RoleUrl[];
    /** Luôn rỗng sau khi đơn giản hóa */
    warnings: PermissionWarning[];
}

/** Response từ GET /api/permission-matrix/catalog */
export interface CatalogResponse {
    groups: ModuleGroup[];
}

/** Cấu trúc một bản ghi quyền URL trong DB (dùng cho Sidebar) */
export interface RoleUrl {
    id: number;
    roleId: number;
    roleName: string;
    urlPattern: string;
    httpMethod: string;
    description: string;
    permissionSource?: string;
    matrixModule?: string | null;
}

/** Cảnh báo xung đột (giữ lại để tương thích type, không dùng trong UI) */
export interface PermissionWarning {
    code: string;
    message: string;
    moduleKey?: string;
}

/** Tạo object ModuleActions với tất cả = false (chưa tick) */
export const emptyActions = (): ModuleActions => ({
    view: false,
    create: false,
    edit: false,
    delete: false,
});

/** Kiểm tra xem tất cả 4 hành động có được tick không */
export const isAllActions = (a: ModuleActions) =>
    a.view && a.create && a.edit && a.delete;

/**
 * Kiểm tra trạng thái "indeterminate" của checkbox tổng (Tất cả).
 * Trả về true khi tick 1 phần (không phải 0 hoặc 4/4).
 */
export const isIndeterminate = (a: ModuleActions) => {
    const any = a.view || a.create || a.edit || a.delete;
    return any && !isAllActions(a);
};
