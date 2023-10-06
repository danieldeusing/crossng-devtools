# CROSS Dev Tools Frontend

**Description**: A user-friendly interface to visualize the health status of your applications.

## Table of Contents

- [Technical Details](#technical-details)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [UI Components](#ui-components)
- [Contributing](#contributing)
- [License](#license)
- [Contact Information](#contact-information)

## Technical Details

- **Angular Version**: 16
- **Other Technologies/Dependencies**:
    - Angular Material
    - RxJS
    - Angular Material

## Prerequisites

- Node.js
- npm
- Docker (Optional)

## Getting Started

1. **Clone the Repository**

    ```bash
    git clone https://github.com/danieldeusing/crossng-devtools.git
    ```

2. **Navigate to the Angular directory**

    ```bash
    cd crossng-devtools/frontend
    ```

3. **Install dependencies**

    ```bash
    npm install
    ```

4. **Run the application**

    ```bash
    ng serve
    ```

5. Visit `http://localhost:4200` in your browser.

## Running with Docker


1. **Build the Docker image**

    ```bash
    docker build -t crossng-devtools .
    ```

2. **Run the Docker container**

    ```bash
    docker run crossng-devtools
    ```

    or better take the docker-compose
3. 
    ```bash
    docker-compose up -d
    ```

## UI Components

- **Dashboard**: Not implemented yet.
- **Health Details**: Dives deeper into individual application health metrics.
- **TBD**: more to come

## Contributing

Contributions, issues, and feature requests are welcome! See [CONTRIBUTING.md](../CONTRIBUTING.md) for details.

## License

This project is [MIT](../LICENSE.md) licensed.

## Contact Information

- **Daniel Deusing** - mail@danieldeusing.de
