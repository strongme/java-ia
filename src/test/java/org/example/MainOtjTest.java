package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.PreparedDbRule;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class MainOtjTest {

  @Rule
  public PreparedDbRule db;

  private ExecutorService executor = Executors.newSingleThreadExecutor();

  private void setupDb() {
    if (db == null) {
      db = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation("db/migration"));
    }
  }

  @Test
  public void test_select() throws SQLException {
    setupDb();
    DataSource dataSource = db.getTestDatabase();
    QueryRunner queryRunner = new QueryRunner(dataSource);
    String sql = "select id, name, description from shiweiyang.course where 1=1";
    List<Map<String, Object>> result = queryRunner.query(sql, MainOtjTest::resultSetHandler);
    Assert.assertEquals(result.size(), 0);
  }

  @Test
  public void test_insert() throws SQLException {
    setupDb();
    DataSource dataSource = db.getTestDatabase();
    QueryRunner queryRunner = new QueryRunner(dataSource);
    generateData(dataSource, 10);
    String sql = "select id, name, description from shiweiyang.course where 1=1";
    List<Map<String, Object>> result = queryRunner.query(sql, MainOtjTest::resultSetHandler);
    System.out.println(result);
    Assert.assertEquals(result.size(), 10);
  }

  @Test
  public void test_future() throws InterruptedException, ExecutionException {
    Future<Integer> future = calculate(10);
    while (!future.isDone()) {
      System.out.println("Calculating...");
      Thread.sleep(500);
    }
    Integer result = future.get();
    System.out.println("result: "+result);
  }

  public Future<Integer> calculate(Integer input) {
    return executor.submit(() -> {
      Thread.sleep(1000);
      return input * input;
    });
  }

  private void generateData(DataSource dataSource, int count) throws SQLException {
    QueryRunner queryRunner = new QueryRunner(dataSource);
    String sql = "insert into shiweiyang.course(name, description) values (?,?)";
    ScalarHandler<Integer> handler = new ScalarHandler<>();
    for (int i = 0; i < count; i++) {
      int id = queryRunner.insert(sql, handler, "jack", "description");
      Assert.assertEquals(i+1, id);
    }
  }

  private static List<Map<String, Object>> resultSetHandler(ResultSet resultSet) throws SQLException {
    List<Map<String, Object>> result = Lists.newArrayList();
    while (resultSet.next()) {
      Map<String, Object> course = Maps.newHashMap();
      course.put("id",resultSet.getInt("id"));
      course.put("name",resultSet.getString("name"));
      course.put("description",resultSet.getString("description"));
      result.add(course);
    }
    return result;
  }

}
