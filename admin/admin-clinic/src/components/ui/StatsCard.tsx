import type { ReactNode } from 'react';
import './StatsCard.css';

interface Props {
    icon: ReactNode;
    label: string;
    value: string | number;
    color: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'cyan' | 'pink';
}

export default function StatsCard({ icon, label, value, color }: Props) {
    return (
        <div className={`stats-card stats-card-${color}`}>
            <div className={`stats-icon-wrapper stats-icon-${color}`}>
                {icon}
            </div>
            <div className="stats-info">
                <span className="stats-value">{value}</span>
                <span className="stats-label">{label}</span>
            </div>
        </div>
    );
}
