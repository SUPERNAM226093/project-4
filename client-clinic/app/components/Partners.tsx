"use client";

import { useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { HOSPITALS } from "../lib/hospitals";

export default function Partners() {
    const router = useRouter();
    const [imgErrors, setImgErrors] = useState<Record<number, boolean>>({});

    const handleImgError = (id: number) => {
        setImgErrors((prev) => ({ ...prev, [id]: true }));
    };

    return (
        <section className="bg-white/50 py-14">
            <div className="mx-auto max-w-7xl px-4">
                <div className="mb-8 text-center">
                    <p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-[#4d8871]">
                        Hệ thống phòng khám
                    </p>
                    <h2 className="text-xl font-extrabold tracking-tight text-[#0a3d2e] md:text-2xl">
                        Cơ sở y tế
                    </h2>
                    <div className="mx-auto mt-3 h-1 w-14 rounded-full bg-[#0d6b52] opacity-20" />
                </div>

                <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
                    {HOSPITALS.map((hospital) => (
                        <button
                            key={hospital.id}
                            onClick={() => router.push(`/hospitals/${hospital.slug}`)}
                            className="group flex min-h-[250px] w-full flex-col items-center justify-center gap-5 rounded-2xl border border-[#b2e8d9] bg-white px-5 py-7 text-center shadow-sm transition-all duration-300 hover:-translate-y-1 hover:border-[#0d6b52] hover:shadow-[0_18px_40px_-18px_rgba(13,107,82,0.35)]"
                        >
                            <div className="relative flex h-36 w-36 items-center justify-center overflow-hidden rounded-[2rem] border border-[#b2e8d9] bg-white shadow-sm transition-all duration-300 group-hover:border-[#0d6b52] group-hover:shadow-[0_15px_30px_-10px_rgba(13,107,82,0.25)]">
                                {hospital.image && !imgErrors[hospital.id] ? (
                                    <Image
                                        src={hospital.image}
                                        alt={`Logo ${hospital.name}`}
                                        width={144}
                                        height={144}
                                        className="h-full w-full object-contain p-2 transition-transform duration-300 group-hover:scale-110"
                                        onError={() => handleImgError(hospital.id)}
                                        loading="eager"
                                    />
                                ) : (
                                    <span className={`bg-gradient-to-br ${hospital.color} bg-clip-text text-5xl font-black text-transparent`}>
                                        {hospital.name.charAt(0)}
                                    </span>
                                )}
                            </div>

                            <p className="line-clamp-2 text-sm font-extrabold leading-relaxed text-[#0a3d2e] transition-colors group-hover:text-[#0d6b52]">
                                {hospital.name}
                                {hospital.verified && (
                                    <span className="ml-1 inline-block text-[#0d6b52]">
                                        <svg className="inline h-3.5 w-3.5" fill="currentColor" viewBox="0 0 20 20">
                                            <path
                                                fillRule="evenodd"
                                                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                                                clipRule="evenodd"
                                            />
                                        </svg>
                                    </span>
                                )}
                            </p>
                        </button>
                    ))}
                </div>
            </div>
        </section>
    );
}
