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
)ENGINE =InnoDB DEFAULT  CHARSET =utf8;
