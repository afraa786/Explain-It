import Section from './Section';

interface SecurityProps {
  security: string[];
}

export default function Security({ security }: SecurityProps) {
  return (
    <Section title="Security">
      {security.length > 0 ? (
        <ul className="space-y-2">
          {security.map((item, index) => (
            <li key={index} className="text-[#e5e5e5] font-mono text-sm">
              • {item}
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-[#a3a3a3]">No security frameworks detected</p>
      )}
    </Section>
  );
}
