# Create data base model for production

# Create data base
CREATE DATABASE IF NOT EXISTS pay_my_buddy CHARACTER SET utf8;

#Create tables
CREATE TABLE IF NOT EXISTS pay_my_buddy.bank_account (
    bank_account_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    rib VARCHAR(40) NOT NULL,
    bank VARCHAR(40) NOT NULL,
    iban VARCHAR(40) NOT NULL,
    bic VARCHAR(15) NOT NULL,
    PRIMARY KEY (bank_account_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.user_account (
    user_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(15) NOT NULL,
    last_name VARCHAR(15) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    bank_account_id BIGINT UNSIGNED NOT NULL,
    balance DECIMAL(6,2) UNSIGNED NOT NULL,
    PRIMARY KEY (user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.role_profile (
    role_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(15) NOT NULL UNIQUE,
    PRIMARY KEY (role_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.privilege (
    privilege_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    privilege_name VARCHAR(30) NOT NULL UNIQUE,
    PRIMARY KEY (privilege_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.transfer(
    transfer_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    sender_user_id BIGINT UNSIGNED NOT NULL,
    receiver_user_id BIGINT UNSIGNED NOT NULL,
    description VARCHAR(60) NOT NULL,
    transfer_date DATE NOT NULL,
    amount DECIMAL(6,2) UNSIGNED NOT NULL,
    fee DECIMAL(4,2) UNSIGNED NOT NULL,
    transfer_type VARCHAR(40) NOT NULL,
    PRIMARY KEY (transfer_id)
) ENGINE = InnoDB;

# Create association tables
CREATE TABLE IF NOT EXISTS pay_my_buddy.transfer_log (
    transfer_id BIGINT UNSIGNED NOT NULL,
    sender_user_id BIGINT UNSIGNED NOT NULL,
    receiver_user_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (transfer_id, sender_user_id, receiver_user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.connection (
    user_id BIGINT UNSIGNED NOT NULL,
    connection_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, connection_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.user_role (
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pay_my_buddy.role_privilege (
    role_id BIGINT UNSIGNED NOT NULL,
    privilege_id BIGINT UNSIGNED NOT NULL
) ENGINE = InnoDB;

# Create table constraints
ALTER TABLE pay_my_buddy.user_account
    ADD CONSTRAINT fk_bank_account_id
        FOREIGN KEY (bank_account_id)
            REFERENCES bank_account(bank_account_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE pay_my_buddy.transfer
    ADD CONSTRAINT fk_sender
        FOREIGN KEY (sender_user_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_receiver
        FOREIGN KEY(receiver_user_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD INDEX ind_date (transfer_date);

ALTER TABLE pay_my_buddy.transfer_log
    ADD CONSTRAINT fk_transfer
        FOREIGN KEY (transfer_id)
            REFERENCES transfer(transfer_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_sender_log
        FOREIGN KEY (sender_user_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_receiver_log
        FOREIGN KEY (receiver_user_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE pay_my_buddy.connection
    ADD CONSTRAINT fk_user_connection
        FOREIGN KEY (user_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_connection
        FOREIGN KEY (connection_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE pay_my_buddy.user_role
    ADD CONSTRAINT fk_user_id_role
        FOREIGN KEY (user_id)
            REFERENCES user_account(user_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_role_id
        FOREIGN KEY (role_id)
            REFERENCES role_profile(role_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE pay_my_buddy.role_privilege
    ADD CONSTRAINT fk_role_id_privilege
        FOREIGN KEY (role_id)
            REFERENCES role_profile(role_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_privilege_id
        FOREIGN KEY (privilege_id)
            REFERENCES privilege(privilege_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;