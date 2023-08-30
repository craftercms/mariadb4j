1. Run a docker with correct MariaDB version:

```Dockerfile
docker run wodby/mariadb:10.4
```

2. Copy binary files from `/usr/bin/` to `DBs/mariaDB4j-db-alpine64-10.4.31.1/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.31.1/alpine/bin`

3. Copy share folder from `/usr/share/mysql/` to `DBs/mariaDB4j-db-alpine64-10.4.31.1/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.31.1/alpine/share`

4. Copy `/usr/bin/mysql_install_db` to `DBs/mariaDB4j-db-alpine64-10.4.31.1/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.31.1/alpine/scripts` and create `mariadb-install-db` from the copied `mysql_install_db`