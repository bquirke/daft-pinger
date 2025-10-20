# Daft Pinger

A simple web scraper that monitors Daft.ie property listings and sends notifications when new properties match your search criteria.

## What it does

- Scrapes Daft.ie search results using headless Chrome
- Tracks previously seen URLs to avoid duplicate notifications
- Sends notifications via ntfy.sh when new properties are found
- Runs automatically on a configurable schedule

## Setup

1. Copy the environment template:
```bash
cp .env.example .env
```

2. Edit `.env` with your settings:
   - `DAFT_SEARCH_URL`: Your Daft.ie search URL with filters
   - `NOTIFICATION_TOPIC`: Your ntfy.sh topic ID
   - `JOB_TIMER_MINUTES`: How often to check 

## Running the application

### Development mode
```bash
./gradlew bootRun
```

### Local execution
```bash
chmod +x run.sh
./run.sh
```

### Docker (recommended)
```bash
cd docker
docker-compose up -d
```

View logs:
```bash
docker-compose logs -f daft-pinger
```

Stop:
```bash
docker-compose down
```

## How to get your search URL

1. Go to Daft.ie
2. Set up your search filters (location, price, bedrooms, etc.). Obviously filter by Most recent so the pinger isnt over active
3. Copy the full URL from your browser
4. Paste it as `DAFT_SEARCH_URL` in your `.env` file

## Notifications

Sign up at [ntfy.sh](https://ntfy.sh) and create a topic. Subscribe to your topic in their app or web interface to receive property alerts.

## Data persistence

The app keeps track of seen URLs in `data/seen-urls.txt` to avoid sending duplicate notifications. This file persists between runs.

## Requirements

- Java 17+
- Chrome browser (for Docker, this is installed automatically)
- Internet connection

## Configuration

All configuration is done via environment variables in the `.env` file. See `.env.example` for available options.