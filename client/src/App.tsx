import { useState } from 'react';
import Header from './components/Header';
import UploadCard from './components/UploadCard';
import ResultsDisplay from './components/ResultsDisplay';
import { AnalysisResult } from './types/api';
import { analyzeProject } from './utils/api';

function App() {
  const [result, setResult] = useState<AnalysisResult | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAnalyze = async (file: File) => {
    setIsLoading(true);
    setError(null);

    try {
      const data = await analyzeProject(file);
      setResult(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    setResult(null);
    setError(null);
  };

  return (
    <div className="min-h-screen bg-[#0f0f0f]">
      <Header />
      <main>
        {result ? (
          <ResultsDisplay result={result} onReset={handleReset} />
        ) : (
          <>
            <UploadCard onAnalyze={handleAnalyze} isLoading={isLoading} />
            {error && (
              <div className="max-w-2xl mx-auto px-6">
                <div className="bg-[#1a1a1a] border border-red-900/50 rounded-lg p-4">
                  <p className="text-red-400 text-sm">{error}</p>
                </div>
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default App;
