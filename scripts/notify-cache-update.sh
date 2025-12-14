#!/bin/sh
# Simple notification script called from Postgres container
# It calls the cache-loader controller endpoint to inform the app
# that the people table changed. The script is robust to network
# errors and exits silently (so DB trigger scripts won't fail).

set -e

URL="http://cache-loader:8080/api/v1/people/cache/refresh"

echo "[notify-cache-update] Calling $URL"

# Try a few times with short delays in case the app is still starting
for i in 1 2 3; do
  if curl -s -X POST "$URL" >/dev/null 2>&1; then
    echo "[notify-cache-update] Notification sent"
    exit 0
  else
    echo "[notify-cache-update] Attempt $i failed, retrying..."
    sleep 1
  fi
done

echo "[notify-cache-update] All attempts failed, exiting with success to avoid DB errors"
exit 0
