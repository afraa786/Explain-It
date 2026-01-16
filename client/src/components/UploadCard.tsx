import { Upload } from 'lucide-react';
import { useState, useRef, DragEvent, ChangeEvent } from 'react';

interface UploadCardProps {
  onAnalyze: (file: File) => void;
  isLoading: boolean;
}

export default function UploadCard({ onAnalyze, isLoading }: UploadCardProps) {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleDragOver = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);

    const file = e.dataTransfer.files[0];
    if (file && file.name.endsWith('.zip')) {
      setSelectedFile(file);
    }
  };

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  const handleAnalyze = () => {
    if (selectedFile) {
      onAnalyze(selectedFile);
    }
  };

  return (
    <div className="max-w-2xl mx-auto px-6 py-12">
      <div className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-lg p-8">
        <div
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          className={`border-2 border-dashed rounded-lg p-12 text-center transition-colors ${
            isDragging
              ? 'border-[#3b82f6] bg-[#1a1a2a]'
              : 'border-[#2a2a2a] bg-[#121212]'
          }`}
        >
          <Upload className="w-12 h-12 mx-auto mb-4 text-[#a3a3a3]" />
          <p className="text-[#e5e5e5] mb-2">
            Drag and drop your ZIP file here
          </p>
          <p className="text-[#a3a3a3] text-sm mb-6">or</p>
          <button
            onClick={handleUploadClick}
            disabled={isLoading}
            className="px-6 py-2 bg-[#2a2a2a] text-[#e5e5e5] rounded hover:bg-[#333333] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Choose File
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept=".zip"
            onChange={handleFileChange}
            className="hidden"
          />
        </div>

        {selectedFile && (
          <div className="mt-6">
            <p className="text-[#a3a3a3] text-sm mb-2">Selected file:</p>
            <p className="text-[#e5e5e5] font-mono text-sm mb-6">
              {selectedFile.name}
            </p>
            <button
              onClick={handleAnalyze}
              disabled={isLoading}
              className="w-full px-6 py-3 bg-[#3b82f6] text-white rounded hover:bg-[#2563eb] transition-colors disabled:opacity-50 disabled:cursor-not-allowed font-medium"
            >
              {isLoading ? 'Analyzing project...' : 'Analyze'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
