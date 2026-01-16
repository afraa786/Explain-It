import { AnalysisResult } from '../types/api';

interface ResultsDisplayProps {
  result: AnalysisResult;
  onReset: () => void;
}

function ResultsDisplay({ result, onReset }: ResultsDisplayProps) {
  return (
    <div className="max-w-4xl mx-auto px-6 py-12">
      {/* Header */}
      <div className="mb-8">
        <button
          onClick={onReset}
          className="mb-4 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
        >
          ← Analyze Another Project
        </button>
        <h2 className="text-3xl font-bold text-white mb-2">Analysis Results</h2>
        <p className="text-gray-400">{result.projectMetadata.summary}</p>
      </div>

      {/* Project Metadata */}
      <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6 mb-6">
        <h3 className="text-xl font-semibold text-white mb-4">Project Overview</h3>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-gray-400 text-sm">Project Type</p>
            <p className="text-white font-semibold">{result.projectMetadata.projectType}</p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">Languages Detected</p>
            <div className="flex flex-wrap gap-2 mt-1">
              {result.projectMetadata.detectedLanguages.map((lang) => (
                <span key={lang} className="px-2 py-1 bg-blue-900/50 text-blue-200 rounded text-sm">
                  {lang}
                </span>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Frameworks */}
      <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6 mb-6">
        <h3 className="text-xl font-semibold text-white mb-4">Frameworks & Libraries</h3>
        <div className="flex flex-wrap gap-2">
          {result.projectMetadata.detectedFrameworks.length > 0 ? (
            result.projectMetadata.detectedFrameworks.map((framework) => (
              <span key={framework} className="px-3 py-1 bg-green-900/50 text-green-200 rounded">
                {framework}
              </span>
            ))
          ) : (
            <p className="text-gray-400">No frameworks detected</p>
          )}
        </div>
      </div>

      {/* Build Info */}
      {result.buildInfo && (
        <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6 mb-6">
          <h3 className="text-xl font-semibold text-white mb-4">Build Information</h3>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-gray-400 text-sm">Build Tool</p>
              <p className="text-white font-semibold">{result.buildInfo.buildTool}</p>
            </div>
            {result.buildInfo.javaVersion && (
              <div>
                <p className="text-gray-400 text-sm">Java Version</p>
                <p className="text-white font-semibold">{result.buildInfo.javaVersion}</p>
              </div>
            )}
            {result.buildInfo.springBootVersion && (
              <div>
                <p className="text-gray-400 text-sm">Spring Boot Version</p>
                <p className="text-white font-semibold">{result.buildInfo.springBootVersion}</p>
              </div>
            )}
          </div>
          {result.buildInfo.dependencies.length > 0 && (
            <div className="mt-4">
              <p className="text-gray-400 text-sm mb-2">Key Dependencies</p>
              <div className="flex flex-wrap gap-2">
                {result.buildInfo.dependencies.slice(0, 10).map((dep) => (
                  <span key={dep} className="px-2 py-1 bg-gray-700 text-gray-200 rounded text-sm">
                    {dep}
                  </span>
                ))}
                {result.buildInfo.dependencies.length > 10 && (
                  <span className="px-2 py-1 bg-gray-700 text-gray-400 rounded text-sm">
                    +{result.buildInfo.dependencies.length - 10} more
                  </span>
                )}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Entry Points */}
      {result.entryPoints.length > 0 && (
        <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6 mb-6">
          <h3 className="text-xl font-semibold text-white mb-4">Entry Points</h3>
          <div className="space-y-3">
            {result.entryPoints.map((ep, idx) => (
              <div key={idx} className="border-l-2 border-yellow-600 pl-4">
                <p className="text-yellow-200 font-mono text-sm">{ep.filePath}</p>
                <p className="text-gray-400 text-sm">
                  {ep.className}.{ep.methodName}()
                </p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* API Routes */}
      {result.apiRoutes.length > 0 && (
        <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6 mb-6">
          <h3 className="text-xl font-semibold text-white mb-4">API Routes</h3>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-600">
                  <th className="text-left py-2 px-2 text-gray-400">Method</th>
                  <th className="text-left py-2 px-2 text-gray-400">Path</th>
                  <th className="text-left py-2 px-2 text-gray-400">Controller</th>
                </tr>
              </thead>
              <tbody>
                {result.apiRoutes.slice(0, 20).map((route, idx) => (
                  <tr key={idx} className="border-b border-gray-700 hover:bg-gray-800/50">
                    <td className="py-2 px-2">
                      <span className={`px-2 py-1 rounded text-xs font-semibold ${
                        route.method === 'GET' ? 'bg-blue-900/50 text-blue-200' :
                        route.method === 'POST' ? 'bg-green-900/50 text-green-200' :
                        route.method === 'PUT' ? 'bg-yellow-900/50 text-yellow-200' :
                        route.method === 'DELETE' ? 'bg-red-900/50 text-red-200' :
                        'bg-gray-900/50 text-gray-200'
                      }`}>
                        {route.method}
                      </span>
                    </td>
                    <td className="py-2 px-2 text-white font-mono">{route.path}</td>
                    <td className="py-2 px-2 text-gray-400">{route.controller}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {result.apiRoutes.length > 20 && (
              <p className="text-gray-400 text-sm mt-4">
                +{result.apiRoutes.length - 20} more routes
              </p>
            )}
          </div>
        </div>
      )}

      {/* Project Structure */}
      {result.projectStructure && (
        <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6">
          <h3 className="text-xl font-semibold text-white mb-4">Project Structure</h3>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <p className="text-gray-400 text-sm">File Count</p>
              <p className="text-white text-2xl font-bold">{result.projectStructure.fileCount}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">Total Size</p>
              <p className="text-white text-2xl font-bold">
                {(result.projectStructure.totalSize / 1024 / 1024).toFixed(2)} MB
              </p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">Directories</p>
              <p className="text-white text-2xl font-bold">{result.projectStructure.directories.length}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ResultsDisplay;
