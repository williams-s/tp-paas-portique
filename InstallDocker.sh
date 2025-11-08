#!/bin/bash

echo "Mise à jour du système..."
sudo apt update
sudo apt upgrade -y

echo "Installation des dépendances..."
sudo apt install -y ca-certificates curl gnupg lsb-release

echo "Ajout de la clé GPG de Docker..."
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

echo "Ajout du dépôt Docker..."
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

echo "Mise à jour des sources..."
sudo apt update

echo "Installation de Docker Engine + CLI + Compose..."
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "Docker installé avec succès !"

sudo systemctl enable --now docker

sudo usermod -aG docker $USER

