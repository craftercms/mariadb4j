Refer: https://git.alpinelinux.org/aports/tree/main/mariadb/APKBUILD?h=3.11-stable

**Note**: The folder name has the postfix `10.4.20` to make it compatible with the Linux version but the actual binary is `10.4.22` since there is no pre-build `10.4.20` for Alpine.

1. Create a Dockerfile and add the MariaDB with correct version:

```Dockerfile
FROM eclipse-temurin:17-jdk-alpine

ENV MARIADB_VERSION 10.4.22
RUN echo 'http://dl-cdn.alpinelinux.org/alpine/v3.11/main' >> /etc/apk/repositories; \
    apk update; \
    apk upgrade; \
    apk add mariadb=$MARIADB_VERSION-r0 mariadb-common=$MARIADB_VERSION-r0 mariadb-client=$MARIADB_VERSION-r0 mariadb-server-utils=$MARIADB_VERSION-r0;
...
```

2. Copy binary files from `/usr/bin/` to `DBs/mariaDB4j-db-alpine64-10.4.20/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.20/alpine/bin`

3. Copy share folder from `/usr/share/mariadb/` to `DBs/mariaDB4j-db-alpine64-10.4.20/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.20/alpine/share`

4. Copy `/usr/bin/mysql_install_db` to `DBs/mariaDB4j-db-mac64-10.4.20/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.20/osx/scripts` and create `mariadb-install-db` from the copied `mysql_install_db`