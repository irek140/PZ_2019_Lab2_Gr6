CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `id_role` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
