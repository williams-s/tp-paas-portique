FROM python:3.11-slim

RUN mkdir -p /var/www/faces

WORKDIR /app

COPY requirements.txt .
COPY . .

# Installer les dépendances
RUN pip install --no-cache-dir -r requirements.txt

# Exposer le port de FastAPI
EXPOSE 8000

# Lancer FastAPI
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
