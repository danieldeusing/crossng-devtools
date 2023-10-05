# CROSS App Health Check

**Description**: Simply checks the health status of your applications

## Table of Contents

- [Technical Details](#technical-details)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)
- [Contact Information](#contact-information)

## Technical Details

- **Spring Boot Version**: 3.1.4
- **Other Technologies/Dependencies**:
    - Spring Web

## Prerequisites

- Java 17
- Maven
- Docker (Optional)

## Getting Started

1. **Clone the Repository**

    ```bash
    git clone https://github.com/danieldeusing/crossng-devtools.git
    ```

2. **Navigate to the directory**

    ```bash
    cd crossng-devtools
    ```

3. **Build the application**

    ```bash
    mvn clean install
    ```

4. **Run the application**

    ```bash
    mvn spring-boot:run
    ```

5. Visit `http://localhost:8080` in your browser.

## Running with Docker

1. **Build the Docker image**

    ```bash
    docker build -t crossng-devtools .
    ```

2. **Run the Docker container**

    ```bash
    docker run -p 8080:8080 crossng-devtools
    ```

## API Endpoints

- **GET** `/api/health-check/{containerName}`: Checks the health status for the given container name.


## Contributing

Contributions, issues, and feature requests are welcome! See [CONTRIBUTING.md](../CONTRIBUTING.md) for details.

## License

This project is [MIT](LICENSE) licensed.

## Contact Information

- **Daniel Deusing** - mail@danieldeusing.de