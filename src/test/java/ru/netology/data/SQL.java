package ru.netology.data;


import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import java.sql.DriverManager;



public class SQL {
    private static final QueryRunner runner = new QueryRunner();

    private SQL() {
    }

    static String url = System.getProperty("db.url");
    static String user = System.getProperty("db.user");
    static String password = System.getProperty("db.password");

    @SneakyThrows
    public static String checkStatus() {
        var statusSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        var connect = DriverManager.getConnection(url, user, password);
        var statusCheck = runner.query(connect, statusSQL, new ScalarHandler<String>());
        return statusCheck;
    }

    @SneakyThrows
    public static String checkStatusCredit() {
        var statusSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        var connect = DriverManager.getConnection(url, user, password);
        var statusCheck = runner.query(connect, statusSQL, new ScalarHandler<String>());
        return statusCheck;
    }

    @SneakyThrows
    public static void cleanDataBase() {
        var connection = DriverManager.getConnection(url, user, password);
        runner.execute(connection, "DELETE FROM payment_entity");
        runner.execute(connection, "DELETE FROM credit_request_entity");
        runner.execute(connection, "DELETE FROM order_entity");
    }
}
