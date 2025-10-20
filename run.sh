#!/bin/bash

# Daft Pinger Application Runner
# This script loads environment variables from .env and runs the application

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🏠 Daft Pinger - Starting Application${NC}"

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  Warning: .env file not found!${NC}"
    echo -e "${YELLOW}   Copy .env.example to .env and fill in your values${NC}"
    echo -e "${YELLOW}   cp .env.example .env${NC}"
    exit 1
fi

# Load environment variables from .env file
echo -e "${GREEN}📋 Loading environment variables from .env...${NC}"
export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)

# Check if JAR file exists
JAR_FILE="build/libs/daft-pinger-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}❌ JAR file not found: $JAR_FILE${NC}"
    echo -e "${YELLOW}   Building application first...${NC}"
    ./gradlew bootJar
fi

# Verify required environment variables
if [ -z "$DAFT_SEARCH_URL" ]; then
    echo -e "${RED}❌ DAFT_SEARCH_URL not set in .env file${NC}"
    exit 1
fi

if [ -z "$NOTIFICATION_TOPIC" ]; then
    echo -e "${RED}❌ NOTIFICATION_TOPIC not set in .env file${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Environment variables loaded successfully${NC}"
echo -e "${GREEN}🚀 Starting Daft Pinger application...${NC}"
echo -e "${BLUE}   Search URL: ${DAFT_SEARCH_URL:0:50}...${NC}"
echo -e "${BLUE}   Notification Topic: $NOTIFICATION_TOPIC${NC}"
echo -e "${BLUE}   Job Timer: ${JOB_TIMER_MINUTES:-5} minutes${NC}"
echo ""

# Create data directory if it doesn't exist
mkdir -p data

# Run the application
java -jar "$JAR_FILE"