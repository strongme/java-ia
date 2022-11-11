package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.zonky.test.db.postgres.embedded.FlywayPreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class MainZonkyTest {

  @Rule
  public PreparedDbRule db = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation("db/migration"));

  @Test
  public void test_select() throws SQLException {
    DataSource dataSource = db.getTestDatabase();
    QueryRunner queryRunner = new QueryRunner(dataSource);
    String sql = "select id, name, description from shiweiyang.course where 1=1";
    List<Map<String, Object>> result = queryRunner.query(sql, MainZonkyTest::resultSetHandler);
    Assert.assertEquals(result.size(), 0);
  }

  @Test
  public void test_insert() throws SQLException {
    DataSource dataSource = db.getTestDatabase();
    QueryRunner queryRunner = new QueryRunner(dataSource);
    String sql = "insert into shiweiyang.course(name, description) values (?,?)";
    ScalarHandler<Integer> handler = new ScalarHandler<>();
    int id = queryRunner.insert(sql, handler, "jack", "description");
    Assert.assertEquals(1, id);
    sql = "select id, name, description from shiweiyang.course where 1=1";
    List<Map<String, Object>> result = queryRunner.query(sql, MainZonkyTest::resultSetHandler);
    System.out.println(result);
    Assert.assertEquals(result.size(), 1);
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
