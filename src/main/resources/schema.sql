USE portafoglio;

CREATE TABLE IF NOT EXISTS customer_accounts (
    `customer_id` BIGINT NOT NULL,
    `name` VARCHAR(255),
    `balance` DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (`customer_id`),
    UNIQUE KEY (`customer_id`),
    KEY `game_events_customer_idx` (`customer_id`)
);

CREATE TABLE IF NOT EXISTS game_events (
    `event_id` BIGINT NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `type` ENUM('PURCHASE', 'WIN') NOT NULL,
    `amount` DECIMAL(19,2) NOT NULL,
    `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`event_id`),
    UNIQUE KEY (`event_id`),
    KEY `customer_id_idx` (`customer_id`)  -- Changed to regular KEY
);
