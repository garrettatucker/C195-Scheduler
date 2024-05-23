-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: c195
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `Appointment_ID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(50) DEFAULT NULL,
  `Description` varchar(50) DEFAULT NULL,
  `Location` varchar(50) DEFAULT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `Start` datetime DEFAULT NULL,
  `End` datetime DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Created_By` varchar(50) DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  `Last_Updated_By` varchar(50) DEFAULT NULL,
  `Customer_ID` int DEFAULT NULL,
  `User_ID` int DEFAULT NULL,
  `Contact_ID` int DEFAULT NULL,
  PRIMARY KEY (`Appointment_ID`),
  KEY `appointments_ibfk_1` (`Customer_ID`),
  KEY `appointments_ibfk_2` (`User_ID`),
  KEY `appointments_ibfk_3` (`Contact_ID`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`Customer_ID`) REFERENCES `customers` (`Customer_ID`),
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`User_ID`) REFERENCES `users` (`User_ID`),
  CONSTRAINT `appointments_ibfk_3` FOREIGN KEY (`Contact_ID`) REFERENCES `contacts` (`Contact_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,'title','description','location','Planning Session','2020-05-28 12:00:00','2020-05-28 13:00:00','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1,1,3),(2,'title','description','location','De-Briefing','2020-05-29 12:00:00','2020-05-29 13:00:00','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',2,2,2),(8,'test','test','test','test','2024-04-09 00:30:00','2024-04-15 02:00:00','2024-04-15 08:20:38','test','2024-04-15 13:20:38','test',2,2,1),(10,'a','a','a','a','2024-04-15 11:00:00','2024-04-15 12:00:00','2024-04-15 09:02:26','test','2024-04-15 14:02:26','test',6,1,2),(11,'b','b','b','b','2024-04-17 19:00:00','2024-04-17 19:30:00','2024-04-15 09:13:32','test','2024-04-16 15:55:26','admin',6,1,3),(12,'q','q','q','q','2024-04-15 15:00:00','2024-04-15 21:30:00','2024-04-15 09:44:14','test','2024-04-15 14:44:14','test',6,1,2),(13,'test','test','test','test','2024-05-01 19:00:00','2024-05-01 20:30:00','2024-04-15 10:55:51','test','2024-04-16 15:55:46','admin',1,2,2),(14,'dude','dudr','dude','dude','2024-05-10 01:00:00','2024-05-10 02:00:00','2024-04-16 09:31:33','test','2024-04-16 15:56:01','admin',11,2,2);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacts` (
  `Contact_ID` int NOT NULL AUTO_INCREMENT,
  `Contact_Name` varchar(50) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Contact_ID`),
  UNIQUE KEY `Contact_ID_UNIQUE` (`Contact_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
INSERT INTO `contacts` VALUES (1,'Anika Costa','acoasta@company.com'),(2,'Daniel Garcia','dgarcia@company.com'),(3,'Li Lee','llee@company.com');
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `countries`
--

DROP TABLE IF EXISTS `countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `countries` (
  `Country_ID` int NOT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Created_By` varchar(50) DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  `Last_Updated_By` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Country_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `countries`
--

LOCK TABLES `countries` WRITE;
/*!40000 ALTER TABLE `countries` DISABLE KEYS */;
INSERT INTO `countries` VALUES (1,'U.S','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script'),(2,'UK','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script'),(3,'Canada','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script');
/*!40000 ALTER TABLE `countries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `Customer_ID` int NOT NULL AUTO_INCREMENT,
  `Customer_Name` varchar(50) DEFAULT NULL,
  `Address` varchar(100) DEFAULT NULL,
  `Postal_Code` varchar(50) DEFAULT NULL,
  `Phone` varchar(50) DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Created_By` varchar(50) DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  `Last_Updated_By` varchar(50) DEFAULT NULL,
  `Division_ID` int DEFAULT NULL,
  PRIMARY KEY (`Customer_ID`),
  UNIQUE KEY `Customer_ID_UNIQUE` (`Customer_ID`),
  KEY `Division_ID` (`Division_ID`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`Division_ID`) REFERENCES `first_level_divisions` (`Division_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Daddy Warbucks','1919 Boardwalk','01291','869-908-1875','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',29),(2,'Lady McAnderson','2 Wonder Way','AF19B','11-445-910-2135','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',103),(3,'Dudley Do-Right12','48 Horse Manor 12','2819812','874-916-2671212','2024-04-04 13:45:25','script','2024-04-11 15:36:48','test',3),(6,'3','3','3','3','2024-04-10 14:08:40',NULL,'2024-04-10 19:08:40',NULL,1),(7,'2314','2143','1243','2143','2024-04-10 14:09:44',NULL,'2024-04-16 15:33:47','test',2),(11,'George Washington','123 Wash Road','11111','811-555-5555','2024-04-11 08:24:01','test','2024-04-11 13:24:01','test',7),(12,'King George','Palace','889998','565465465465465','2024-04-11 08:26:19','test','2024-04-11 13:26:19','test',101);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `first_level_divisions`
--

DROP TABLE IF EXISTS `first_level_divisions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `first_level_divisions` (
  `Division_ID` int NOT NULL,
  `Division` varchar(50) DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Created_By` varchar(50) DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  `Last_Updated_By` varchar(50) DEFAULT NULL,
  `Country_ID` int DEFAULT NULL,
  PRIMARY KEY (`Division_ID`),
  KEY `Country_ID` (`Country_ID`),
  CONSTRAINT `first_level_divisions_ibfk_1` FOREIGN KEY (`Country_ID`) REFERENCES `countries` (`Country_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `first_level_divisions`
--

LOCK TABLES `first_level_divisions` WRITE;
/*!40000 ALTER TABLE `first_level_divisions` DISABLE KEYS */;
INSERT INTO `first_level_divisions` VALUES (1,'Alabama','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(2,'Arizona','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(3,'Arkansas','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(4,'California','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(5,'Colorado','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(6,'Connecticut','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(7,'Delaware','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(8,'District of Columbia','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(9,'Florida','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(10,'Georgia','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(11,'Idaho','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(12,'Illinois','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(13,'Indiana','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(14,'Iowa','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(15,'Kansas','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(16,'Kentucky','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(17,'Louisiana','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(18,'Maine','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(19,'Maryland','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(20,'Massachusetts','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(21,'Michigan','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(22,'Minnesota','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(23,'Mississippi','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(24,'Missouri','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(25,'Montana','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(26,'Nebraska','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(27,'Nevada','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(28,'New Hampshire','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(29,'New Jersey','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(30,'New Mexico','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(31,'New York','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(32,'North Carolina','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(33,'North Dakota','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(34,'Ohio','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(35,'Oklahoma','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(36,'Oregon','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(37,'Pennsylvania','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(38,'Rhode Island','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(39,'South Carolina','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(40,'South Dakota','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(41,'Tennessee','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(42,'Texas','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(43,'Utah','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(44,'Vermont','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(45,'Virginia','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(46,'Washington','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(47,'West Virginia','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(48,'Wisconsin','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(49,'Wyoming','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(52,'Hawaii','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(54,'Alaska','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',1),(60,'Northwest Territories','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(61,'Alberta','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(62,'British Columbia','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(63,'Manitoba','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(64,'New Brunswick','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(65,'Nova Scotia','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(66,'Prince Edward Island','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(67,'Ontario','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(68,'Québec','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(69,'Saskatchewan','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(70,'Nunavut','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(71,'Yukon','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(72,'Newfoundland and Labrador','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',3),(101,'England','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',2),(102,'Wales','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',2),(103,'Scotland','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',2),(104,'Northern Ireland','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script',2);
/*!40000 ALTER TABLE `first_level_divisions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `User_ID` int NOT NULL,
  `User_Name` varchar(50) DEFAULT NULL,
  `Password` text,
  `Create_Date` datetime DEFAULT NULL,
  `Created_By` varchar(50) DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  `Last_Updated_By` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`User_ID`),
  UNIQUE KEY `User_ID_UNIQUE` (`User_ID`),
  UNIQUE KEY `User_Name` (`User_Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test','test','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script'),(2,'admin','admin','2024-04-04 13:45:25','script','2024-04-04 18:45:25','script');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-17  8:09:58
