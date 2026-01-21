/* =========================
   ROOT API RESPONSE
========================= */

export interface AnalysisResult {
  projectMetadata?: ProjectMetadata;

  entryPoints?: EntryPoint[];
  configFiles?: ConfigFile[];
  apiRoutes?: ApiRoute[];
  buildInfo?: BuildInfo;
  projectStructure?: ProjectStructure;

  // Backward compatibility (flat responses)
  projectType?: string;
  languages?: string[];
  frameworks?: string[];
  detectedLanguages?: string[];
  detectedFrameworks?: string[];
  summary?: string;
}

/* =========================
   PROJECT METADATA
========================= */

export interface ProjectMetadata {
  projectRootName?: string;

  projectType?: string;
  languages?: string[];
  frameworks?: string[];

  detectedLanguages?: string[];
  detectedFrameworks?: string[];

  entryPoints?: EntryPoint[];
  configFiles?: ConfigFile[];

  apiDetected?: boolean;
  apiRoutes?: ApiRoute[];

  /* ✅ DATA LAYER */
  dataLayerHints?: string[];
  dataLayerDetections?: DetectionResult[];

  /* ✅ SECURITY */
  securityHints?: string[];
  securityDetections?: DetectionResult[];

  buildInfo?: BuildInfo;
  projectStructure?: ProjectStructure;

  frameworkDetection?: FrameworkDetectionResult;
  projectSize?: ProjectSizeInfo;

  summary?: string;
}

/* =========================
   ENTRY POINTS
========================= */

export interface EntryPoint {
  filePath?: string;
  className?: string;
  methodName?: string;
}

/* =========================
   CONFIG FILES
========================= */

export interface ConfigFile {
  filePath?: string;
  fileType?: string;
  content?: string;
}

/* =========================
   API ROUTES
========================= */

export interface ApiRoute {
  method?: string;
  path?: string;
  controller?: string;
  handler?: string;
}

/* =========================
   BUILD INFO
========================= */

export interface BuildInfo {
  buildTool?: string;
  javaVersion?: string;
  springBootVersion?: string;
  nodeVersion?: string;
  pythonVersion?: string;
  dependencies?: string[];
}

/* =========================
   PROJECT STRUCTURE
========================= */

export interface ProjectStructure {
  rootPath?: string;
  directories?: string[];
  fileCount?: number;
  totalSize?: number;
}

/* =========================
   DETECTION RESULTS
========================= */

export interface DetectionResult {
  type?: string;
  confidence?: number;
  description?: string;
  sourceFile?: string;
}

/* =========================
   FRAMEWORK DETECTION
========================= */

export interface FrameworkDetectionResult {
  framework?: string;
  confidence?: number;
  evidence?: string[];
}

/* =========================
   PROJECT SIZE INFO
========================= */

export interface ProjectSizeInfo {
  fileCount?: number;
  totalLines?: number;
  totalSizeKb?: number;
}
