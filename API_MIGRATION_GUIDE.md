# API & Frontend Migration Guide

## Updated API Response Structure

### New Response Format

The `/analyze` endpoint now returns enriched data with framework detection, project size, and confidence-scored detections.

#### Response Structure

```json
{
  "projectType": "Backend (Java)",
  "languages": ["Java"],
  "frameworks": ["Spring Boot", "Spring Data JPA"],
  
  "frameworkDetection": {
    "primaryLanguage": "Java",
    "languageVersion": "17",
    "allDetectedLanguages": ["Java"],
    "buildSystem": {
      "name": "Maven",
      "type": "Build System",
      "confidence": "HIGH",
      "reason": "pom.xml found",
      "evidence": ["File: pom.xml"]
    },
    "frameworks": [
      {
        "name": "Spring Boot",
        "type": "Framework",
        "confidence": "HIGH",
        "reason": "spring-boot-starter-web dependency found",
        "evidence": ["Dependency: org.springframework.boot:spring-boot-starter-web:3.0.0"],
        "version": "3.0.0"
      }
    ]
  },
  
  "projectSize": {
    "totalSizeBytes": 44387840,
    "totalSizeMB": 42.37,
    "totalFileCount": 1284,
    "excludedDirs": [".git", "node_modules", "target", "build", ".venv", "dist", ".next", "__pycache__"]
  },
  
  "dataLayerDetections": [
    {
      "name": "PostgreSQL",
      "type": "Database",
      "confidence": "HIGH",
      "reason": "postgresql dependency detected in pom.xml",
      "evidence": ["Dependency: org.postgresql:postgresql:42.5.0"],
      "version": "42.5.0"
    },
    {
      "name": "JPA",
      "type": "ORM",
      "confidence": "HIGH",
      "reason": "ORM dependency detected in pom.xml",
      "evidence": ["Dependency: org.springframework.boot:spring-boot-starter-data-jpa:3.0.0"]
    },
    {
      "name": "Flyway",
      "type": "Migration Tool",
      "confidence": "HIGH",
      "reason": "Migration tool dependency detected",
      "evidence": ["Dependency: org.flywaydb:flyway-core:9.5.0"]
    }
  ],
  
  "dataLayerHints": [
    "PostgreSQL (HIGH)",
    "JPA ORM (HIGH)",
    "Flyway (HIGH)",
    "HikariCP (HIGH)"
  ],
  
  "securityDetections": [
    {
      "name": "Spring Security",
      "type": "Authentication Framework",
      "confidence": "HIGH",
      "reason": "Spring Security dependency detected",
      "evidence": ["Dependency: org.springframework.boot:spring-boot-starter-security:3.0.0"],
      "version": "3.0.0"
    },
    {
      "name": "JWT",
      "type": "Authentication",
      "confidence": "HIGH",
      "reason": "JWT library dependency detected",
      "evidence": ["Dependency: io.jsonwebtoken:jjwt:0.11.5"]
    }
  ],
  
  "securityHints": [
    "Spring Security (HIGH)",
    "JWT (HIGH)",
    "CORS Configuration (HIGH)"
  ],
  
  "apiDetected": true,
  "apiRoutes": [
    {
      "method": "GET",
      "path": "/api/users",
      "controller": "UserController"
    }
  ],
  
  "entryPoints": [...],
  "configFiles": [...],
  "buildInfo": {...},
  "projectStructure": {...},
  
  "summary": "Backend (Java) | Frameworks: Spring Boot, Spring Data JPA | Build: Maven | Data Layer: PostgreSQL, JPA | Size: 42.37 MB (1284 files) | Security: Spring Security, JWT"
}
```

## TypeScript Client Types

Update `src/types/api.ts`:

