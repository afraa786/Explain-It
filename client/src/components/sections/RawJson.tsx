import { useState } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';

interface RawJsonProps {
  data: unknown;
}

export default function RawJson({ data }: RawJsonProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="mb-8">
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="flex items-center gap-2 text-[#e5e5e5] hover:text-[#a3a3a3] transition-colors mb-4"
      >
        {isExpanded ? (
          <ChevronDown className="w-5 h-5" />
        ) : (
          <ChevronRight className="w-5 h-5" />
        )}
        <h2 className="text-xl font-semibold">Raw JSON</h2>
      </button>

      {isExpanded && (
        <div className="bg-[#0f0f0f] border border-[#2a2a2a] rounded-lg p-6 overflow-x-auto">
          <pre className="text-[#e5e5e5] font-mono text-sm">
            {JSON.stringify(data, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
}
