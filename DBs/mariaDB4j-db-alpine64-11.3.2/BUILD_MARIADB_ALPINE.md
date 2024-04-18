1. Run a docker with correct MariaDB version:

```Dockerfile
docker run wodby/mariadb:11
```

2. Copy binary files from `/usr/bin/` to `DBs/mariaDB4j-db-alpine64-11.3.2/src/main/resources/ch/vorburger/mariadb4j/mariadb-11.3.2/alpine/bin`

3. Copy share folder from `/usr/share/mysql/` to `DBs/mariaDB4j-db-alpine64-11.3.2/src/main/resources/ch/vorburger/mariadb4j/mariadb-11.3.2/alpine/share`

4. Copy `/usr/bin/mysql_install_db` to `DBs/mariaDB4j-db-alpine64-11.3.2/src/main/resources/ch/vorburger/mariadb4j/mariadb-11.3.2/alpine/scripts` and create `mariadb-install-db` from the copied `mysql_install_db`
