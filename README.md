Task Application

Overview:
Task Application is a Spring Boot-based multi-user task approval system that allows users to create tasks, request approvals, and collaborate through comments. The application provides a robust backend solution for task management with a multi-approval workflow.

Features:
User Authentication:
1. Sign up and login functionality
2. User management


Task Management:
1. Create tasks with dynamic status tracking
2. Multi-user approval process (requires 3 approvals)
3. Comprehensive comment system


Approval Workflow:
1. Request approvals from multiple users
2. Track approval status
3. Notification mechanisms for task progress



Tech Stack:
1. Backend: Spring Boot 3.4.0
2. Database: PostgreSQL
3. ORM: Spring Data JPA
   
Dependencies:
1. Spring Web
2. Spring Data JPA
3. Spring Boot Validation
4. Spring Boot Mail
5. Lombok
6. Mockito (for testing)



Prerequisites:
1. Java 17
2. Maven
3. PostgreSQL database

Configuration:

Database Setup:
1. Install PostgreSQL
2. Create a new database for the application
   
3. Update application.properties with your database credentials:
`propertiesCopyspring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name`
`spring.datasource.username=your_username`
`spring.datasource.password=your_password`

Installation
1. Clone the repository:
`git clone https://github.com/BhavishyaTolani/TaskServiceApplication.git`
`cd TaskApplication`

Build the project:
`mvn clean install`

Run the application:
`mvn spring-boot:run`



API Endpoints:

User Controller:
1. POST /api/users: Sign up a new user
2. GET /api/users/loginId/{id}/password/{password}: User login

Task Controller:
1. POST /api/tasks: Create a new task
2. GET /api/tasks/taskId/{taskId}: Retrieve a specific task
3. POST /api/tasks/comment: Add a comment to a task
4. GET /api/tasks/comments/{commentId}: View a specific comment
5. GET /api/tasks/taskId/{taskId}/comments: View all comments for a task

Approval Controller:
1. POST /api/requestForApproval: Request approval for a task
2. POST /api/approve: Approve a task

Testing:
Run the tests using Maven
`mvn test`
