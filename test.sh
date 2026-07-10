#!/bin/bash
set -e

echo "=== Tests backend ==="
cd backend
mvn test
cd ..

echo ""
echo "=== Tests frontend ==="
cd frontend
npx ng test --watch=false --browsers=ChromeHeadless
cd ..

echo ""
echo "Todos los tests pasaron."
