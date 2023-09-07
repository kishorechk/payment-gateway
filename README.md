# Payment Gateway API

This project provides a payment gateway API that allows merchants to process payments and retrieve details of previously made payments. It also includes a simulated bank component for testing the payment flow.

## Table of Contents

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Running the Application](#running-the-application)
- [Assumptions](#assumptions)
- [Areas for Improvement](#areas-for-improvement)
- [Cloud Deployment Considerations](#cloud-deployment-considerations)
- [License](#license)

## Getting Started

### Prerequisites

- Java 11 or higher
- Gradle
- H2 Database (for local testing)

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/kishorechk/payment-gateway.git
   cd payment-gateway
   ```

2. Build the project:
   ```bash
   gradle build
   ```

3. Run the application:
   ```bash
   gradle bootRun
   ```

4. Access the API documentation at: `http://localhost:8080/swagger-ui.html`

## Assumptions

- The payment gateway primarily focuses on processing and retrieving payments, without handling user authentication or merchant registration.
- The simulated bank component is for testing purposes only and does not represent a real-world bank integration.
- Payments are processed in real-time without any queuing mechanism.

## Areas for Improvement

- **Authentication & Authorization**: Implement OAuth2.0 or JWT-based authentication to ensure secure access.
- **Database**: Migrate from H2 to a more scalable database like PostgreSQL for production use.
- **Error Handling**: Enhance error handling to cover more edge cases and provide more descriptive error messages.
- **Logging & Monitoring**: Integrate with tools like ELK Stack or Grafana for better logging and monitoring.
- **Testing**: Increase test coverage, especially for edge cases.

## Cloud Deployment Considerations

- **Containerization**: Use Docker to containerize the application for easy deployment and scaling.
- **Kubernetes**: Use Kubernetes for orchestration to ensure high availability and scalability.
- **Managed Database**: Use cloud-managed databases like Amazon RDS or Google Cloud SQL for better performance and backups.
- **Serverless**: Consider using serverless technologies like AWS Lambda or Google Cloud Functions for specific microservices to reduce costs.
- **CDN & Load Balancing**: Use services like Amazon CloudFront or Google Cloud Load Balancing to distribute traffic and reduce latency.
- **Security**: Ensure all data in transit is encrypted using SSL/TLS. Use cloud-native security tools for regular vulnerability assessments.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
