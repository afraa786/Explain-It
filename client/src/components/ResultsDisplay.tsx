import { AnalysisResult } from '../types/api';

interface ResultsDisplayProps {
  result: AnalysisResult;
  onReset: () => void;
}

export default function ResultsDisplay({ result, onReset }: ResultsDisplayProps) {
  const metadata = result.projectMetadata;

  if (!metadata) {
    return (
      <div className="max-w-4xl mx-auto px-6 py-12">
        <button
          onClick={onReset}
          className="mb-4 px-4 py-2 bg-blue-600 text-white rounded"
        >
          ← Analyze Another Project
        </button>
        <div className="bg-red-900/20 border border-red-700 rounded-lg p-6">
          <p className="text-red-200">Invalid analysis result.</p>
        </div>
      </div>
    );
  }

  const {
    projectType,
    languages,
    frameworks,
    summary,
    entryPoints,
    apiRoutes,
    buildInfo,
    projectStructure,
    projectSize,
    securityHints,
    securityDetections,
    dataLayerHints,
    dataLayerDetections
  } = metadata;

  return (
    <div className="max-w-5xl mx-auto px-6 py-12 text-white">
      {/* Header */}
      <div className="mb-8">
        <button
          onClick={onReset}
          className="mb-4 px-4 py-2 bg-blue-600 rounded"
        >
          ← Analyze Another Project
        </button>
        <h2 className="text-3xl font-bold mb-2">Analysis Results</h2>
        <p className="text-gray-400">{summary ?? 'Project analysis summary'}</p>
      </div>

      {/* Project Overview */}
      <Section title="Project Overview">
        <Info label="Project Type" value={projectType ?? 'Unknown'} />
        <TagList label="Languages" items={languages} />
        <TagList label="Frameworks" items={frameworks} color="green" />
      </Section>

      {/* Build Info */}
      {buildInfo && (
        <Section title="Build Information">
          <Info label="Build Tool" value={buildInfo.buildTool} />
          <Info label="Java Version" value={buildInfo.javaVersion} />
          <Info label="Spring Boot Version" value={buildInfo.springBootVersion} />
          <TagList label="Dependencies" items={buildInfo.dependencies} />
        </Section>
      )}

      {/* Entry Points */}
      {entryPoints?.length ? (
        <Section title="Entry Points">
          {entryPoints.map((ep, i) => (
            <div key={i} className="border-l-2 border-yellow-500 pl-4 mb-2">
              <p className="font-mono text-yellow-300">
                {ep.filePath ?? 'Unknown file'}
              </p>
              <p className="text-gray-400">
                {(ep.className ?? 'Class')}.{ep.methodName ?? 'method'}()
              </p>
            </div>
          ))}
        </Section>
      ) : null}

      {/* API Routes */}
      {apiRoutes?.length ? (
        <Section title="API Routes">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-600">
                <th className="text-left">Method</th>
                <th className="text-left">Path</th>
                <th className="text-left">Controller</th>
              </tr>
            </thead>
            <tbody>
              {apiRoutes.map((r, i) => (
                <tr key={i} className="border-b border-gray-700">
                  <td>{r.method ?? 'N/A'}</td>
                  <td className="font-mono">{r.path ?? r.handler}</td>
                  <td>{r.controller ?? 'Unknown'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Section>
      ) : null}

      {/* Security */}
      {(securityHints?.length || securityDetections?.length) && (
        <Section title="Security Analysis">
          <TagList label="Security Hints" items={securityHints} color="red" />
          <DetectionList detections={securityDetections} />
        </Section>
      )}

      {/* Data Layer */}
      {(dataLayerHints?.length || dataLayerDetections?.length) && (
        <Section title="Data Layer Analysis">
          <TagList label="Hints" items={dataLayerHints} color="purple" />
          <DetectionList detections={dataLayerDetections} />
        </Section>
      )}

      {/* Project Size */}
      {projectSize && (
        <Section title="Project Size">
          <Info
            label="Total Size (MB)"
            value={((projectSize.totalSizeKb ?? 0) / 1024).toFixed(2)}
          />
       <Info label="Total Lines" value={projectSize.totalLines ?? 0} />

        </Section>
      )}

      {/* Project Structure */}
      {projectStructure && (
        <Section title="Project Structure">
          <Info label="Files" value={projectStructure.fileCount ?? 0} />
          <Info
            label="Total Size"
            value={`${((projectStructure.totalSize ?? 0) / 1024 / 1024).toFixed(2)} MB`}
          />
          <Info
            label="Directories"
            value={projectStructure.directories?.length ?? 0}
          />
        </Section>
      )}
    </div>
  );
}

/* ---------- Helper Components ---------- */

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="bg-[#1a1a1a] border border-gray-700 rounded-lg p-6 mb-6">
      <h3 className="text-xl font-semibold mb-4">{title}</h3>
      {children}
    </div>
  );
}

function Info({ label, value }: { label: string; value?: string | number }) {
  if (value === undefined || value === null) return null;
  return (
    <p className="text-gray-300">
      <span className="text-gray-400">{label}:</span> {value}
    </p>
  );
}

function TagList({
  label,
  items,
  color = 'blue'
}: {
  label: string;
  items?: string[];
  color?: string;
}) {
  if (!items?.length) return null;
  return (
    <div className="mb-3">
      <p className="text-gray-400 mb-1">{label}</p>
      <div className="flex flex-wrap gap-2">
        {items.map((item) => (
          <span
            key={item}
            className={`px-2 py-1 bg-${color}-900/50 text-${color}-200 rounded text-sm`}
          >
            {item}
          </span>
        ))}
      </div>
    </div>
  );
}

function DetectionList({
  detections
}: {
  detections?: { type?: string; description?: string }[];
}) {
  if (!detections?.length) return null;
  return (
    <div className="space-y-2">
      {detections.map((d, i) => (
        <div key={i} className="text-sm text-gray-300">
          <strong>{d.type ?? 'Issue'}:</strong> {d.description ?? 'No details'}
        </div>
      ))}
    </div>
  );
}
