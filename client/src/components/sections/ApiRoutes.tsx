import Section from './Section';

interface ApiRoutesProps {
  routes: string[];
}

export default function ApiRoutes({ routes }: ApiRoutesProps) {
  return (
    <Section title="API Routes">
      <div className="bg-[#0f0f0f] rounded p-4 font-mono text-sm overflow-x-auto">
        {routes.length > 0 ? (
          <ul className="space-y-1">
            {routes.map((route, index) => (
              <li key={index} className="text-[#e5e5e5]">
                {route}
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-[#a3a3a3]">No API routes detected</p>
        )}
      </div>
    </Section>
  );
}
