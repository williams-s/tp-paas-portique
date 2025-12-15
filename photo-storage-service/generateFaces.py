import requests
import time
import os


URL = "https://thispersondoesnotexist.com"
UPLOAD_DIR = "/var/www/faces"
os.makedirs(UPLOAD_DIR, exist_ok=True)

UNKNOWN_FACE_PATH = "./Unknown.jpg"

with open(UNKNOWN_FACE_PATH, "rb") as f:
    with open(f"{UPLOAD_DIR}/Unknown.jpg", "wb") as g:
        g.write(f.read())

currentStudentNum = 20001
STUDENTS = 119
for i in range(STUDENTS):
    r = requests.get(URL, headers={"User-Agent": "Mozilla/5.0"})
    with open(f"{UPLOAD_DIR}/{currentStudentNum}.jpg", "wb") as f:
        f.write(r.content)
    print(f"Saved {currentStudentNum}.jpg")
    currentStudentNum += 1
    time.sleep(1)
