package io.quarkus.reactive.mysql.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.quarkus.test.QuarkusUnitTest;
import io.vertx.mysqlclient.MySQLPool;

public class MultipleDataSourcesAndMySQLPoolCreatorsTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application-multiple-datasources-with-erroneous-url.properties")
            .withApplicationRoot((jar) -> jar
                    .addClasses(BeanUsingDefaultDataSource.class)
                    .addClass(BeanUsingHibernateDataSource.class)
                    .addClass(DefaultMySQLPoolCreator.class)
                    .addClass(HibernateMySQLPoolCreator.class));

    @Inject
    BeanUsingDefaultDataSource beanUsingDefaultDataSource;

    @Inject
    BeanUsingHibernateDataSource beanUsingHibernateDataSource;

    @Test
    public void testMultipleDataSources() {
        beanUsingDefaultDataSource.verify()
                .thenCompose(v -> beanUsingHibernateDataSource.verify())
                .toCompletableFuture()
                .join();
    }

    @ApplicationScoped
    static class BeanUsingDefaultDataSource {

        @Inject
        MySQLPool mySQLClient;

        public CompletionStage<Void> verify() {
            CompletableFuture<Void> cf = new CompletableFuture<>();
            mySQLClient.query("SELECT 1").execute(ar -> {
                if (ar.failed()) {
                    cf.completeExceptionally(ar.cause());
                } else {
                    cf.complete(null);
                }
            });
            return cf;
        }
    }

    @ApplicationScoped
    static class BeanUsingHibernateDataSource {

        @Inject
        @ReactiveDataSource("hibernate")
        MySQLPool mySQLClient;

        public CompletionStage<Void> verify() {
            CompletableFuture<Void> cf = new CompletableFuture<>();
            mySQLClient.query("SELECT 1").execute(ar -> {
                if (ar.failed()) {
                    cf.completeExceptionally(ar.cause());
                } else {
                    cf.complete(null);
                }
            });
            return cf;
        }
    }

    @Singleton
    public static class DefaultMySQLPoolCreator implements MySQLPoolCreator {

        @Override
        public MySQLPool create(Input input) {
            assertEquals(12345, input.mySQLConnectOptions().getPort()); // validate that the bean has been called for the proper datasource
            return MySQLPool.pool(input.vertx(), input.mySQLConnectOptions().setHost("localhost").setPort(3308),
                    input.poolOptions());
        }
    }

    @Singleton
    @ReactiveDataSource("hibernate")
    public static class HibernateMySQLPoolCreator implements MySQLPoolCreator {

        @Override
        public MySQLPool create(Input input) {
            assertEquals(55555, input.mySQLConnectOptions().getPort()); // validate that the bean has been called for the proper datasource
            return MySQLPool.pool(input.vertx(), input.mySQLConnectOptions().setHost("localhost").setPort(3308),
                    input.poolOptions());
        }
    }
}
