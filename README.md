# 🔥 Realshyt Fashion Backend API

E-commerce REST API for Realshyt streetwear brand built with Spring Boot.

## Tech Stack
- Java 24
- Spring Boot 3.5.7
- Spring Data JPA
- H2 Database (Development)
- MySQL (Production)
- Spring Security
- Gradle

## Getting Started

```bash
# Clone repository
git clone https://github.com/valtheen/realshyt-fashion-backend.git
cd realshyt-fashion-backend

# Run the application
./gradlew bootRun

# Or on Windows
gradlew.bat bootRun
```

## API Endpoints

### Products
- `GET /api/products` - Get all active products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/search?keyword={keyword}` - Search products
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}` - Update order
- `DELETE /api/orders/{id}` - Delete order

## Database

### H2 Console (Development)
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:realshyt_fashion`
- **Username**: `sa`
- **Password**: (leave empty)

### Sample Data
The application automatically initializes with sample products:
- REAL SHYT Hoodie ($89.99)
- Graffiti Tee ($45.99)
- Urban Joggers ($79.99)
- Street Cap ($35.99)
- Smoke Jacket ($129.99)
- Drip Shorts ($59.99)

## Configuration

### Development (H2)
```properties
spring.datasource.url=jdbc:h2:mem:realshyt_fashion
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

### Production (MySQL)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/realshyt_fashion
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## Security
- API endpoints are publicly accessible for development
- H2 console is accessible without authentication
- Security configuration can be customized in `SecurityConfig.java`

## Testing the API

```bash
# Get all products
curl http://localhost:8080/api/products

# Get product by ID
curl http://localhost:8080/api/products/1

# Search products
curl "http://localhost:8080/api/products/search?keyword=hoodie"
```

## Project Structure
```
src/
├── main/
│   ├── java/com/realshyt/fashion/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/                    # Test classes
```

## Development

### Prerequisites
- Java 24 or higher
- Gradle 8.14.3

### Building
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License.

## Contact
- **Developer**: Nauval Fatheen
- **GitHub**: [@valtheen](https://github.com/valtheen)
- **Project**: [Realshyt Fashion Backend](https://github.com/valtheen/realshyt-fashion-backend)