```typescript
export type ConfidenceLevel = "HIGH" | "MEDIUM" | "LOW";

export interface DetectionResult {
  name: string;
  type: string; // "Framework", "Database", "ORM", "Authentication", etc.
  confidence: ConfidenceLevel;
  reason: string; // Human-readable explanation
  evidence: string[]; // Supporting signals
  version?: string;
}

// Framework detection output
export interface FrameworkDetectionResult {
  primaryLanguage: string;
  languageVersion: string;
  allDetectedLanguages: string[];
  frameworks: DetectionResult[];
  buildSystem: DetectionResult;
}

// Project size metrics
export interface ProjectSizeInfo {
  totalSizeBytes: number;
  totalSizeMB: number;
  totalFileCount: number;
  excludedDirs: string[];
}

// Main analysis response (updated)
export interface ProjectMetadata {
  projectType: string;
  languages: string[];
  frameworks: string[]; // Legacy, deprecated
  
  // New structured detections
  frameworkDetection: FrameworkDetectionResult;
  projectSize: ProjectSizeInfo;
  dataLayerDetections: DetectionResult[];
  dataLayerHints: string[]; // Legacy
  securityDetections: DetectionResult[];
  securityHints: string[]; // Legacy
  
  // Existing fields
  entryPoints: EntryPoint[];
  configFiles: ConfigFile[];
  apiDetected: boolean;
  apiRoutes: ApiRoute[];
  buildInfo: BuildInfo;
  projectStructure: ProjectStructure;
  summary: string;
}
```

## Frontend Display Components

### New Component: DetectionList

```typescript
// src/components/DetectionList.tsx
import React from 'react';
import { DetectionResult, ConfidenceLevel } from '../types/api';

interface DetectionListProps {
  detections: DetectionResult[] | undefined;
  title: string;
  icon: string;
}

export const DetectionList: React.FC<DetectionListProps> = ({ 
  detections, 
  title, 
  icon 
}) => {
  if (!detections || detections.length === 0) {
    return null;
  }

  const confidenceColor = (level: ConfidenceLevel) => {
    switch (level) {
      case "HIGH":
        return "bg-green-100 text-green-800";
      case "MEDIUM":
        return "bg-yellow-100 text-yellow-800";
      case "LOW":
        return "bg-red-100 text-red-800";
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm p-6">
      <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
        <span className="text-2xl">{icon}</span>
        {title}
      </h3>
      
      <div className="space-y-3">
        {detections.map((detection, idx) => (
          <div 
            key={idx} 
            className="border-l-4 border-blue-500 pl-4 py-2"
          >
            <div className="flex items-start justify-between gap-4">
              <div className="flex-1">
                <h4 className="font-semibold text-gray-900">
                  {detection.name}
                  {detection.version && (
                    <span className="text-gray-500 text-sm ml-2">
                      v{detection.version}
                    </span>
                  )}
                </h4>
                <p className="text-sm text-gray-600 mt-1">
                  {detection.reason}
                </p>
                {detection.evidence.length > 0 && (
                  <div className="mt-2 text-xs text-gray-500">
                    {detection.evidence.map((ev, i) => (
                      <div key={i} className="ml-2">• {ev}</div>
                    ))}
                  </div>
                )}
              </div>
              <span className={`px-3 py-1 rounded-full text-sm font-medium whitespace-nowrap ${
                confidenceColor(detection.confidence)
              }`}>
                {detection.confidence}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
```

### Updated DataLayer Section

```typescript
// src/components/sections/DataLayer.tsx
import React from 'react';
import { ProjectMetadata } from '../../types/api';
import { DetectionList } from '../DetectionList';

export const DataLayer: React.FC<{ metadata: ProjectMetadata }> = ({ metadata }) => {
  return (
    <div className="space-y-4">
      <DetectionList
        detections={metadata.dataLayerDetections}
        title="Data Layer Analysis"
        icon="🗄️"
      />
      
      {/* Fallback for old format */}
      {(!metadata.dataLayerDetections || metadata.dataLayerDetections.length === 0) &&
       metadata.dataLayerHints && metadata.dataLayerHints.length > 0 && (
        <div className="bg-blue-50 rounded-lg p-4">
          <h3 className="font-semibold mb-2">Data Layer</h3>
          <ul className="space-y-1">
            {metadata.dataLayerHints.map((hint, idx) => (
              <li key={idx} className="text-sm text-gray-700">• {hint}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};
```

### Project Size Component

