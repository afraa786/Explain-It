import Section from './Section';
import { EntryPoint } from '../../types/api';

interface EntryPointsProps {
  entryPoints: EntryPoint[];
}

export default function EntryPoints({ entryPoints }: EntryPointsProps) {
  return (
    <Section title="Entry Points">
      <div className="space-y-6">
        {entryPoints.map((entry, index) => (
          <div key={index} className="space-y-2">
            <div>
              <span className="text-[#a3a3a3] text-sm">File:</span>
              <p className="text-[#e5e5e5] font-mono text-sm">{entry.file}</p>
            </div>
            <div>
              <span className="text-[#a3a3a3] text-sm">Class:</span>
              <p className="text-[#e5e5e5] font-mono text-sm">
                {entry.className}
              </p>
            </div>
            <div>
              <span className="text-[#a3a3a3] text-sm">Method:</span>
              <p className="text-[#e5e5e5] font-mono text-sm">{entry.method}</p>
            </div>
            {index < entryPoints.length - 1 && (
              <div className="border-t border-[#2a2a2a] mt-4" />
            )}
          </div>
        ))}
      </div>
    </Section>
  );
}
