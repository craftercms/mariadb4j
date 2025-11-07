1. Run a docker with correct MariaDB version:

```Dockerfile
docker run wodby/mariadb:11.4
```

2. Copy binary files from `/usr/bin/` to `DBs/mariaDB4j-db-alpine64-11.4.3/src/main/resources/ch/vorburger/mariadb4j/mariadb-11.4.3/alpine/bin`

3. Copy share folder from `/usr/share/mysql/` to `DBs/mariaDB4j-db-alpine64-11.4.3/src/main/resources/ch/vorburger/mariadb4j/mariadb-11.4.3/alpine/share/mysql`

4. Copy `/usr/bin/mariadb-install-db` to `DBs/mariaDB4j-db-alpine64-11.4.3/src/main/resources/ch/vorburger/mariadb4j/mariadb-11.4.3/alpine/scripts` and create `mysql_install_db` from the copied `mariadb-install-db`
