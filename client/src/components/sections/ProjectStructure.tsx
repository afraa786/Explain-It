import Section from './Section';
import { ProjectStructure as ProjectStructureType } from '../../types/api';

interface ProjectStructureProps {
  structure: ProjectStructureType;
}

export default function ProjectStructure({ structure }: ProjectStructureProps) {
  return (
    <Section title="Project Structure">
      <div className="space-y-4">
        <div>
          <span className="text-[#a3a3a3] text-sm">Source Directories:</span>
          <ul className="mt-2 space-y-1">
            {structure.sourceDirectories.map((dir, index) => (
              <li key={index} className="text-[#e5e5e5] font-mono text-sm">
                {dir}
              </li>
            ))}
          </ul>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Resource Directories:</span>
          <ul className="mt-2 space-y-1">
            {structure.resourceDirectories.map((dir, index) => (
              <li key={index} className="text-[#e5e5e5] font-mono text-sm">
                {dir}
              </li>
            ))}
          </ul>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Test Directories:</span>
          <ul className="mt-2 space-y-1">
            {structure.testDirectories.map((dir, index) => (
              <li key={index} className="text-[#e5e5e5] font-mono text-sm">
                {dir}
              </li>
            ))}
          </ul>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Total Classes:</span>
          <p className="text-[#e5e5e5] font-mono">{structure.classCount}</p>
        </div>
      </div>
    </Section>
  );
}
