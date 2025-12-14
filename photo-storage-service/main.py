from fastapi import FastAPI
from fastapi.responses import FileResponse
import os

app = FastAPI()

UPLOAD_DIR = "/var/www/faces"
os.makedirs(UPLOAD_DIR, exist_ok=True)
UNKNOWN_FACE_PATH = os.path.join(UPLOAD_DIR, "Unknown.jpg")

@app.get("/faces/{filename}")
async def get_image(filename: str):
    file_path = os.path.join(UPLOAD_DIR, f"{filename}.jpg")
    if not os.path.exists(file_path):
        if not os.path.exists(UNKNOWN_FACE_PATH):
            return {"error": "image not found"}
        return FileResponse(UNKNOWN_FACE_PATH)
    return FileResponse(file_path)
