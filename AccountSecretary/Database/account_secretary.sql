-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: account_secretary
-- ------------------------------------------------------
-- Server version	5.7.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `as_record`
--

DROP TABLE IF EXISTS `as_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `as_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `datetime` datetime NOT NULL,
  `is_income` tinyint(1) NOT NULL DEFAULT '0',
  `money` decimal(9,2) NOT NULL,
  `type_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `type_id_idx` (`type_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `record_type_id` FOREIGN KEY (`type_id`) REFERENCES `as_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `record_user_id` FOREIGN KEY (`user_id`) REFERENCES `as_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `as_record`
--

LOCK TABLES `as_record` WRITE;
/*!40000 ALTER TABLE `as_record` DISABLE KEYS */;
INSERT INTO `as_record` VALUES (1,'账目测试','2016-11-25 01:17:26',0,100.00,5,1);
/*!40000 ALTER TABLE `as_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `as_security`
--

DROP TABLE IF EXISTS `as_security`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `as_security` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `as_security`
--

LOCK TABLES `as_security` WRITE;
/*!40000 ALTER TABLE `as_security` DISABLE KEYS */;
INSERT INTO `as_security` VALUES (1,'你觉得什么事最让你高兴？'),(2,'你最喜欢吃的水果是什么？'),(3,'你最喜欢的颜色是什么？'),(4,'你最喜欢听的歌是哪首？'),(5,'你最喜欢的明星是谁？');
/*!40000 ALTER TABLE `as_security` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `as_type`
--

DROP TABLE IF EXISTS `as_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `as_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(8) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `type_user_id` FOREIGN KEY (`user_id`) REFERENCES `as_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `as_type`
--

LOCK TABLES `as_type` WRITE;
/*!40000 ALTER TABLE `as_type` DISABLE KEYS */;
INSERT INTO `as_type` VALUES (1,'购物',NULL),(2,'餐饮',NULL),(3,'交通',NULL),(4,'医疗',NULL),(5,'通讯',NULL),(6,'其他',NULL);
/*!40000 ALTER TABLE `as_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `as_user`
--

DROP TABLE IF EXISTS `as_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `as_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `icon_id` int(11) NOT NULL,
  `password` varchar(32) NOT NULL,
  `security_answer` varchar(20) NOT NULL,
  `security_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `security_id_idx` (`security_id`),
  CONSTRAINT `user_security_id` FOREIGN KEY (`security_id`) REFERENCES `as_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `as_user`
--

LOCK TABLES `as_user` WRITE;
/*!40000 ALTER TABLE `as_user` DISABLE KEYS */;
INSERT INTO `as_user` VALUES (1,'test',1,'74D839D98630E280DF752E8939454A6B','紫色',3);
/*!40000 ALTER TABLE `as_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-24 20:01:41
