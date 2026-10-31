#!/usr/bin/env bash
# Prueba de humo post-despliegue: comprueba que la API desplegada responde y que su flujo básico funciona.
# Uso: scripts/smoke-test.sh http://localhost:8081
set -euo pipefail

BASE_URL="${1:-http://localhost:8081}"
BODY=$(mktemp)
FALLOS=0

esperar_salud() {
  for _ in $(seq 1 60); do
    if curl -sf "$BASE_URL/actuator/health/readiness" >/dev/null; then
      echo "✔ La API está lista en $BASE_URL"
      return 0
    fi
    sleep 3
  done
  echo "✘ La API no respondió en $BASE_URL"
  exit 1
}

# comprobar <descripción> <código esperado> <método> <ruta> [json]
comprobar() {
  local descripcion=$1 esperado=$2 metodo=$3 ruta=$4 datos=${5:-}
  local codigo
  if [ -n "$datos" ]; then
    codigo=$(curl -s -o "$BODY" -w '%{http_code}' -X "$metodo" "$BASE_URL$ruta" -H 'Content-Type: application/json' -d "$datos")
  else
    codigo=$(curl -s -o "$BODY" -w '%{http_code}' -X "$metodo" "$BASE_URL$ruta")
  fi
  if [ "$codigo" = "$esperado" ]; then
    echo "✔ $descripcion ($codigo)"
  else
    echo "✘ $descripcion: se esperaba $esperado y llegó $codigo"
    cat "$BODY"; echo
    FALLOS=$((FALLOS + 1))
  fi
}

esperar_salud

comprobar "Crear producto" 201 POST /api/productos '{"nombre":"Prueba de humo","stock":1,"precio":100}'
ID=$(sed -E 's/.*"id":([0-9]+).*/\1/' "$BODY")
comprobar "Consultar producto creado" 200 GET "/api/productos/$ID"
comprobar "Producto inexistente devuelve 404" 404 GET /api/productos/999999
comprobar "Datos inválidos devuelven 400" 400 POST /api/productos '{"nombre":"","precio":0}'
comprobar "Eliminar producto" 204 DELETE "/api/productos/$ID"

comprobar "Crear usuario" 201 POST /api/usuarios "{\"nombre\":\"Smoke\",\"correo\":\"smoke$(date +%s)@test.cl\",\"contrasena\":\"segura123\"}"
if grep -q contrasena "$BODY"; then
  echo "✘ La respuesta expone la contraseña"
  FALLOS=$((FALLOS + 1))
else
  echo "✔ La contraseña no se expone en la respuesta"
fi

rm -f "$BODY"
if [ "$FALLOS" -gt 0 ]; then
  echo "Prueba de humo fallida: $FALLOS comprobaciones con error"
  exit 1
fi
echo "Prueba de humo superada"
