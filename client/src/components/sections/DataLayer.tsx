import Section from './Section';

interface DataLayerProps {
  orm: string;
  databaseHints: string[];
}

export default function DataLayer({ orm, databaseHints }: DataLayerProps) {
  return (
    <Section title="Data Layer">
      <div className="space-y-4">
        <div>
          <span className="text-[#a3a3a3] text-sm">ORM:</span>
          <p className="text-[#e5e5e5] font-mono">{orm || 'None detected'}</p>
        </div>
        <div>
          <span className="text-[#a3a3a3] text-sm">Database Hints:</span>
          {databaseHints.length > 0 ? (
            <ul className="mt-2 space-y-1">
              {databaseHints.map((hint, index) => (
                <li key={index} className="text-[#e5e5e5] font-mono text-sm">
                  • {hint}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-[#e5e5e5] font-mono">None detected</p>
          )}
        </div>
      </div>
    </Section>
  );
}
