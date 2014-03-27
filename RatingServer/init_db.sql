CREATE TABLE `Survey` (
  `id` varchar(20) NOT NULL,
  `title` text NOT NULL,
  `subtitle` text,
  `description` text,
  `image_url` varchar(255),
  PRIMARY KEY (`id`)
);

CREATE TABLE `Item` (
  `survey_id` varchar(20) NOT NULL,
  `item_id` int(11) NOT NULL,
  `title` text,
  `subtitle` text,
  `description` text,
  `image_url` varchar(255),
  `rating` decimal(4,3) NOT NULL DEFAULT '0.000',
  `votes` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`survey_id`,`item_id`),
  FOREIGN KEY (`survey_id`) REFERENCES `Survey` (`id`)
);

CREATE TABLE `Vote` (
  `survey_id` varchar(20) NOT NULL,
  `item_id` int(11) NOT NULL,
  `user_id` char(16) NOT NULL,
  PRIMARY KEY (`survey_id`,`item_id`, `user_id`),
  FOREIGN KEY (`survey_id`, `item_id`) REFERENCES `Item` (`survey_id`, `item_id`)
);

CREATE TABLE `Image` (
  `name` varchar(50) NOT NULL,
  `type` varchar(31) NOT NULL,
  `data` longblob NOT NULL,
  PRIMARY KEY (`name`)
);