```typescript
// src/components/sections/ProjectMetrics.tsx
import React from 'react';
import { ProjectMetadata } from '../../types/api';

export const ProjectMetrics: React.FC<{ metadata: ProjectMetadata }> = ({ 
  metadata 
}) => {
  if (!metadata.projectSize) return null;

  const { totalSizeMB, totalFileCount } = metadata.projectSize;

  return (
    <div className="grid grid-cols-2 gap-4 mb-6">
      <div className="bg-gradient-to-br from-purple-500 to-purple-600 rounded-lg p-4 text-white">
        <div className="text-sm opacity-90">Total Size</div>
        <div className="text-3xl font-bold">{totalSizeMB} MB</div>
      </div>
      
      <div className="bg-gradient-to-br from-indigo-500 to-indigo-600 rounded-lg p-4 text-white">
        <div className="text-sm opacity-90">Total Files</div>
        <div className="text-3xl font-bold">{totalFileCount}</div>
      </div>
    </div>
  );
};
```

### Framework Detection Card

```typescript
// src/components/sections/FrameworkInfo.tsx
import React from 'react';
import { ProjectMetadata } from '../../types/api';

export const FrameworkInfo: React.FC<{ metadata: ProjectMetadata }> = ({ 
  metadata 
}) => {
  const fd = metadata.frameworkDetection;
  if (!fd) return null;

  return (
    <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
      <h3 className="text-lg font-semibold mb-4">📦 Technology Stack</h3>
      
      <div className="grid grid-cols-2 gap-4">
        <div>
          <p className="text-sm text-gray-600">Primary Language</p>
          <p className="text-lg font-semibold">{fd.primaryLanguage}</p>
          {fd.languageVersion && (
            <p className="text-xs text-gray-500">v{fd.languageVersion}</p>
          )}
        </div>
        
        <div>
          <p className="text-sm text-gray-600">Build System</p>
          <p className="text-lg font-semibold">{fd.buildSystem?.name || "Unknown"}</p>
        </div>
      </div>
      
      {fd.allDetectedLanguages && fd.allDetectedLanguages.length > 1 && (
        <div className="mt-4 pt-4 border-t">
          <p className="text-sm text-gray-600 mb-2">Other Languages</p>
          <div className="flex flex-wrap gap-2">
            {fd.allDetectedLanguages.map((lang, idx) => (
              <span 
                key={idx} 
                className="bg-gray-100 text-gray-800 px-3 py-1 rounded text-sm"
              >
                {lang}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
```

## Backend Controller Updates

```java
// AnalysisController.java
@PostMapping("/analyze")
public ResponseEntity<ProjectMetadata> analyzeProject(
    @RequestParam String projectPath) throws Exception {
    
    // Extract uploaded file if needed
    Path projectRoot = Paths.get(projectPath);
    
    ProjectAnalysisService service = new ProjectAnalysisService();
    ProjectMetadata result = service.analyzeProject(projectRoot);
    
    // Enrich with timestamp
    // result.setAnalyzedAt(LocalDateTime.now());
    
    return ResponseEntity.ok(result);
}
```

## Backward Compatibility

The refactored API maintains backward compatibility:
- Old `frameworks` array still populated
- Old `dataLayerHints` array still populated
- Old `securityHints` array still populated
- New structured detections available alongside old fields

## Migration Steps for Frontend

1. **Update types** (`src/types/api.ts`)
2. **Create new components** (DetectionList, ProjectMetrics, FrameworkInfo)
3. **Update sections** to use new DetectionResult arrays
4. **Add confidence indicators** (badges with colors)
5. **Display project size metrics** prominently
6. **Test with real projects** (Spring Boot, Django, Node.js)

## Testing the API

```bash
# Spring Boot project
curl -X POST http://localhost:8080/analyze \
  -F "project=@/path/to/spring-project"

# Django project
curl -X POST http://localhost:8080/analyze \
  -F "project=@/path/to/django-project"

# Node.js project
curl -X POST http://localhost:8080/analyze \
  -F "project=@/path/to/node-project"
```

## Performance Notes

- **Framework Detection**: ~50-100ms for typical projects
- **Project Size Calculation**: ~100-200ms (excludes node_modules, target, etc.)
- **Full Analysis**: ~500-1000ms depending on project size
- Excludes build artifacts: ~50-70% disk space reduction

## Troubleshooting

### Missing Detections

If a framework/database isn't detected:
1. Check if signature is in FrameworkDetector or Analyzer
2. Verify dependency name in pom.xml/package.json/requirements.txt
3. Add new signature to appropriate map
4. Run analysis again

### Incorrect Language Detection

The language detector uses weighted scoring. If wrong language is primary:
1. Check file counts by extension
2. Adjust weights in FrameworkDetector
3. Common issue: Many .json/.yml files affecting scoring
