import Section from './Section';

interface ProjectOverviewProps {
  projectType: string;
  languages: string[];
  frameworks: string[];
}

export default function ProjectOverview({
  projectType,
  languages,
  frameworks,
}: ProjectOverviewProps) {
  return (
    <Section title="Project Overview">
      <div className="space-y-4">
        <div>
          <span className="text-[#a3a3a3] text-sm">Project Type:</span>
          <p className="text-[#e5e5e5] font-mono">{projectType}</p>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Languages:</span>
          <p className="text-[#e5e5e5] font-mono">{languages.join(', ')}</p>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Frameworks:</span>
          <p className="text-[#e5e5e5] font-mono">{frameworks.join(', ')}</p>
        </div>
      </div>
    </Section>
  );
}
