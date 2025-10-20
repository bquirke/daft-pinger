# Daft Pinger - Docker Setup

This directory contains Docker configuration for running Daft Pinger as a containerized application.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- `.env` file configured in project root (copy from `.env.example`)

### Run with Docker Compose
```bash
# From project root
cd docker
docker-compose up -d
```

### Run with Docker directly
```bash
# Build the image
docker build -f docker/Dockerfile -t daft-pinger .

# Run the container
docker run -d \
  --name daft-pinger \
  -e DAFT_SEARCH_URL="your-search-url" \
  -e NOTIFICATION_TOPIC="your-topic" \
  -e JOB_TIMER_MINUTES="5" \
  -v ./data:/app/data \
  -p 8080:8080 \
  daft-pinger
```

## 📁 File Structure

```
docker/
├── Dockerfile              # Multi-stage build with Chrome
├── docker-compose.yml      # Complete orchestration
└── README.md              # This file
```

## 🔧 Environment Variables

Required environment variables (set in `.env` file):
- `DAFT_SEARCH_URL` - Daft.ie search URL with your filters
- `NOTIFICATION_TOPIC` - ntfy.sh topic for notifications
- `JOB_TIMER_MINUTES` - How often to check (default: 5 minutes)

## 🗂️ Data Persistence

The container mounts `../data:/app/data` to persist the `seen-urls.txt` file between container restarts.

## 📊 Monitoring

- **Health checks**: Available at `http://localhost:8080/actuator/health`
- **Logs**: `docker-compose logs -f daft-pinger`
- **Status**: `docker-compose ps`

## 🛠️ Development Commands

```bash
# View logs
docker-compose logs -f

# Restart service
docker-compose restart

# Stop service
docker-compose down

# Rebuild and restart
docker-compose up --build -d

# Shell into container
docker-compose exec daft-pinger bash

# Check resource usage
docker stats daft-pinger
```

## 🐳 Production Deployment

For production, consider:
1. Using Docker secrets for environment variables
2. Setting up proper logging aggregation
3. Adding monitoring and alerting
4. Using a reverse proxy if needed
5. Setting up automated backups of the data volume

## 🔒 Security Notes

- Container runs as non-root user (`appuser`)
- Resource limits configured in docker-compose
- Only necessary ports exposed
- Chrome runs in sandboxed mode with `--no-sandbox`