import Section from './Section';

interface BuildInfoProps {
  buildTool: string;
  javaVersion: string;
  springBootVersion: string;
}

export default function BuildInfo({
  buildTool,
  javaVersion,
  springBootVersion,
}: BuildInfoProps) {
  return (
    <Section title="Build Info">
      <div className="space-y-4">
        <div>
          <span className="text-[#a3a3a3] text-sm">Build Tool:</span>
          <p className="text-[#e5e5e5] font-mono">{buildTool}</p>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Java Version:</span>
          <p className="text-[#e5e5e5] font-mono">{javaVersion}</p>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Spring Boot Version:</span>
          <p className="text-[#e5e5e5] font-mono">{springBootVersion}</p>
        </div>
      </div>
    </Section>
  );
}
