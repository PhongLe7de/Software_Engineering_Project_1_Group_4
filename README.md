# Software Engineering Project 1 - Group 4

## Tech Stack

### Backend
- **Spring Boot** - Java framework for production-ready applications
- **Redis** - In-memory data store for real-time drawing event storage
- **PostgreSQL** - Relational database for persistent storage

### Frontend
- **React** - Component-based UI library
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first CSS framework

## Key Features

- Real-time drawing with WebSocket communication
- Event-driven architecture: Redis → PostgreSQL data flow
- Fallback to PostgreSQL when Redis is unavailable
- Collaborative drawing with cursor tracking
- Type-safe frontend with React and TypeScript
- Multi-language support with automatic browser detection

## Localization

The application supports multiple languages with automatic detection and manual selection.

### Supported Languages
- **English** (en)
- **Japanese** (ja) - 日本語
- **Russian** (ru) - Русский
- **Chinese** (zh) - 中文

### Language Selection
Users can change the application language using the **LanguageSelector** component:
1. Click the globe icon in the application header
2. Select preferred language from the dropdown menu
3. The selection is automatically saved to browser localStorage

### Localization Framework
The application uses **react-i18next** with the following features:
- **Automatic detection**: Browser language is detected on first load via `i18next-browser-languagedetector`
- **Persistent preference**: Language selection is stored in localStorage
- **Fallback language**: Defaults to English if selected language is unavailable
- **Translation files**: Located in `frontend/src/i18n/locales/*.json`

## Technology Rationale

### Spring Boot
- Minimal boilerplate and auto-configuration accelerate development
- Built-in WebSocket support for real-time features
- Excellent integration with Redis and PostgreSQL through Spring Data, plus seamless CI/CD with Jenkins

### Redis for Drawing Events
- Sub-millisecond response times critical for real-time drawing
- High throughput for concurrent users
- Efficient Redis Hashes via `@RedisHash` annotation
- Temporary buffer before PostgreSQL persistence

### PostgreSQL
- Reliable production database
- Native JSON support

### React + TypeScript
- Component reusability for drawing tools
- Type safety prevents runtime errors
- Rich ecosystem for canvas/drawing libraries

### Tailwind CSS
- Rapid UI development with utility classes
- Consistent design system
- Optimized bundle size

## Development Setup

### Prerequisites
- Java 17+, Node.js 18+, PostgreSQL 13+, Redis 6+

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Run Unit Tests and Generate Coverage Report
```bash
cd backend
mvn clean test
```
- Open the Coverage Report
```bash
cd backend
open target/site/jacoco/index.html  #MacOs
```
```bash
cd backend
start target\site\jacoco\index.html #Window
```
## 🌍 Internationalization (i18n) and Localization

### Overview
All user-facing text (buttons, labels, tooltips, etc.) is stored in translation files for easy updates and language switching.

### Folder Structure

```bash
frontend/
├── src/
│   ├── i18n/
│   │   ├── index.ts
│   │   ├── locales/
│   │   │   ├── en.json
│   │   │   ├── fi.json
│   │   │   └── vi.json
│   └── components/
│       └── ...
```

### Adding a New Language

1. Create a new folder under `src/i18n/locales/` (e.g., `en` for English).
2. Add a `en.json` file inside it:
   ```json
   {
     "user": {
       "id": "ID",
       "name": "名前"
     }
   }
   ```
3. Register it in the i18n configuration (`src/i18n/index.ts`):
   ```ts
   i18n.init({
     resources: {
       en: { translation: en },
       fi: { translation: fi },
       vi: { translation: vi },
       fr: { translation: fr },
     },
     fallbackLng: "en",
   });
   ```

### Selecting a Language
Add a language selector in your UI:

```tsx
import { useTranslation } from "react-i18next";

const { t } = useTranslation();

    <Button>{t("home.join_board")} </Button>;
```

### Running Localized Frontend

To test localization:

```bash
cd frontend
npm run dev
```