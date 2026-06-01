"use client";

import React, { createContext, useContext, useState, useCallback, ReactNode, useRef } from 'react';

type ToastType = 'success' | 'error' | 'info';

interface Toast {
    id: string;
    message: string;
    type: ToastType;
    updatedAt?: number;
}

interface ToastContextType {
    showToast: (message: string, type?: ToastType) => void;
    toasts: Toast[];
    removeToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export function ToastProvider({ children }: { children: ReactNode }) {
    const [toasts, setToasts] = useState<Toast[]>([]);
    const timeoutsRef = useRef<Record<string, NodeJS.Timeout>>({});

    const removeToast = useCallback((id: string) => {
        if (timeoutsRef.current[id]) {
            clearTimeout(timeoutsRef.current[id]);
            delete timeoutsRef.current[id];
        }
        setToasts((prev) => prev.filter((t) => t.id !== id));
    }, []);

    const showToast = useCallback((message: string, type: ToastType = 'info') => {
        const id = Math.random().toString(36).substring(2, 9);
        
        setToasts((prev) => {
            const existingIndex = prev.findIndex(t => t.message === message && t.type === type);
            
            if (existingIndex > -1) {
                const existingToast = prev[existingIndex];
                const existingId = existingToast.id;
                
                if (timeoutsRef.current[existingId]) {
                    clearTimeout(timeoutsRef.current[existingId]);
                }
                
                timeoutsRef.current[existingId] = setTimeout(() => {
                    removeToast(existingId);
                }, 5000);
                
                const nextToasts = [...prev];
                nextToasts[existingIndex] = {
                    ...existingToast,
                    updatedAt: Date.now()
                };
                return nextToasts;
            } else {
                timeoutsRef.current[id] = setTimeout(() => {
                    removeToast(id);
                }, 5000);
                
                return [...prev, { id, message, type, updatedAt: Date.now() }];
            }
        });
    }, [removeToast]);

    return (
        <ToastContext.Provider value={{ showToast, toasts, removeToast }}>
            {children}
        </ToastContext.Provider>
    );
}

export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error('useToast must be used within a ToastProvider');
    }
    return context;
}

