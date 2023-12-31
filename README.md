# CROSS Dev Tools

**Description**: A comprehensive solution to monitor and visualize the health status of your applications. The system consists of a backend Spring Boot application to perform health checks and an Angular-based frontend to display the results.

## Table of Contents

- [Structure](#structure)
- [Technical Details](#technical-details)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)
- [Contact Information](#contact-information)

## Structure

The repository is structured into two main directories:

- `backend/`: Contains the Spring Boot application responsible for performing health checks.
- `frontend/`: Holds the Angular application for the user interface.

## Technical Details

- **Backend**:
    - Technology: Spring Boot
    - [Detailed Backend README](./backend/README.md)

- **Frontend**:
    - Technology: Angular
    - [Detailed Frontend README](./frontend/README.md)

## Prerequisites

- Java 17 (For the backend)
- Maven (For the backend)
- Node.js (For the frontend)
- npm (For the frontend)
- Docker (Optional)

## Getting Started

1. **Clone the Repository**

    ```bash
    git clone https://github.com/danieldeusing/crossng-devtools.git
    ```

2. **Set Up the Backend**

   Follow the instructions in the [Backend README](./backend/README.md).

3. **Set Up the Frontend**

   Follow the instructions in the [Frontend README](./frontend/README.md).

4. **Run Both Applications**

   Start both the backend and frontend applications as described in their respective READMEs.

   Visit `http://localhost:4200` in your browser to see the frontend application, which communicates with the 
   backend at `https://localhost:8443`.

## Deployment with Docker

The prod branch of this repository is integrated with a GitHub Action that automates the deployment process. Upon every push to the prod branch, the action is triggered to build a Docker image of the system and push it to Docker Hub under the name regorianer/crossng-devtools.

### Using the Docker Image

For ease of deployment, a docker-compose.yml file is provided at the root of the repository. This Compose file is configured to pull the regorianer/crossng-devtools image from Docker Hub and run it.

This generally uses the local docker image, but if you replace 

```txt
 image: crossng-devtools
```

with 

```txt
 image: regorianer/crossng-devtools:latest
```

it will take use of the image deployed in dockerhub. You can then start the application with the image from dockerhub

```bash
 docker-compose up -d
```

Based on the configuration your application should be served at https://localhost:8443 and the API at 
https://localhost:8443/api/

## Contributing

Contributions, issues, and feature requests are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License

This project is [MIT](LICENSE.md) licensed.

## Contact Information

- **Daniel Deusing** - mail@danieldeusing.de
