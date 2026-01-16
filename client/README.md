# ExplainIt

A clean, professional frontend for analyzing backend projects. Upload a ZIP file of your backend codebase and receive a structured analysis of its architecture, frameworks, API routes, and more.

## Features

- Clean, dark-themed developer interface
- Drag & drop ZIP file upload
- Structured display of project analysis
- Collapsible raw JSON viewer
- Fully typed with TypeScript
- Responsive design

## Tech Stack

- React 18 with TypeScript
- Vite
- Tailwind CSS
- Lucide React (icons)

## Setup Instructions

### Prerequisites

- Node.js 18+ installed
- npm or yarn package manager

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd explainit
```

2. Install dependencies:
```bash
npm install
```

3. Configure the API endpoint:

Create a `.env` file in the project root (or modify the existing one):
```bash
VITE_API_URL=http://localhost:8080
```

Replace `http://localhost:8080` with your actual backend API URL.

### Development

Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### Build

Build for production:
```bash
npm run build
```

The production-ready files will be in the `dist/` directory.

### Preview Production Build

Preview the production build locally:
```bash
npm run preview
```

## API Integration

The frontend expects a backend API with the following endpoint:

**POST** `/api/explain/analyze`

- Content-Type: `multipart/form-data`
- Field name: `file` (ZIP file)

### Expected Response Format

```json
{
  "projectType": "Spring Boot REST API",
  "languages": ["Java"],
  "frameworks": ["Spring Boot", "Spring Web"],
  "buildTool": "Maven",
  "javaVersion": "17",
  "springBootVersion": "3.2.0",
  "entryPoints": [
    {
      "file": "src/main/java/com/example/Application.java",
      "className": "Application",
      "method": "main"
    }
  ],
  "apiRoutes": [
    "GET /api/users",
    "POST /api/users",
    "GET /api/users/{id}"
  ],
  "dataLayer": {
    "orm": "JPA/Hibernate",
    "databaseHints": ["PostgreSQL"]
  },
  "security": ["Spring Security"],
  "projectStructure": {
    "sourceDirectories": ["src/main/java"],
    "resourceDirectories": ["src/main/resources"],
    "testDirectories": ["src/test/java"],
    "classCount": 42
  }
}
```

## Project Structure

```
src/
├── components/
│   ├── Header.tsx              # App header
│   ├── UploadCard.tsx          # File upload interface
│   ├── ResultsDisplay.tsx      # Main results container
│   └── sections/               # Result section components
│       ├── Section.tsx         # Reusable section wrapper
│       ├── ProjectOverview.tsx
│       ├── BuildInfo.tsx
│       ├── EntryPoints.tsx
│       ├── ApiRoutes.tsx
│       ├── DataLayer.tsx
│       ├── Security.tsx
│       ├── ProjectStructure.tsx
│       └── RawJson.tsx
├── types/
│   └── api.ts                  # TypeScript type definitions
├── utils/
│   └── api.ts                  # API integration
├── App.tsx                     # Main application component
├── main.tsx                    # Application entry point
└── index.css                   # Global styles
```

## Design Principles

- **Minimalist**: Clean interface focused on content
- **Dark Theme**: Easy on the eyes for extended use
- **Developer-First**: Monospace fonts for technical data
- **Accessible**: Keyboard navigation and semantic HTML
- **No Distractions**: No animations, gradients, or gimmicks

## Color Palette

- Background: `#0f0f0f`
- Surface Cards: `#1a1a1a`
- Borders: `#2a2a2a`
- Text Primary: `#e5e5e5`
- Text Secondary: `#a3a3a3`
- Accent: `#3b82f6`

## License

MIT
