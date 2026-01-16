import { ReactNode } from 'react';

interface SectionProps {
  title: string;
  children: ReactNode;
}

export default function Section({ title, children }: SectionProps) {
  return (
    <div className="mb-8">
      <h2 className="text-xl font-semibold text-[#e5e5e5] mb-4">{title}</h2>
      <div className="bg-[#121212] border border-[#2a2a2a] rounded-lg p-6">
        {children}
      </div>
    </div>
  );
}
