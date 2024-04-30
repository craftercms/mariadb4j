1. Create a symbolic link /Users/osx to point to your user's home folder
2. Make sure you have all required libs: ``brew install cmake jemalloc traildb/judy/judy openssl boost gnutls``
3. Download the MacOS SDK for the minimum supported version from https://github.com/phracker/MacOSX-SDKs/releases
4. Unzip MacOS SDK (e.g. `~/dev/MacOSX13.3.sdk`) (this must match your OS)
5. Download OpenSSL source from  https://www.openssl.org/source (latest version openssl-1.1.1v.tar.gz)
6. Unzip openssl (e.g. `~/dev/openssl-1.1.1c`)
7. Create new folder for openssl build (e.g. `~/dev/openssl`)
8. Inside openssl source folder run following command:
    `./Configure darwin64-x86_64-cc --prefix=/Users/osx/dev/openssl no-shared CFLAGS=" -isysroot /Users/osx/dev/MacOSX13.3.sdk" LDFLAGS=" -isysroot /Users/osx/dev/MacOSX13.3.sdk}"`
9. In the openssl source folder run `make`
10. In the openssl source folder run `make install`
11. Download the source of the mariadb version to build
12. Unzip source
13. Create folder `mariadb-build` as sibling of mariadb source
14. `cd mariadb-build`
15. run following command (before running check if paths are correct)
`cmake ../mariadb-source
-DBUILD_CONFIG=mysql_release -DCMAKE_INSTALL_PREFIX=~/dev/mariadb
-DOPENSSL_INCLUDE_DIR=/Users/osx/dev/openssl/include -DOPENSSL_LIBRARIES=/Users/osx/dev/openssl/lib/libssl.a
-DCRYPTO_LIBRARY=/Users/osx/dev/openssl/lib/libcrypto.a  -DOPENSSL_ROOT_DIR=/Users/osx/dev/openssl
-DWITH_SSL=/Users/osx/dev/openssl -DCMAKE_C_FLAGS="-Wno-deprecated-declarations"
-DCMAKE_OSX_SYSROOT=/Users/osx/dev/MacOSX13.3.sdk -DCMAKE_OSX_DEPLOYMENT_TARGET=13.3 -DWITHOUT_TOKUDB=1
-DWITH_SSL=yes -DDEFAULT_CHARSET=UTF8 -DDEFAULT_COLLATION=utf8_general_ci -DCOMPILATION_COMMENT=CrafterCms
-DWITH_PCRE=bundled -DWITH_READLINE=ON -DWITH_JEMALLOC=/usr/local/Cellar/jemalloc/5.2.0/include`
16. run `make`
17. run `make install`
18. the binaries should be in this folder `~/dev/mariadb`
