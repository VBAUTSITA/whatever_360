package com.tracker.web.auditoriared;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/** Local USERTEST fixture only. No password is accepted via arguments, properties or environment. */
public final class OracleIntegrationHarness {
    static final String URL = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
    static final String MARKER = "AUDITORIA_RED_SYNTHETIC_V1_20261027";
    static final String[] OBJECTS = {"TM_CONF_ACTUAL_CELDA_4G", "TM_CONF_ACTUAL_CELDA_5G",
            "PLAN_MAP_SECT_CRIT_4GWEEK", "AUDRED_TA5G_AUX", "VW_PLANI_RECURSOS_5G_TA_DIARIO", "RECURSOS_FORMULARIO"};
    private static Connection connection;

    public static void main(String[] args) throws Exception {
        Set<String> options = new LinkedHashSet<>(Arrays.asList(args));
        for (String option : options) if (!Arrays.asList("inspect", "setup", "verify", "serve", "all", "--password-stdin").contains(option))
            throw new IllegalArgumentException("Opciones: inspect setup verify serve all --password-stdin");
        char[] password;
        if (options.contains("--password-stdin")) {
            // Explicit opt-in for a launcher with a masked prompt and anonymous stdin pipe.
            String line = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)).readLine();
            if (line == null || line.isEmpty()) throw new IllegalArgumentException("Falta credencial en stdin");
            password = line.toCharArray();
        } else {
            Console console = System.console();
            if (console == null) throw new IllegalStateException("Requiere consola para readPassword o launcher con --password-stdin");
            password = console.readPassword("USERTEST en localhost/FREEPDB1 - clave (oculta): ");
        }
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Properties properties = new Properties();
            properties.setProperty("user", "USERTEST");
            properties.setProperty("password", new String(password));
            try { connection = DriverManager.getConnection(URL, properties); }
            finally { properties.clear(); }
        } finally { Arrays.fill(password, '\0'); }
        try {
            assertIdentity(connection);
            inspect(connection);
            boolean all = options.contains("all");
            if (all || options.contains("setup")) setup(connection);
            if (all || options.contains("verify")) OracleIntegrationVerification.verify(connection);
            if (all || options.contains("serve")) {
                OracleBrowserFixture.start(dataSource(connection));
                System.out.println("ORACLE_BROWSER_READY http://127.0.0.1:3299/tracker/auditoria-red/dashboard");
                return; // Tomcat keeps the process alive; one local connection stays in memory.
            }
        } finally {
            if (!(options.contains("all") || options.contains("serve"))) connection.close();
        }
    }

    static DataSource dataSource(Connection c) { return new SingleConnectionDataSource(c, true); }

    static void assertIdentity(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT USER, SYS_CONTEXT('USERENV','CON_NAME') FROM DUAL")) {
            rs.next(); String user = rs.getString(1), pdb = rs.getString(2);
            if (!"USERTEST".equals(user) || !"FREEPDB1".equals(pdb))
                throw new IllegalStateException("Se requiere USERTEST/FREEPDB1; no se ejecuta DDL en otro esquema");
            System.out.println("IDENTITY " + user + " / " + pdb + " / " + c.getMetaData().getDatabaseProductVersion().replace('\n', ' '));
        }
    }

    static void inspect(Connection c) throws SQLException {
        reportQuery(c, "PRIVILEGES", "SELECT PRIVILEGE FROM SESSION_PRIVS WHERE PRIVILEGE IN ('CREATE TABLE','CREATE VIEW','CREATE SEQUENCE','CREATE PROCEDURE') ORDER BY PRIVILEGE");
        reportQuery(c, "USER_OBJECTS", "SELECT OBJECT_NAME, OBJECT_TYPE, STATUS FROM USER_OBJECTS WHERE OBJECT_TYPE IN ('TABLE','VIEW') ORDER BY OBJECT_NAME");
        for (String object : OBJECTS) reportQuery(c, "COLUMNS " + object,
                "SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE FROM USER_TAB_COLUMNS WHERE TABLE_NAME='" + object + "' ORDER BY COLUMN_ID");
    }

    static void reportQuery(Connection c, String label, String sql) throws SQLException {
        System.out.println("QUERY " + label + " | " + sql);
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int n = 0;
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) row.append(i == 1 ? "" : " | ").append(rs.getString(i));
                System.out.println(row); n++;
            }
            System.out.println("ROWS " + n);
        }
    }

    static void setup(Connection c) throws Exception {
        assertIdentity(c);
        requirePrivilege(c, "CREATE TABLE");
        requirePrivilege(c, "CREATE VIEW");
        // Preflight ALL names before the first implicit-commit DDL. Existing user objects are untouched.
        for (String object : OBJECTS) if (exists(c, object)) requireOwned(c, object);
        for (String key : Arrays.asList("AUDRED_4G_PK", "AUDRED_5G_PK", "AUDRED_TA4G_PK", "AUDRED_TA5G_PK", "AUDRED_RECURSOS_PK")) {
            try (PreparedStatement st = c.prepareStatement("SELECT TABLE_NAME FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME=?")) {
                st.setString(1, key);
                try (ResultSet rs = st.executeQuery()) { while (rs.next()) requireOwned(c, rs.getString(1)); }
            }
            if (exists(c, key)) {
                try (PreparedStatement st = c.prepareStatement("SELECT TABLE_NAME FROM USER_INDEXES WHERE INDEX_NAME=?")) {
                    st.setString(1, key);
                    try (ResultSet rs = st.executeQuery()) {
                        if (!rs.next()) throw new IllegalStateException("COLISION: " + key);
                        requireOwned(c, rs.getString(1));
                    }
                }
            }
        }
        String ddl = resource("/oracle/auditoria-red-fixture.sql");
        for (String block : ddl.split("(?m)^-- @object ")) {
            if (block.startsWith("--") || block.trim().isEmpty()) continue;
            int newline = block.indexOf('\n');
            String object = block.substring(0, newline).trim();
            if (!Arrays.asList(OBJECTS).contains(object)) throw new IllegalStateException("Objeto fuera del modulo: " + object);
            if (exists(c, object)) { requireOwned(c, object); continue; }
            String sql = block.substring(newline + 1).trim();
            if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
            try (Statement st = c.createStatement()) {
                st.execute(sql);
                st.execute("COMMENT ON TABLE " + object + " IS '" + MARKER + " - DATOS SINTETICOS; no es DDL de produccion'");
            }
            System.out.println("CREATED " + object);
        }
        validateSchema(c);
        // Seed inserts are idempotent by fixture ID and never update an existing row.
        c.setAutoCommit(false);
        try (Statement st = c.createStatement()) {
            for (String sql : resource("/oracle/auditoria-red-seed.sql").split("(?m)^-- @statement\\s*$")) {
                String clean = sql.trim();
                if (clean.isEmpty() || clean.startsWith("--")) continue;
                if (clean.endsWith(";")) clean = clean.substring(0, clean.length() - 1);
                System.out.println("SEED inserted=" + st.executeUpdate(clean));
            }
            c.commit();
        } catch (Exception e) { c.rollback(); throw e; }
        finally { c.setAutoCommit(true); }
        System.out.println("SETUP_COMPLETE " + MARKER);
        validateSchema(c);
    }

    static void validateSchema(Connection c) throws Exception {
        for (String object : OBJECTS) requireOwned(c, object);
        Properties expected = new Properties();
        try (InputStream in = OracleIntegrationHarness.class.getResourceAsStream("/oracle/schema-columns.properties")) { expected.load(in); }
        for (String key : expected.stringPropertyNames()) {
            String[] parts = key.split("\\.");
            try (PreparedStatement st = c.prepareStatement("SELECT DATA_TYPE FROM USER_TAB_COLUMNS WHERE TABLE_NAME=? AND COLUMN_NAME=?")) {
                st.setString(1, parts[0]); st.setString(2, parts[1]);
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next() || !expected.getProperty(key).equals(rs.getString(1)))
                        throw new IllegalStateException("Esquema inesperado: " + key + " requiere " + expected.getProperty(key));
                }
            }
        }
        for (String object : OBJECTS) {
            int expectedCount = 0;
            for (String key : expected.stringPropertyNames()) if (key.startsWith(object + ".")) expectedCount++;
            try (PreparedStatement st = c.prepareStatement("SELECT COUNT(*) FROM USER_TAB_COLUMNS WHERE TABLE_NAME=?")) {
                st.setString(1, object);
                try (ResultSet rs = st.executeQuery()) {
                    rs.next(); if (rs.getInt(1) != expectedCount) throw new IllegalStateException("Cantidad de columnas inesperada: " + object);
                }
            }
            reportQuery(c,"VERIFIED_COLUMNS "+object,"SELECT COLUMN_NAME,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,NULLABLE FROM USER_TAB_COLUMNS WHERE TABLE_NAME='"+object+"' ORDER BY COLUMN_ID");
        }
        reportQuery(c, "FIXTURE_COUNTS", "SELECT 'TM_CONF_ACTUAL_CELDA_4G', COUNT(*) FROM TM_CONF_ACTUAL_CELDA_4G UNION ALL SELECT 'TM_CONF_ACTUAL_CELDA_5G', COUNT(*) FROM TM_CONF_ACTUAL_CELDA_5G UNION ALL SELECT 'PLAN_MAP_SECT_CRIT_4GWEEK', COUNT(*) FROM PLAN_MAP_SECT_CRIT_4GWEEK UNION ALL SELECT 'AUDRED_TA5G_AUX', COUNT(*) FROM AUDRED_TA5G_AUX UNION ALL SELECT 'VW_PLANI_RECURSOS_5G_TA_DIARIO', COUNT(*) FROM VW_PLANI_RECURSOS_5G_TA_DIARIO UNION ALL SELECT 'RECURSOS_FORMULARIO', COUNT(*) FROM RECURSOS_FORMULARIO");
        reportQuery(c, "FIXTURE_CONSTRAINTS", "SELECT TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE, STATUS FROM USER_CONSTRAINTS WHERE TABLE_NAME IN ('TM_CONF_ACTUAL_CELDA_4G','TM_CONF_ACTUAL_CELDA_5G','PLAN_MAP_SECT_CRIT_4GWEEK','AUDRED_TA5G_AUX','RECURSOS_FORMULARIO') ORDER BY TABLE_NAME,CONSTRAINT_NAME");
        reportQuery(c, "FIXTURE_INDEXES", "SELECT TABLE_NAME, INDEX_NAME, UNIQUENESS, STATUS FROM USER_INDEXES WHERE TABLE_NAME IN ('TM_CONF_ACTUAL_CELDA_4G','TM_CONF_ACTUAL_CELDA_5G','PLAN_MAP_SECT_CRIT_4GWEEK','AUDRED_TA5G_AUX','RECURSOS_FORMULARIO') ORDER BY TABLE_NAME,INDEX_NAME");
    }

    static void requirePrivilege(Connection c, String privilege) throws SQLException {
        try (PreparedStatement st = c.prepareStatement("SELECT COUNT(*) FROM SESSION_PRIVS WHERE PRIVILEGE=?")) {
            st.setString(1, privilege);
            try (ResultSet rs = st.executeQuery()) {
                rs.next(); if (rs.getInt(1) != 1) throw new IllegalStateException("Falta privilegio " + privilege + "; no se eleva ni se ejecuta DDL");
            }
        }
    }

    static boolean exists(Connection c, String name) throws SQLException {
        try (PreparedStatement st = c.prepareStatement("SELECT COUNT(*) FROM USER_OBJECTS WHERE OBJECT_NAME=?")) {
            st.setString(1, name); try (ResultSet rs = st.executeQuery()) { rs.next(); return rs.getInt(1) > 0; }
        }
    }
    static void requireOwned(Connection c, String name) throws SQLException {
        try (PreparedStatement st = c.prepareStatement("SELECT COMMENTS, TABLE_TYPE FROM USER_TAB_COMMENTS WHERE TABLE_NAME=?")) {
            st.setString(1, name); try (ResultSet rs = st.executeQuery()) {
                if (!rs.next() || rs.getString(1) == null || !rs.getString(1).startsWith(MARKER + " - "))
                    throw new IllegalStateException("COLISION: " + name + " existe sin marca propia; no se modifica ni elimina");
                String type = "VW_PLANI_RECURSOS_5G_TA_DIARIO".equals(name) ? "VIEW" : "TABLE";
                if (!type.equals(rs.getString(2))) throw new IllegalStateException("Tipo inesperado para " + name);
            }
        }
    }
    static String resource(String name) throws IOException {
        try (InputStream in = OracleIntegrationHarness.class.getResourceAsStream(name)) {
            if (in == null) throw new FileNotFoundException(name);
            ByteArrayOutputStream out = new ByteArrayOutputStream(); byte[] b = new byte[4096]; int n;
            while ((n = in.read(b)) >= 0) out.write(b, 0, n);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
