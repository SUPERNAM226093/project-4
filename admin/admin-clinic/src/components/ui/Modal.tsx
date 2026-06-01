import { HiOutlineXMark } from 'react-icons/hi2';
import type { ReactNode } from 'react';

interface Props {
    title: string;
    children: ReactNode;
    onClose: () => void;
    wide?: boolean;
}

export default function Modal({ title, children, onClose, wide }: Props) {
    return (
        <div className="modal-overlay" onClick={onClose}>
            <div
                className="modal-content"
                style={wide ? { maxWidth: '720px' } : undefined}
                onClick={(e) => e.stopPropagation()}
            >
                <div className="modal-header">
                    <h2>{title}</h2>
                    <button className="btn-icon" onClick={onClose}>
                        <HiOutlineXMark size={20} />
                    </button>
                </div>
                {children}
            </div>
        </div>
    );
}
