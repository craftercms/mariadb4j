#!/bin/bash

JEMALLOC_VERSION=5.3.0
TARGET_DIR="$LAUNCH_DIR/mariaDB4j-db-mac64-10.4.31.2/src/main/resources/ch/vorburger/mariadb4j/mariadb-10.4.31.2/osx/"

cecho () {
	if [ "$2" == "info" ] ; then
		COLOR="96m";
	elif [ "$2" == "strong" ] ; then
		COLOR="94m";
	elif [ "$2" == "success" ] ; then
		COLOR="92m";
	elif [ "$2" == "warning" ] ; then
		COLOR="93m";
	elif [ "$2" == "error" ] ; then
		COLOR="91m";
	else #default color
		COLOR="0m";
	fi

	STARTCOLOR="\e[$COLOR";
	ENDCOLOR="\e[0m";

	printf "$STARTCOLOR%b$ENDCOLOR" "$1"
}

# This script is used to build MariaDB on OSX
function preFlightCheck() {
	if ! type -p brew > /dev/null 2>&1; then
		cecho "brew command not found, please install 'brew', aborting.\n" "error"
		exit 1
	fi

	if ! type -p cmake > /dev/null 2>&1; then
		cecho "cmake command not found, will attempt to install.\n" "warning"
		brew install cmake
	fi

	if ! brew list jemalloc > /dev/null 2>&1; then
		cecho "jemalloc command not found, will attempt to install.\n" "warning"
		brew install jemalloc
		JEMALLOC_VERSION=$(brew list --version jemalloc | cut -f 2 -d ' ')
	fi

	if ! type -p wget > /dev/null 2>&1; then
		cecho "wget command not found, will attempt to install.\n" "warning"
		brew install wget
	fi
}

function downloadArtifact() {
	url=$1
	artifact=$2
	source=$3
	destination=$4
	wget $1
	tar xf $artifact
	mv $source $destination
}

function buildOpenSSL() {
	pushd .
	cd ~/dev/openssl/
	./Configure darwin64-x86_64-cc --prefix=/Users/osx/dev/openssl no-shared CFLAGS=" -isysroot /Users/$USER/dev/MacOSX10.12.sdk" LDFLAGS=" -isysroot /Users/$USER/dev/MacOSX10.12.sdk}"
	make
	make install
	popd
}

function buildMariaDB() {
	pushd .
	mkdir -p ~/dev/mariadb-build
	cd ~/dev/mariadb-build
	# When building MariaDB 10.4.31 for Mac, we must patch/fix the code per https://jira.mariadb.org/browse/MDEV-27579
	cp "$LAUNCH_DIR/mysql-10.4.31.cc" ~/dev/mariadb-source/client/mysql.cc
	cmake ../mariadb-source -DBUILD_CONFIG=mysql_release -DCMAKE_INSTALL_PREFIX=~/dev/mariadb -DOPENSSL_INCLUDE_DIR=/Users/$USER/dev/openssl/include -DOPENSSL_LIBRARIES=/Users/$USER/dev/openssl/lib/libssl.a -DCRYPTO_LIBRARY=/Users/$USER/dev/openssl/lib/libcrypto.a -DOPENSSL_ROOT_DIR=/Users/$USER/dev/openssl -DWITH_SSL=/Users/$USER/dev/openssl -DCMAKE_C_FLAGS="-Wno-deprecated-declarations" -DCMAKE_OSX_SYSROOT=/Users/$USER/dev/MacOSX10.12.sdk -DCMAKE_OSX_DEPLOYMENT_TARGET=10.12 -DWITHOUT_TOKUDB=1 -DWITH_SSL=yes -DDEFAULT_CHARSET=UTF8 -DDEFAULT_COLLATION=utf8_general_ci -DCOMPILATION_COMMENT=CrafterCms -DWITH_PCRE=bundled -DWITH_READLINE=on  -DWITH_JEMALLOC=/usr/local/Cellar/jemalloc/$JEMALLOC_VERSION/include
	make
	make install
	popd
}
function copyMariaDBArtifacts() {
    # Copy the artifacts from MariaDB build to DBs/...
    pushd .
    cd ~/dev/mariadb
    cp -r scripts "$TARGET_DIR/"
    cp -r shared "$TARGET_DIR/"
    cp bin/my_print_results "$TARGET_DIR/bin"
    cp bin/mysql "$TARGET_DIR/bin"
    cp bin/mysql_install_db "$TARGET_DIR/bin"
    cp bin/mysql_secure_installation "$TARGET_DIR/bin"
    cp bin/mysql_upgrade "$TARGET_DIR/bin"
    cp bin/mysqlcheck "$TARGET_DIR/bin"
    cp bin/mysqld "$TARGET_DIR/bin"
    cp bin/mysqldump "$TARGET_DIR/bin" 
    cp bin/resolveip "$TARGET_DIR/bin" 
    popd
}

function main() {
	cd ~
	mkdir -p dev
	cd dev
	downloadArtifact https://github.com/phracker/MacOSX-SDKs/releases/download/11.3/MacOSX10.12.sdk.tar.xz MacOSX10.12.sdk.tar.xz MacOSX10.12.sdk MacOSX10.12.sdk
	downloadArtifact https://www.openssl.org/source/openssl-1.1.1v.tar.gz openssl-1.1.1v.tar.gz openssl-1.1.1v openssl
	downloadArtifact https://mirror.its.dal.ca/mariadb//mariadb-10.4.31/source/mariadb-10.4.31.tar.gz mariadb-10.4.31.tar.gz mariadb-10.4.31 mariadb-source

	buildOpenSSL
	buildMariaDB
	copyMariaDBArtifacts
}

LAUNCH_DIR=`pwd`

pushd .
preFlightCheck
main "$@"
popd
