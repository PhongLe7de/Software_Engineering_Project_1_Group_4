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
