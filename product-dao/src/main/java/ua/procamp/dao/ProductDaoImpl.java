package ua.procamp.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import ua.procamp.dao.Mappers.ProductMapper;
import ua.procamp.exception.DaoOperationException;
import ua.procamp.model.Product;

public class ProductDaoImpl implements ProductDao {

  private DataSource dataSource;
  private ProductMapper productMapper;
  private static final String SAVE_SQL = "INSERT INTO products(name,producer,price,expiration_date) VALUES(?,?,?,?);";
  private static final String FIND_ALL_SQL = "SELECT * FROM products";

  //Do I need compare all object? Or just use id ?
  private static final String FIND_BY_ID_SQL = "SELECT * FROM products WHERE id = ?;";
  private static final String REMOVE_SQL = "DELETE FROM products WHERE id = ?;";

  //MB we can do better. We need remember if we change one column;
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
    try (PreparedStatement statement = dataSource.getConnection()
        .prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
      productMapper.fulfillStatementWithoutCreationTime(statement, product);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          product.setId(generatedKeys.getLong(1));
        } else {
          throw new DaoOperationException("Error saving product: " + product);
        }
      }
    } catch (SQLException e) {
      throw new DaoOperationException("Error saving product: " + product, e);
    }
  }

  @Override
  public List<Product> findAll() {
    ArrayList<Product> products = new ArrayList<>();
    try (Statement statement = dataSource.getConnection().createStatement()) {
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

  //Optional ???
  @Override
  public Product findOne(Long id) {
    verifyId(id);
    Product product;
    try (PreparedStatement statement = dataSource.getConnection()
        .prepareStatement(FIND_BY_ID_SQL)) {
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();

      //Or we can just check on null after mapping
      if (resultSet.next()) {
        product = productMapper.getProduct(resultSet);
      } else {
        //Mb need more specific Exception
        throw new DaoOperationException(String.format("Product with id = %s does not exist", id));
      }
    } catch (SQLException e) {
      throw new DaoOperationException(String.format("Product with id = %s does not exist", id), e);
    }
    return product;
  }

  @Override
  public void update(Product product) {
    Long id = product.getId();
    verifyId(id);
    try (PreparedStatement statement = dataSource.getConnection()
        .prepareStatement(UPDATE_SQL)) {
      productMapper.fulfillStatementWithoutCreationTime(statement, product);
      // Need remember number of fields
      statement.setLong(5, id);
      int numberOfUpdated = statement.executeUpdate();
      if (numberOfUpdated != 1) {
        throw new DaoOperationException("Nothing was updated");
      }
    } catch (SQLException e) {
      throw new DaoOperationException(String.format("Product id cannot be %s", id), e);
    }
  }


  @Override
  public void remove(Product product) {
    Long id = product.getId();
    verifyId(id);
    try (PreparedStatement statement = dataSource.getConnection()
        .prepareStatement(REMOVE_SQL)) {
      // Need remember number of fields
      statement.setLong(1, id);
      int numberOfUpdated = statement.executeUpdate();
      if (numberOfUpdated != 1) {
        throw new DaoOperationException(String.format("Product id cannot be %s", id));
      }
    } catch (SQLException e) {
      throw new DaoOperationException(
          String.format("Product with id = %s does not exist", id), e);
    }
  }

  //Mb change to Custom ViolationIdException
  private void verifyId(Long id) throws DaoOperationException {
    if (id == null) {
      throw new DaoOperationException("Product id cannot be null");
    }
    if (id <= 0) {
      throw new DaoOperationException(String.format("Product with id = %s does not exist", id));
    }
  }
}
