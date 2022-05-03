### Found Lost App Bakend

### How to run in Docker
 1. In the project root run:
    ```
    docker build -t foundlost-back .
    ```
    
  2. Run 
    ```
    docker-compose up
    ```
    
    
  3. Connect to the database using DBeaver/MySQL Workspace or SQL Developer.
  Main configurations:
   ![Main configuration](Main-conf-db.png)
   
   Driver configurations: 
   ![Driver conf](Driver-conf-db-2.png)
   
   SSL configurations:
   ![SSL configuratiob](SSL-conf-db-4.png)
   
   And you must be able to connect and see the tables:
   ![DB](tables.png)
   
  4. Insert in the roles table the next values:

  ![Roles](roles.png)
  
  5. Access SWAGGER: http://localhost:8080/foundlost/swagger-ui/


#### In case of problems
In case of an error related to id and autoincrement do:
1. Delete posts table.
2. Check the autoincrement checkbox from properties for field "id" for users table.
![Roles](check.png)

3. Execute:
```
CREATE TABLE `posts` (
`post_id` bigint NOT NULL,
`address` varchar(255) DEFAULT NULL,
`age` varchar(255) DEFAULT NULL,
`breed` varchar(255) DEFAULT NULL,
`contacts` varchar(255) DEFAULT NULL,
`created_date` datetime DEFAULT NULL,
`details` varchar(255) DEFAULT NULL,
`eye_color` varchar(255) DEFAULT NULL,
`fur_color` varchar(255) DEFAULT NULL,
`gender` varchar(255) DEFAULT NULL,
`image` varchar(255) DEFAULT NULL,
`name` varchar(255) DEFAULT NULL,
`nationality` varchar(255) DEFAULT NULL,
`reward` bigint DEFAULT NULL,
`special_signs` varchar(255) DEFAULT NULL,
`species` varchar(255) DEFAULT NULL,
`status` varchar(255) DEFAULT NULL,
`type` varchar(255) DEFAULT NULL,
`author_id` bigint DEFAULT NULL,
PRIMARY KEY (`post_id`),
KEY `FK6xvn0811tkyo3nfjk2xvqx6ns` (`author_id`),
CONSTRAINT `FK6xvn0811tkyo3nfjk2xvqx6ns` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

```

  

   