package ru.netology.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQL {
    private static final QueryRunner runner = new QueryRunner();

    private SQL() {
    }

    @SneakyThrows
    private static Connection getConnect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusResponse {
        private String status;
    }

    public static String checkStatus() {
        var statusSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var connect = getConnect()) {
                var statusCheck = runner.query(connect, statusSQL, new BeanHandler<>(StatusResponse.class));
                if (statusCheck != null)
                    return statusCheck.getStatus();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String checkStatusCredit() {
        var statusSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (var connect = getConnect()) {
            var statusCheck = runner.query(connect, statusSQL, new BeanHandler<>(StatusResponse.class));
            return statusCheck.getStatus();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static void cleanDataBase() {
        var connection = getConnect();
        runner.execute(connection, "DELETE FROM payment_entity");
        runner.execute(connection, "DELETE FROM credit_request_entity");
        runner.execute(connection, "DELETE FROM order_entity");
    }
}
