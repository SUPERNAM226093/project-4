

interface Props {
    title: string;
    message: string;
    onConfirm: () => void;
    onCancel: () => void;
}

export default function ConfirmDialog({ title, message, onConfirm, onCancel }: Props) {
    

    return (
        <div className="confirm-overlay" onClick={onCancel}>
            <div className="confirm-box" onClick={(e) => e.stopPropagation()}>
                <h3>{title}</h3>
                <p>{message}</p>
                <div className="confirm-actions">
                    <button className="btn btn-ghost" onClick={onCancel}>{"Hủy"}</button>
                    <button className="btn btn-danger" onClick={onConfirm}>{"Xóa"}</button>
                </div>
            </div>
        </div>
    );
}
