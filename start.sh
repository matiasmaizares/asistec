#!/bin/bash
set -e

if [ ! -f .env ]; then
  echo "No existe .env — copiando .env.example (revisá los valores antes de usar en serio)."
  cp .env.example .env
fi

echo "Levantando Postgres y Redis (docker compose)..."
docker compose up -d postgres redis

set -a; source .env; set +a

echo "Iniciando backend (perfil dev) en http://localhost:8080 ..."
cd backend
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo "Instalando dependencias del frontend..."
cd frontend
npm install --silent

echo "Iniciando frontend en http://localhost:4200 ..."
npm start &
FRONTEND_PID=$!
cd ..

echo ""
echo "Backend:  http://localhost:8080"
echo "Frontend: http://localhost:4200"
echo ""
echo "Ctrl+C para detener ambos procesos"

trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0" INT TERM
wait
