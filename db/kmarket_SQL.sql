-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema kmarket
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema kmarket
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `kmarket` DEFAULT CHARACTER SET utf8 ;
USE `kmarket` ;

-- -----------------------------------------------------
-- Table `kmarket`.`km_member_general`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_member_general` (
  `login_id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `phone_number` CHAR(13) NOT NULL,
  `email` VARCHAR(100) NULL,
  `type` TINYINT NOT NULL,
  `point` INT NULL DEFAULT 0,
  `level` TINYINT NULL DEFAULT 1,
  `zip_code` VARCHAR(10) NULL,
  `address` VARCHAR(255) NULL,
  `detail_address` VARCHAR(255) NULL,
  `regip` VARCHAR(100) NOT NULL,
  `withdrawal_date` DATETIME NULL,
  `ragistration_date` DATETIME NOT NULL,
  PRIMARY KEY (`login_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_member_seller`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_member_seller` (
  `login_id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `type` TINYINT NOT NULL,
  `point` INT NULL DEFAULT 0,
  `level` TINYINT NULL DEFAULT 1,
  `zip_code` VARCHAR(10) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `detail_address` VARCHAR(255) NOT NULL,
  `company` VARCHAR(20) NOT NULL,
  `ceo` VARCHAR(20) NOT NULL,
  `company_registration_number` CHAR(12) NOT NULL,
  `report_number` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(20) NOT NULL,
  `manager_name` VARCHAR(20) NOT NULL,
  `manager_phone_number` CHAR(13) NOT NULL,
  `fax_number` VARCHAR(20) NOT NULL,
  `regip` VARCHAR(100) NOT NULL,
  `withdrawal_date` DATETIME NULL,
  `ragistragion_date` DATETIME NOT NULL,
  PRIMARY KEY (`login_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_member_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_member_user` (
  `uid` VARCHAR(20) NOT NULL,
  `type` TINYINT NOT NULL,
  PRIMARY KEY (`uid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_member_point`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_member_point` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(20) NOT NULL,
  `order_number` VARCHAR(50) NOT NULL,
  `earned_point` INT NOT NULL,
  `earned_point_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`, `uid`),
  INDEX `fk_km_member_point_km_member_user1_idx` (`uid` ASC) VISIBLE,
  CONSTRAINT `fk_km_member_point_km_member_user1`
    FOREIGN KEY (`uid`)
    REFERENCES `kmarket`.`km_member_user` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_member_terms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_member_terms` (
  `terms` TEXT NOT NULL,
  `privacy` TEXT NOT NULL,
  `location` TEXT NOT NULL,
  `finance` TEXT NOT NULL,
  `tax` TEXT NOT NULL)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product_category1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product_category1` (
  `category1_code` INT NOT NULL,
  `category1_name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`category1_code`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product_category2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product_category2` (
  `category2_code` INT NOT NULL,
  `category1_code` INT NOT NULL,
  `category2_name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`category2_code`, `category1_code`),
  INDEX `fk_km_product_category2_km_product_category11_idx` (`category1_code` ASC) VISIBLE,
  CONSTRAINT `fk_km_product_category2_km_product_category11`
    FOREIGN KEY (`category1_code`)
    REFERENCES `kmarket`.`km_product_category1` (`category1_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `category1_code` INT NOT NULL,
  `category2_code` INT NOT NULL,
  `seller` VARCHAR(20) NOT NULL,
  `product_name` VARCHAR(100) NOT NULL,
  `descript` VARCHAR(100) NOT NULL,
  `company` VARCHAR(100) NOT NULL,
  `price` INT NOT NULL,
  `discount` TINYINT NULL DEFAULT 0,
  `point` INT NULL DEFAULT 0,
  `stock` INT NULL DEFAULT 0,
  `sold_number` INT NULL DEFAULT 0,
  `delivery_cost` INT NULL DEFAULT 0,
  `hit` INT NULL DEFAULT 0,
  `score` TINYINT NULL DEFAULT 0,
  `review` INT NULL DEFAULT 0,
  `thumbnail1` VARCHAR(255) NOT NULL,
  `thumbnail2` VARCHAR(255) NOT NULL,
  `thumbnail3` VARCHAR(255) NOT NULL,
  `detail_cut` VARCHAR(255) NOT NULL,
  `status` VARCHAR(20) NULL DEFAULT '새상품',
  `duty` VARCHAR(20) NULL DEFAULT '과세상품',
  `receipt` VARCHAR(30) NULL DEFAULT '발행가능 - 신용카드 전표, 온라인 현금영수증',
  `business_type` VARCHAR(20) NULL DEFAULT '사업자 판매자',
  `origin` VARCHAR(20) NULL DEFAULT '상세 설명 참고',
  `product_ip` VARCHAR(20) NOT NULL,
  `ragistration_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`, `seller`),
  INDEX `fk_km_product_km_member_seller1_idx` (`seller` ASC) VISIBLE,
  INDEX `fk_km_product_km_product_category21_idx` (`category2_code` ASC, `category1_code` ASC) VISIBLE,
  CONSTRAINT `fk_km_product_km_member_seller1`
    FOREIGN KEY (`seller`)
    REFERENCES `kmarket`.`km_member_seller` (`login_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_km_product_km_product_category21`
    FOREIGN KEY (`category2_code` , `category1_code`)
    REFERENCES `kmarket`.`km_product_category2` (`category2_code` , `category1_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product_order` (
  `order_number` VARCHAR(50) NOT NULL,
  `uid` VARCHAR(20) NOT NULL,
  `order_count` INT NULL DEFAULT 0,
  `order_price` INT NULL DEFAULT 0,
  `order_discount` INT NULL DEFAULT 0,
  `order_delivery` INT NULL DEFAULT 0,
  `save_point` INT NULL DEFAULT 0,
  `used_point` INT NULL DEFAULT 0,
  `order_total_price` INT NULL DEFAULT 0,
  `recip_name` VARCHAR(20) NOT NULL,
  `recip_hp` VARCHAR(13) NOT NULL,
  `recip_zip` CHAR(5) NOT NULL,
  `recip_address` VARCHAR(255) NOT NULL,
  `recip_detail_address` VARCHAR(255) NOT NULL,
  `order_payment` TINYINT NOT NULL,
  `order_complete` TINYINT NOT NULL,
  `order_date` DATETIME NOT NULL,
  `order_state` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`order_number`, `uid`),
  INDEX `fk_km_product_order_km_member_user1_idx` (`uid` ASC) VISIBLE,
  CONSTRAINT `fk_km_product_order_km_member_user1`
    FOREIGN KEY (`uid`)
    REFERENCES `kmarket`.`km_member_user` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product_order_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product_order_item` (
  `order_number` VARCHAR(50) NOT NULL,
  `product_id` INT(10) NOT NULL,
  `count` INT NOT NULL,
  `price` INT NOT NULL,
  `discount` TINYINT NOT NULL,
  `point` INT NOT NULL,
  `delivery_cost` INT NOT NULL,
  `total_price` INT NOT NULL,
  PRIMARY KEY (`order_number`, `product_id`),
  INDEX `fk_km_product_order_item_km_product1_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_km_product_order_item_km_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `kmarket`.`km_product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product_cart`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product_cart` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `product_id` INT(10) NOT NULL,
  `uid` VARCHAR(20) NOT NULL,
  `count` INT NOT NULL,
  `price` INT NOT NULL,
  `discount` INT NOT NULL,
  `point` INT NOT NULL,
  `delivery_cost` INT NOT NULL,
  `total_price` INT NOT NULL,
  `ragistration_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`, `product_id`, `uid`),
  INDEX `fk_km_product_cart_km_member_user1_idx` (`uid` ASC) VISIBLE,
  INDEX `fk_km_product_cart_km_product1_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_km_product_cart_km_member_user1`
    FOREIGN KEY (`uid`)
    REFERENCES `kmarket`.`km_member_user` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_km_product_cart_km_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `kmarket`.`km_product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kmarket`.`km_product_review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `kmarket`.`km_product_review` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `product_id` INT(10) NOT NULL,
  `uid` VARCHAR(20) NOT NULL,
  `content` VARCHAR(255) NOT NULL,
  `rating` TINYINT NOT NULL,
  `regip` VARCHAR(100) NOT NULL,
  `registration_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`, `product_id`, `uid`),
  INDEX `fk_km_product_review_km_product1_idx` (`product_id` ASC) VISIBLE,
  INDEX `fk_km_product_review_km_member_user1_idx` (`uid` ASC) VISIBLE,
  CONSTRAINT `fk_km_product_review_km_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `kmarket`.`km_product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_km_product_review_km_member_user1`
    FOREIGN KEY (`uid`)
    REFERENCES `kmarket`.`km_member_user` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
