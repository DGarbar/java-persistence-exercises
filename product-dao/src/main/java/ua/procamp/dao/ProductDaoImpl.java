package ua.procamp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import ua.procamp.dao.Mappers.ProductMapper;
import ua.procamp.exception.DaoOperationException;
import ua.procamp.model.Product;

public class ProductDaoImpl implements ProductDao {

  private DataSource dataSource;
  private ProductMapper productMapper;
  private static final String SAVE_SQL = "INSERT INTO products(name,producer,price,expiration_date) VALUES(?,?,?,?);";
  private static final String FIND_ALL_SQL = "SELECT * FROM products";
  private static final String FIND_BY_ID_SQL = "SELECT * FROM products WHERE id = ?;";
  private static final String REMOVE_SQL = "DELETE FROM products WHERE id = ?;";
  private static final String UPDATE_SQL =
      "UPDATE products SET "
          + " name = ?,"
          + " producer = ?,"
          + " price = ?,"
          + " expiration_date = ?"
          + " WHERE id = ?;";


  public ProductDaoImpl(DataSource dataSource) {
    this.dataSource = dataSource;
    productMapper = new ProductMapper();
  }

  @Override
  public void save(Product product) {
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection
          .prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
      productMapper.fulfillStatementWithoutCreationTime(statement, product);
      statement.executeUpdate();
      Long id = getGeneratedId(statement)
          .orElseThrow(() -> new DaoOperationException("Nothing was saved product= " + product));
      product.setId(id);
    } catch (SQLException e) {
      throw new DaoOperationException("Error saving product: " + product, e);
    }
  }

  private Optional<Long> getGeneratedId(Statement statement) throws SQLException {
    ResultSet generatedKeys = statement.getGeneratedKeys();
    if (generatedKeys.next()) {
      return Optional.of(generatedKeys.getLong(1));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<Product> findAll() {
    ArrayList<Product> products = new ArrayList<>();
    try (Connection connection = dataSource.getConnection()) {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
      while (resultSet.next()) {
        Product product = productMapper.getProduct(resultSet);
        products.add(product);
      }
    } catch (SQLException e) {
      throw new DaoOperationException("Error findAll products", e);
    }
    return products;
  }

  @Override
  public Product findOne(Long id) {
    verifyId(id);
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL);
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();
      return getProduct(resultSet)
          .orElseThrow(() -> new DaoOperationException(
              String.format("Product with id = %s does not exist", id)));
    } catch (SQLException e) {
      throw new DaoOperationException(String.format("Error finding one with id = %d", id), e);
    }
  }

  private Optional<Product> getProduct(ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      return Optional.of(productMapper.getProduct(resultSet));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void update(Product product) {
    Long id = product.getId();
    verifyId(id);
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(UPDATE_SQL);
      productMapper.fulfillStatementWithoutCreationTime(statement, product);
      statement.setLong(5, id);
      executeUpdate(statement);
    } catch (SQLException e) {
      throw new DaoOperationException(String.format("Product id cannot be %s", id), e);
    }
  }


  @Override
  public void remove(Product product) {
    Long id = product.getId();
    verifyId(id);
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(REMOVE_SQL);
      statement.setLong(1, id);
      executeUpdate(statement);
    } catch (SQLException e) {
      throw new DaoOperationException(
          String.format("Product with id = %s does not exist", id), e);
    }
  }

  //Smelly code
  private void executeUpdate(PreparedStatement statement) throws SQLException {
    int numberOfUpdated = statement.executeUpdate();
    if (numberOfUpdated != 1) {
      throw new DaoOperationException("Nothing was updated");
    }
  }

  private void verifyId(Long id) throws DaoOperationException {
    if (id == null) {
      throw new DaoOperationException("Product id cannot be null");
    }
    if (id <= 0) {
      throw new DaoOperationException(String.format("Product with id = %s does not exist", id));
    }
  }
}
