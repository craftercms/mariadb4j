/*
 * #%L
 * MariaDB4j
 * %%
 * Copyright (C) 2012 - 2014 Michael Vorburger
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package ch.vorburger.mariadb4j.springframework;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.MariaDB4jService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * MariaDB4jService extension suitable for use in Spring Framework-based applications.
 *
 * <p>Other than implementing {@link Lifecycle} to get auto-started, this class allows applications
 * using it to programmatically set a default port/socket/data- &amp; base directory in their {@link
 * Configuration}, yet let end-users override those via the Spring Values mariaDB4j.port,
 * mariaDB4j.socket, mariaDB4j.dataDir, mariaDB4j.baseDir; so e.g. via -D or (if using Spring Boot)
 * main() command line arguments.
 *
 * <p>This Service is intentionally NOT annotated as a {@link Service} {@link Component}, because we
 * don't want it to be auto-started by component scan without explicit declaration in
 * a @Configuration (or XML)
 *
 * @author Michael Vorburger
 */
public class MariaDB4jSpringService extends MariaDB4jService implements Lifecycle {

    public static final String PORT = "mariaDB4j.port";
    public static final String SOCKET = "mariaDB4j.socket";
    public static final String DATA_DIR = "mariaDB4j.dataDir";
    public static final String TMP_DIR = "mariaDB4j.tmpDir";
    public static final String BASE_DIR = "mariaDB4j.baseDir";
    public static final String LIB_DIR = "mariaDB4j.libDir";
    public static final String UNPACK = "mariaDB4j.unpack";
    public static final String ARGS = "mariaDB4j.args";
    public static final String OS_USER = "mariaDB4j.osUser";
    public static final String DEFAULT_CHARSET = "mariaDB4j.defaultCharset";
    public static final String SECURITY_DISABLED = "mariaDB4j.securityDisabled";
    public static final String DEFAULT_ROOT_PASSWORD = "mariaDB4j.defaultRootPassword";
    public static final String DRIVER_CLASS_NAME = "mariaDB4j.driverClassName";

    protected ManagedProcessException lastException;

    @Value("${$DBVersion:mariadb-11.4.3}")
    public void setDBVersion(String version) {
        getConfiguration().setDatabaseVersion(version);
    }

    @Value("${" + PORT + ":-1}")
    public void setDefaultPort(int port) {
        if (port != -1) getConfiguration().setPort(port);
    }

    @Value("${" + SOCKET + ":NA}")
    public void setDefaultSocket(String socket) {
        if (!"NA".equals(socket)) getConfiguration().setSocket(socket);
    }

    @Value("${" + DATA_DIR + ":NA}")
    public void setDefaultDataDir(String dataDir) {
        if (!"NA".equals(dataDir)) getConfiguration().setDataDir(new File(dataDir));
    }

    @Value("${" + TMP_DIR + ":NA}")
    public void setDefaultTmpDir(String tmpDir) {
        if (!"NA".equals(tmpDir)) getConfiguration().setTmpDir(tmpDir);
    }

    @Value("${" + BASE_DIR + ":NA}")
    public void setDefaultBaseDir(String baseDir) {
        if (!"NA".equals(baseDir)) getConfiguration().setBaseDir(new File(baseDir));
    }

    @Value("${" + LIB_DIR + ":NA}")
    public void setDefaultLibDir(String libDir) {
        if (!"NA".equals(libDir)) getConfiguration().setLibDir(new File(libDir));
    }

    @Value("${" + UNPACK + ":#{null}}")
    public void setDefaultIsUnpackingFromClasspath(Boolean unpack) {
        if (unpack != null) getConfiguration().setUnpackingFromClasspath(unpack);
    }

    @Value("${" + ARGS + ":#{null}}")
    public void setArgs(List<String> args) {
        if (args != null && args.size() > 0) {
            for (String arg : args) {
                getConfiguration().addArg(arg);
            }
        }
    }

    @Value("${" + OS_USER + ":NA}")
    public void setDefaultOsUser(String osUser) {
        if (!"NA".equals(osUser)) getConfiguration().addArg("--user=" + osUser);
    }

    @Value("${" + DEFAULT_CHARSET + ":NA}")
    public void setDefaultCharacterSet(String charset) {
        if (!Objects.equals(charset, "NA")) getConfiguration().setDefaultCharacterSet(charset);
    }

    @Value("${" + SECURITY_DISABLED + ":#{null}}")
    public void setSecurityDisabled(Boolean securityDisabled) {
        if (securityDisabled != null) {
            getConfiguration().setSecurityDisabled(securityDisabled);
        }
    }

    @Value("${" + DEFAULT_ROOT_PASSWORD + ":#NA}")
    public void setDefaultRootPassword(String defaultRootPassword) {
        if (!"NA".equals(defaultRootPassword)) {
            getConfiguration().setDefaultRootPassword(defaultRootPassword);
        }
    }

    @Value("${" + DRIVER_CLASS_NAME + ":#NA}")
    public void setDriverClassName(String driverClassName) {
        if (!"NA".equals(driverClassName)) {
            getConfiguration().setDriverClassName(driverClassName);
        }
    }

    @Override
    public void start() { // no throws ManagedProcessException
        try {
            super.start();
        } catch (ManagedProcessException e) {
            lastException = e;
            throw new IllegalStateException("MariaDB4jSpringService start() failed", e);
        }
    }

    @Override
    public void stop() { // no throws ManagedProcessException
        try {
            super.stop();
        } catch (ManagedProcessException e) {
            lastException = e;
            throw new IllegalStateException("MariaDB4jSpringService stop() failed", e);
        }
    }

    public ManagedProcessException getLastException() {
        return lastException;
    }
}
