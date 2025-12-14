import requests
import os
import sys


URL = "https://thispersondoesnotexist.com"
UPLOAD_DIR = "/var/www/faces"
os.makedirs(UPLOAD_DIR, exist_ok=True)

if len(sys.argv) < 2:
    print("No student num provided")
    sys.exit(1)

num = sys.argv[1]
r = requests.get(URL, headers={"User-Agent": "Mozilla/5.0"})
with open(f"{UPLOAD_DIR}/{num}.jpg", "wb") as f:
    f.write(r.content)
print(f"Saved {num}.jpg")