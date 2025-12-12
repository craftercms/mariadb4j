1. Run a docker with correct MariaDB version:
    ```shell
    docker run wodby/mariadb:11.8
    ```

2. Rename `DBs/mariaDB4j-db-alpine64-11.8.5` directory with to new version
3. Run `copy-from-container.sh`
   ```shell
   ./copy-from-container.sh <container_id>
   ```
