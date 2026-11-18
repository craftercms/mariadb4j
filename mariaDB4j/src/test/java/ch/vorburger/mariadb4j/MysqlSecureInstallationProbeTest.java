/*
 * #%L
 * MariaDB4j
 * %%
 * Copyright (C) 2012 - 2023 Michael Vorburger
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
package ch.vorburger.mariadb4j;

import ch.vorburger.exec.ManagedProcess;
import ch.vorburger.exec.ManagedProcessException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.net.ssl.SSLException;

/**
 * Connector/J 4 defaults {@code sslMode} to {@code verify-full}. The secure-install probe treats
 * any {@link SQLException} as "already secured", so a TLS failure must not be what the probe sees.
 */
public class MysqlSecureInstallationProbeTest {

    private static final String ROOT_PASSWORD = "installed-root";

    @Test
    public void freshDataDirProbeConnectsAndSecureInstallRuns() throws Exception {
        File tempDir = new File("target/mysql-secure-probe-fresh");
        FileUtils.deleteDirectory(tempDir);
        ObservingDB db = null;
        try {
            db = newDatabase(tempDir);
            db.prepareDirectories();
            db.unpackEmbeddedDb();
            db.install();
            db.start();

            String probeUrl = DB.unsecuredRootProbeUrl(db.getConfiguration().getPort());
            try (Connection connection = DriverManager.getConnection(probeUrl, "root", "")) {
                Assert.assertFalse(connection.isClosed());
            }
            db.stop();

            db.runMysqlSecureInstallationScript();
            Assert.assertTrue(
                    "secure installation did not run after the empty-root probe connected",
                    db.secureInstallPrepared);

            db.start();
            assertAuthenticationFailure(DB.unsecuredRootProbeUrl(db.getConfiguration().getPort()));
            try (Connection connection =
                    DriverManager.getConnection(
                            db.getConfiguration().getURL("mysql"), "root", ROOT_PASSWORD)) {
                Assert.assertFalse(connection.isClosed());
            }
        } finally {
            stopAndDelete(db, tempDir);
        }
    }

    @Test
    public void securedDataDirProbeIsAuthenticationErrorAndSkipsScript() throws Exception {
        File tempDir = new File("target/mysql-secure-probe-secured");
        FileUtils.deleteDirectory(tempDir);
        ObservingDB db = null;
        try {
            db = newDatabase(tempDir);
            db.prepareDirectories();
            db.unpackEmbeddedDb();
            db.install();
            db.runMysqlSecureInstallationScript();
            Assert.assertTrue(db.secureInstallPrepared);

            db.secureInstallPrepared = false;
            db.start();
            String probeUrl = DB.unsecuredRootProbeUrl(db.getConfiguration().getPort());
            assertAuthenticationFailure(probeUrl);
            db.stop();

            db.runMysqlSecureInstallationScript();
            Assert.assertFalse(
                    "secure installation ran after the probe should have seen an authentication error",
                    db.secureInstallPrepared);

            db.start();
            assertAuthenticationFailure(DB.unsecuredRootProbeUrl(db.getConfiguration().getPort()));
            try (Connection connection =
                    DriverManager.getConnection(
                            db.getConfiguration().getURL("mysql"), "root", ROOT_PASSWORD)) {
                Assert.assertFalse(connection.isClosed());
            }
        } finally {
            stopAndDelete(db, tempDir);
        }
    }

    private static ObservingDB newDatabase(File tempDir) throws ManagedProcessException {
        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(0);
        config.setSecurityDisabled(false);
        config.setDefaultRootPassword(ROOT_PASSWORD);
        config.setDataDir(new File(tempDir, "data"));
        config.setBaseDir(new File(tempDir, "base"));
        config.setTmpDir(new File(tempDir, "tmp").getAbsolutePath());
        config.setDeletingTemporaryBaseAndDataDirsOnShutdown(false);
        return new ObservingDB(config.build());
    }

    private static void assertAuthenticationFailure(String probeUrl) throws SQLException {
        try (Connection connection = DriverManager.getConnection(probeUrl, "root", "")) {
            Assert.fail("probe connected as root with an empty password: " + connection);
        } catch (SQLException failure) {
            boolean authenticationError = false;
            for (Throwable current = failure; current != null; current = current.getCause()) {
                if (current instanceof SSLException) {
                    Assert.fail("probe failed with a TLS error: " + failure);
                }
                if (current instanceof SQLException sqlException
                        && (sqlException.getErrorCode() == 1045
                                || "28000".equals(sqlException.getSQLState()))) {
                    authenticationError = true;
                }
            }
            if (!authenticationError) {
                Assert.fail(
                        "probe failed with SQLState "
                                + failure.getSQLState()
                                + " error code "
                                + failure.getErrorCode()
                                + ": "
                                + failure.getMessage());
            }
        }
    }

    private static void stopAndDelete(DB db, File tempDir)
            throws IOException, ManagedProcessException {
        if (db != null) {
            db.stop();
        }
        FileUtils.deleteDirectory(tempDir);
    }

    private static final class ObservingDB extends DB {

        private boolean secureInstallPrepared;

        private ObservingDB(DBConfiguration config) {
            super(config);
        }

        @Override
        ManagedProcess secureInstallPreparation() throws ManagedProcessException, IOException {
            secureInstallPrepared = true;
            return super.secureInstallPreparation();
        }
    }
}
