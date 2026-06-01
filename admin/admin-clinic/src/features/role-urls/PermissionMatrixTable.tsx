import { useMemo, useState } from 'react';

import type { CatalogResponse, ModuleActions, ModuleDefinition } from './types';
import { emptyActions, isAllActions, isIndeterminate } from './types';

interface FlatRow {
    type: 'group' | 'module';
    groupLabel?: string;
    module?: ModuleDefinition;
    index?: number;
}

interface Props {
    catalog: CatalogResponse;
    modules: Record<string, ModuleActions>;
    onChange: (modules: Record<string, ModuleActions>) => void;
}

export default function PermissionMatrixTable({ catalog, modules, onChange }: Props) {
    
    const [search, setSearch] = useState('');
    const [showCodeColumn, setShowCodeColumn] = useState(true);
    const [showColumnMenu, setShowColumnMenu] = useState(false);

    const flatRows: FlatRow[] = useMemo(() => {
        const rows: FlatRow[] = [];
        let idx = 0;
        for (const group of catalog.groups) {
            for (const mod of group.modules) {
                const q = search.trim().toLowerCase();
                if (q && !mod.label.toLowerCase().includes(q) && !mod.key.toLowerCase().includes(q)) {
                    continue;
                }
                idx += 1;
                rows.push({ type: 'module', module: mod, index: idx });
            }
        }
        return rows;
    }, [catalog, search]);

    const updateModule = (key: string, patch: Partial<ModuleActions>) => {
        const current = modules[key] ?? emptyActions();
        onChange({ ...modules, [key]: { ...current, ...patch } });
    };

    const setRowAll = (key: string, checked: boolean) => {
        onChange({
            ...modules,
            [key]: { view: checked, create: checked, edit: checked, delete: checked },
        });
    };

    const renderCheckbox = (checked: boolean, indeterminate: boolean, onToggle: () => void) => (
        <input
            type="checkbox"
            className="matrix-checkbox"
            checked={checked}
            ref={el => {
                if (el) el.indeterminate = indeterminate;
            }}
            onChange={onToggle}
        />
    );

    return (
        <div className="matrix-toolbar">
            <div className="matrix-toolbar-row">
                <input
                    className="form-control"
                    placeholder={"Tìm nhanh module..."}
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    style={{ maxWidth: 320 }}
                />
                <div className="column-menu-wrap">
                    <button type="button" className="btn btn-ghost" onClick={() => setShowColumnMenu(v => !v)}>
                        {"Quản lý cột"}
                    </button>
                    {showColumnMenu && (
                        <div className="column-menu">
                            <label>
                                <input
                                    type="checkbox"
                                    checked={showCodeColumn}
                                    onChange={e => setShowCodeColumn(e.target.checked)}
                                />
                                {"Mã tài nguyên"}
                            </label>
                        </div>
                    )}
                </div>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="permission-matrix-table">
                        <thead>
                            <tr>
                                <th style={{ width: 48 }}>{"STT"}</th>
                                <th>{"Tên tài nguyên"}</th>
                                {showCodeColumn && (
                                    <th>{"Mã tài nguyên"}</th>
                                )}
                                <th className="text-center">{"Xem"}</th>
                                <th className="text-center">{"Thêm"}</th>
                                <th className="text-center">{"Sửa"}</th>
                                <th className="text-center">{"Xóa"}</th>
                                <th className="text-center">{"Tất cả"}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {flatRows.map((row, i) => {
                                if (row.type === 'group') {
                                    return (
                                        <tr key={`g-${row.groupLabel}-${i}`} className="matrix-group-row">
                                            <td colSpan={showCodeColumn ? 8 : 7}>
                                                <strong>{row.groupLabel}</strong>
                                            </td>
                                        </tr>
                                    );
                                }
                                const mod = row.module!;
                                const actions = modules[mod.key] ?? emptyActions();
                                return (
                                    <tr key={mod.key}>
                                        <td>{row.index}</td>
                                        <td style={{ fontWeight: 500 }}>{mod.label}</td>
                                        {showCodeColumn && (
                                            <td>
                                                <code className="matrix-code">{mod.key}</code>
                                            </td>
                                        )}
                                        <td className="text-center">
                                            {renderCheckbox(actions.view, false, () =>
                                                updateModule(mod.key, { view: !actions.view }))}
                                        </td>
                                        <td className="text-center">
                                            {renderCheckbox(actions.create, false, () =>
                                                updateModule(mod.key, { create: !actions.create }))}
                                        </td>
                                        <td className="text-center">
                                            {renderCheckbox(actions.edit, false, () =>
                                                updateModule(mod.key, { edit: !actions.edit }))}
                                        </td>
                                        <td className="text-center">
                                            {renderCheckbox(actions.delete, false, () =>
                                                updateModule(mod.key, { delete: !actions.delete }))}
                                        </td>
                                        <td className="text-center">
                                            {renderCheckbox(
                                                isAllActions(actions),
                                                isIndeterminate(actions),
                                                () => setRowAll(mod.key, !isAllActions(actions)),
                                            )}
                                        </td>
                                    </tr>
                                );
                            })}
                            {flatRows.filter(r => r.type === 'module').length === 0 && (
                                <tr>
                                    <td colSpan={showCodeColumn ? 8 : 7} className="empty-state">
                                        {"Không tìm thấy module"}
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}