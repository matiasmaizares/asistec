#!/bin/bash
set -e

echo "Iniciando backend en http://localhost:8080 ..."
cd backend
mvn spring-boot:run &
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
