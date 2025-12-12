#!/bin/bash

# Usage: ./copy-from-container.sh <container_id>

set -e

CONTAINER=$1

TARGET_VERSION="11.8.5"
SRC_DIR="/usr/bin/"
TARGET_ROOT="./src/main/resources/ch/vorburger/mariadb4j/mariadb-${TARGET_VERSION}/alpine"

DEST_DIR="${TARGET_ROOT}/bin"


FILES=(mariadb mariadb-check mariadb-dump mariadb-install-db mariadb-secure-installation mariadb-upgrade mariadbd my_print_defaults resolveip)

for FILE in "${FILES[@]}"; do
  echo "Copying ${FILE} from container ${CONTAINER}..."
  docker cp "${CONTAINER}:${SRC_DIR}/${FILE}" "${DEST_DIR}/"
done

SRC_DIR="/usr/share/mysql"
DEST_DIR="${TARGET_ROOT}/share/"

echo "Copying ${SRC_DIR} from container ${CONTAINER}..."
docker cp "${CONTAINER}:${SRC_DIR}" "${DEST_DIR}/"

SRC_DIR="/usr/bin/mariadb-install-db"
DEST_DIR="${TARGET_ROOT}/scripts"

echo "Copying ${SRC_DIR} from container ${CONTAINER}..."
docker cp "${CONTAINER}:${SRC_DIR}" "${DEST_DIR}/"

cp "${DEST_DIR}/mariadb-install-db" "${DEST_DIR}/mysql_install_db"
