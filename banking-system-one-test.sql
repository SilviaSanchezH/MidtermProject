DROP SCHEMA banking_system_one_test;
CREATE SCHEMA banking_system_one_test;
USE banking_system_one_test;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255),
  `password` varchar(255),
  `username` varchar(255) UNIQUE,
  PRIMARY KEY (`id`)
);

CREATE TABLE `admin` (
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `user` (`id`)
);

CREATE TABLE `account_holder` (
  `birth` date,
  `primary_city` varchar(255),
  `primary_street` varchar(255),
  `primary_postal_code` varchar(255),
  `mailing_city` varchar(255), 
  `mailing_street` varchar(255),
  `mailing_postal_code` varchar(255),
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `user` (`id`)
);

CREATE TABLE `third_party` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hashed_key` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255),
  `user_id` int,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ;

CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `balance_amount` decimal(38,2) NOT NULL,
  `balance_currency` varchar(255),
  `created_at` date DEFAULT NULL,
  `penalty_fee_amount` decimal(19,4) DEFAULT '40.00',
  `penalty_fee_currency` varchar(255),
  `primary_owner_id` int,
  `secondary_owner_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`secondary_owner_id`) REFERENCES `account_holder` (`id`),
  FOREIGN KEY (`primary_owner_id`) REFERENCES `account_holder` (`id`)
);


CREATE TABLE `checking` (
  `minimum_balance_amount` decimal(19,2) DEFAULT NULL,
  `minimum_balance_currency` varchar(255) DEFAULT NULL,
  `monthly_maintenance_fee_amount` decimal(19,2) DEFAULT NULL,
  `monthly_maintenance_fee_currency` varchar(255) DEFAULT NULL,
  `secret_key` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE `savings` (
  `interest_addition_date` date DEFAULT NULL,
  `interest_rate` decimal(19,4) DEFAULT '0.0025',
  `minimum_balance_amount` decimal(19,2) DEFAULT '1000.00',
  `minimum_balance_currency` varchar(255),
  `secret_key` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE `student_checking` (
  `secret_key` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE `credit_card` (
  `credit_limit_amount` decimal(19,2) DEFAULT '100.00',
  `credit_limit_currency` varchar(255) DEFAULT NULL,
  `interest_addition_date` date DEFAULT NULL,
  `interest_rate` decimal(19,4) DEFAULT '0.20',
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE `transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `transaction_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(19,2) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `destination_account` int DEFAULT NULL,
  `destination_third_party` int DEFAULT NULL,
  `origin_account` int DEFAULT NULL,
  `origin_third_party` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`origin_third_party`) REFERENCES `third_party` (`id`),
  FOREIGN KEY (`destination_account`) REFERENCES `account` (`id`),
  FOREIGN KEY (`origin_account`) REFERENCES `account` (`id`),
  FOREIGN KEY (`destination_third_party`) REFERENCES `third_party` (`id`)
);

INSERT INTO user(`name`, username, password) VALUES
('irene', 'admin', '$2a$10$wfWzZ3g2LMvyDNTlLiRwDuhjPKRfe7BTI3cvrzB2LP4oS66pe7BVS');

INSERT INTO role(`name`, user_id) VALUES
('ADMIN', 1);

INSERT INTO admin (id) VALUES
(1);