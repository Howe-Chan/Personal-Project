-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: namecard_manager
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
-- Table structure for table `nm_namecard`
--

DROP TABLE IF EXISTS `nm_namecard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nm_namecard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_pinyin` varchar(60) NOT NULL,
  `name` varchar(20) NOT NULL,
  `job_title` varchar(100) DEFAULT NULL,
  `company_name` varchar(100) DEFAULT NULL,
  `company_address` varchar(100) DEFAULT NULL,
  `mobile` varchar(11) DEFAULT NULL,
  `tel` varchar(13) DEFAULT NULL,
  `fax` varchar(15) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `web` varchar(50) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `namecard_user_id_idx` (`user_id`),
  CONSTRAINT `namecard_user_id` FOREIGN KEY (`user_id`) REFERENCES `nm_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nm_namecard`
--

LOCK TABLES `nm_namecard` WRITE;
/*!40000 ALTER TABLE `nm_namecard` DISABLE KEYS */;
INSERT INTO `nm_namecard` VALUES (1,'ceshi','测试','总经理','四川测试技术有限公司','成都市高新区天府二街138号蜀都中心1栋1单元7F','13747747474','028-47747474','028-47747474','ceshi@ceshi.com','www.ceshi.com',1),(2,'shanchunqiu','单春秋','测试工程师','四川春秋大梦责任有限公司','四川省成都市武侯区咸阳大道158号先飞大厦一栋2单元15楼','13586429843','028-86496345','028-86496345','shanchuqiu@qq.com','www.chuqiudream.com',1);
/*!40000 ALTER TABLE `nm_namecard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nm_security`
--

DROP TABLE IF EXISTS `nm_security`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nm_security` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nm_security`
--

LOCK TABLES `nm_security` WRITE;
/*!40000 ALTER TABLE `nm_security` DISABLE KEYS */;
INSERT INTO `nm_security` VALUES (1,'你觉得什么事最让你高兴？'),(2,'你最喜欢吃的水果是什么？'),(3,'你最喜欢的颜色是什么？'),(4,'你最喜欢听的歌是哪首？'),(5,'你最喜欢的明星是谁？');
/*!40000 ALTER TABLE `nm_security` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nm_user`
--

DROP TABLE IF EXISTS `nm_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nm_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `icon_id` int(11) NOT NULL,
  `password` varchar(32) NOT NULL,
  `security_answer` varchar(20) NOT NULL,
  `security_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_security_id_idx` (`security_id`),
  CONSTRAINT `user_security_id` FOREIGN KEY (`security_id`) REFERENCES `nm_security` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nm_user`
--

LOCK TABLES `nm_user` WRITE;
/*!40000 ALTER TABLE `nm_user` DISABLE KEYS */;
INSERT INTO `nm_user` VALUES (1,'test',1,'74D839D98630E280DF752E8939454A6B','紫色',3);
/*!40000 ALTER TABLE `nm_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-02 11:40:12
