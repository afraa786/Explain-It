export interface AnalysisResult {
  projectMetadata?: ProjectMetadata;
  entryPoints?: EntryPoint[];
  configFiles?: ConfigFile[];
  apiRoutes?: ApiRoute[];
  buildInfo?: BuildInfo;
  projectStructure?: ProjectStructure;
  // Also handle flat structure from backend
  projectType?: string;
  languages?: string[];
  frameworks?: string[];
  detectedLanguages?: string[];
  detectedFrameworks?: string[];
  summary?: string;
}

export interface ProjectMetadata {
  detectedLanguages?: string[];
  detectedFrameworks?: string[];
  projectType?: string;
  summary?: string;
}

export interface EntryPoint {
  filePath?: string;
  className?: string;
  methodName?: string;
}

export interface ConfigFile {
  filePath?: string;
  fileType?: string;
  content?: string;
}

export interface ApiRoute {
  method?: string;
  path?: string;
  controller?: string;
  handler?: string;
}

export interface BuildInfo {
  buildTool?: string;
  javaVersion?: string;
  springBootVersion?: string;
  dependencies?: string[];
}

export interface ProjectStructure {
  rootPath?: string;
  directories?: string[];
  fileCount?: number;
  totalSize?: number;
}
