package ua.procamp.dao.Mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ua.procamp.model.Product;

public class ProductMapper {

  public void fulfillStatementWithCreationTime(PreparedStatement stmt, Product product)
      throws SQLException {
    fulfillStatementWithoutCreationTime(stmt, product);
    stmt.setObject(5, product.getCreationTime());
  }

  public void fulfillStatementWithoutCreationTime(PreparedStatement stmt, Product product)
      throws SQLException {
    stmt.setString(1, product.getName());
    stmt.setString(2, product.getProducer());
    stmt.setBigDecimal(3, product.getPrice());
    stmt.setObject(4, product.getExpirationDate());
  }

  //MB better to use Column name also (but if want to change one column, we need to remember this)
  public Product getProduct(ResultSet resultSet) throws SQLException {
    return Product.builder()
        .id(resultSet.getLong(1))
        .name(resultSet.getString(2))
        .producer(resultSet.getString(3))
        .price(resultSet.getBigDecimal(4))
        .expirationDate(resultSet.getDate(5).toLocalDate())
        .creationTime(resultSet.getTimestamp(6).toLocalDateTime())
        .build();
  }
}
