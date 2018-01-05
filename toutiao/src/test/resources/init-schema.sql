drop table if exists `user`;
create table `user`(
  `id` int(11) unsigned not null auto_increment,
  `name` varchar(64) not null default '',
  `password` varchar(128) not null default '',
  `salt` varchar(32) not null default '',
  `head_url` varchar(256) not null default '',
  PRIMARY KEY (`id`),
  UNIQUE key `name` (`name`)
)ENGINE =InnoDB DEFAULT CHARSET =utf8;

drop table if exists `news`;
create table `news`(
  `id` int(11) unsigned not null auto_increment,
  `title` varchar(128) not null default '',
  `link` varchar(256) not null default '',
  `image` varchar(256) not null default '',
  `like_count`int not null,
  `comment_count`int not null,
  `created_date` datetime not null,
  `user_id` int(11) not null,
  PRIMARY KEY (`id`)
)ENGINE =InnoDB  DEFAULT  CHARSET =utf8;

drop table if exists `login_ticket`;
create table `login_ticket`(
  `id` int not null AUTO_INCREMENT,
  `user_id` int not null,
  `ticket` varchar(45) not null,
  `expired` datetime not null,
  `status`   int  null default 0,
  primary key (`id`),
  unique index `ticket_UNIQUE` (`ticket` asc )
)ENGINE =InnoDB DEFAULT  CHARSET =utf8;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `user_id` INT NOT NULL,
  `entity_id` INT NOT NULL,
  `entity_type` INT NOT NULL,
  `created_date` DATETIME NOT NULL,
  `status` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `entity_index` (`entity_id` ASC, `entity_type` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `from_id` INT NULL,
  `to_id` INT NULL,
  `content` TEXT NULL,
  `created_date` DATETIME NULL,
  `has_read` INT NULL,
  `conversation_id` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `conversation_index` (`conversation_id` ASC),
  INDEX `created_date` (`created_date` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;
