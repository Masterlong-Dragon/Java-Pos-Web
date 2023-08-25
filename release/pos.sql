-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: pos
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) DEFAULT NULL,
  `Contact` varchar(255) DEFAULT NULL,
  `Address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`CustomerID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Walk-in POS.Individual.Customer','','');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderdetails`
--

DROP TABLE IF EXISTS `orderdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderdetails` (
  `OrderID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Quantity` int DEFAULT NULL,
  `UnitPrice` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`ProductID`),
  KEY `orderdetails_ibfk_2` (`ProductID`),
  CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `orders` (`OrderID`),
  CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`ProductID`) REFERENCES `products` (`ProductID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderdetails`
--

LOCK TABLES `orderdetails` WRITE;
/*!40000 ALTER TABLE `orderdetails` DISABLE KEYS */;
INSERT INTO `orderdetails` VALUES (4,1,1,5.00),(5,1,1,5.00),(6,1,1,5.00),(6,2,40,140.00),(7,1,1,5.00),(8,1,1,5.00),(9,1,1,5.00),(10,1,1,5.00),(11,1,1,5.00),(12,1,1,5.00),(12,2,20,70.00),(13,1,1,5.00),(13,2,20,70.00),(14,2,14,49.00),(15,1,1,5.00),(15,2,20,70.00),(16,2,19,66.50),(17,2,20,70.00),(18,2,20,70.00),(19,2,5,17.50),(19,3,5,17.50),(20,2,5,17.50),(20,3,5,17.50),(21,1,10,50.00),(22,1,10,50.00),(23,1,10,50.00),(24,1,10,50.00),(25,1,10,50.00),(26,1,10,50.00),(27,1,10,50.00),(28,1,10,50.00),(29,1,10,50.00),(30,1,10,50.00),(31,1,10,50.00),(32,1,10,50.00),(33,1,10,50.00),(34,1,10,50.00),(35,1,10,50.00),(36,1,10,50.00),(37,1,10,50.00),(38,1,10,50.00),(39,1,10,50.00),(40,1,10,50.00),(41,1,10,50.00),(42,1,10,50.00),(42,2,10,35.00),(43,1,10,50.00),(43,2,10,35.00),(44,2,10,35.00),(45,1,1,5.00),(46,1,10,50.00),(47,1,2,10.00),(48,3,20,70.00),(49,2,20,70.00),(50,3,2,7.00),(51,2,2,7.00),(52,2,3,10.50),(53,1,2,10.00),(54,1,1,5.00),(55,1,1,5.00),(56,1,2,10.00),(57,1,2,10.00),(58,1,2,10.00),(59,1,4,20.00),(61,1,4,20.00),(61,2,4,14.00),(61,9,3,10.50),(62,1,4,20.00),(62,2,4,14.00),(62,9,3,10.50),(63,2,2,7.00),(64,1,20,100.00),(65,10,2,30.00),(65,11,2,10.00),(65,14,2,20.00);
/*!40000 ALTER TABLE `orderdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `OrderDate` date DEFAULT NULL,
  `TotalAmount` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  KEY `orders_ibfk_1` (`CustomerID`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,'2023-05-31',145.00),(3,1,'2023-05-31',145.00),(4,1,'2023-06-01',5.00),(5,1,'2023-06-01',5.00),(6,1,'2023-06-02',145.00),(7,1,'2023-06-02',5.00),(8,1,'2023-06-02',5.00),(9,1,'2023-06-02',5.00),(10,1,'2023-06-02',5.00),(11,1,'2023-06-02',5.00),(12,1,'2023-06-01',75.00),(13,1,'2023-06-01',75.00),(14,1,'2023-06-01',49.00),(15,1,'2023-06-01',75.00),(16,1,'2023-06-01',66.50),(17,1,'2023-06-03',70.00),(18,1,'2023-06-03',70.00),(19,1,'2023-06-07',35.00),(20,1,'2023-06-07',35.00),(21,1,'2023-06-13',50.00),(22,1,'2023-06-13',50.00),(23,1,'2023-06-13',50.00),(24,1,'2023-06-13',50.00),(25,1,'2023-06-13',50.00),(26,1,'2023-06-13',50.00),(27,1,'2023-06-13',50.00),(28,1,'2023-06-13',50.00),(29,1,'2023-06-13',50.00),(30,1,'2023-06-13',50.00),(31,1,'2023-06-13',50.00),(32,1,'2023-06-13',50.00),(33,1,'2023-06-13',50.00),(34,1,'2023-06-13',50.00),(35,1,'2023-06-13',50.00),(36,1,'2023-06-13',50.00),(37,1,'2023-06-13',50.00),(38,1,'2023-06-13',50.00),(39,1,'2023-06-13',50.00),(40,1,'2023-06-13',50.00),(41,1,'2023-06-13',50.00),(42,1,'2023-06-13',85.00),(43,1,'2023-06-13',85.00),(44,1,'2023-06-13',35.00),(45,1,'2023-06-13',5.00),(46,1,'2023-06-13',50.00),(47,1,'2023-06-13',10.00),(48,1,'2023-06-13',70.00),(49,1,'2023-06-13',70.00),(50,1,'2023-06-13',7.00),(51,1,'2023-06-13',7.00),(52,1,'2023-06-13',10.50),(53,1,'2023-06-13',10.00),(54,1,'2023-06-13',5.00),(55,1,'2023-06-13',5.00),(56,1,'2023-06-13',10.00),(57,1,'2023-06-13',10.00),(58,1,'2023-06-14',10.00),(59,1,'2023-06-14',20.00),(60,1,'2023-06-14',0.00),(61,1,'2023-06-14',44.50),(62,1,'2023-06-14',44.50),(63,1,'2023-06-14',7.00),(64,1,'2023-06-14',100.00),(65,1,'2023-06-15',60.00);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `PaymentID` int NOT NULL AUTO_INCREMENT,
  `OrderID` int DEFAULT NULL,
  `PaymentDate` date DEFAULT NULL,
  `PaymentMethod` varchar(255) DEFAULT NULL,
  `ChangeAmount` decimal(10,2) DEFAULT NULL,
  `Amount` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`PaymentID`),
  KEY `payments_ibfk_1` (`OrderID`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `orders` (`OrderID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,5,'2023-06-03','Cash',125.00,200.00),(2,5,'2023-06-03','Cash',125.00,200.00),(3,5,'2023-06-03','Cash',125.00,200.00),(4,5,'2023-06-03','Cash',125.00,200.00),(5,5,'2023-06-03','Cash',125.00,200.00),(6,5,'2023-06-03','Cash',125.00,200.00),(7,5,'2023-06-03','Cash',125.00,200.00),(9,18,'2023-06-03','Cash',130.00,200.00),(10,19,'2023-06-07','Cash',165.00,200.00),(11,16,'2023-06-07','Cash',-191.50,-200.00),(12,20,'2023-06-07','Cash',165.00,200.00),(13,14,'2023-06-07','Cash',-191.50,-200.00),(14,40,'2023-06-13','Mobile',0.00,50.00),(15,41,'2023-06-13','Mobile',10.00,60.00),(16,41,'2023-06-13','Mobile',10.00,60.00),(17,41,'2023-06-13','Mobile',10.00,60.00),(18,41,'2023-06-13','Mobile',10.00,60.00),(19,41,'2023-06-13','Mobile',10.00,60.00),(20,41,'2023-06-13','Mobile',10.00,60.00),(21,41,'2023-06-13','Mobile',10.00,60.00),(22,41,'2023-06-13','Mobile',10.00,60.00),(23,41,'2023-06-13','Mobile',10.00,60.00),(24,43,'2023-06-13','Mobile',0.00,85.00),(25,45,'2023-06-13','Cash',0.00,5.00),(26,45,'2023-06-13','Cash',1.00,6.00),(27,45,'2023-06-13','Cash',1.00,6.00),(28,45,'2023-06-13','Cash',1.00,6.00),(29,46,'2023-06-13','Cash',1.00,51.00),(30,46,'2023-06-13','Cash',1.00,51.00),(33,47,'2023-06-13','Cash',10.00,20.00),(34,50,'2023-06-13','Cash',3.00,10.00),(35,50,'2023-06-13','Cash',3.00,10.00),(36,51,'2023-06-13','Cash',13.00,20.00),(37,52,'2023-06-13','Cash',89.50,100.00),(38,52,'2023-06-13','Cash',89.50,100.00),(39,52,'2023-06-13','Cash',1989.50,2000.00),(40,54,'2023-06-13','Cash',0.00,5.00),(41,55,'2023-06-13','Cash',0.00,5.00),(42,56,'2023-06-13','Cash',1.00,11.00),(43,57,'2023-06-13','Cash',0.00,10.00),(45,63,'2023-06-14','Cash',1.00,8.00),(46,64,'2023-06-14','Mobile',20.00,120.00),(47,65,'2023-06-15','Mobile',20.00,80.00);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `ProductID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) DEFAULT NULL,
  `Price` decimal(10,2) DEFAULT NULL,
  `Stock` int DEFAULT NULL,
  PRIMARY KEY (`ProductID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'吃个桃桃',5.00,999885),(2,'可乐',3.50,999935),(3,'雪碧',3.50,999978),(4,'芬达',3.50,1000000),(9,'可悲',10.00,999994),(10,'喵喵',15.00,19998),(11,'Dr.Pepper',5.00,19998),(13,'波子汽水',7.00,100),(14,'鸭脖',10.00,2333331),(15,'三鲜冰粉',10.00,50);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salespersons`
--

DROP TABLE IF EXISTS `salespersons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salespersons` (
  `SalespersonID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) DEFAULT NULL,
  `Contact` varchar(255) DEFAULT NULL,
  `Address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`SalespersonID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salespersons`
--

LOCK TABLES `salespersons` WRITE;
/*!40000 ALTER TABLE `salespersons` DISABLE KEYS */;
INSERT INTO `salespersons` VALUES (1,'张三','12345678901','123456');
/*!40000 ALTER TABLE `salespersons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactionlog`
--

DROP TABLE IF EXISTS `transactionlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactionlog` (
  `TransactionID` int NOT NULL AUTO_INCREMENT,
  `TransactionDate` date DEFAULT NULL,
  `CustomerID` int DEFAULT NULL,
  `SalespersonID` int DEFAULT NULL,
  `TransactionType` varchar(255) DEFAULT NULL,
  `PaymentID` int DEFAULT NULL,
  PRIMARY KEY (`TransactionID`),
  KEY `transactionlog_ibfk_1` (`CustomerID`),
  KEY `transactionlog_ibfk_3` (`PaymentID`),
  KEY `transactionlog_ibfk_2` (`SalespersonID`),
  CONSTRAINT `transactionlog_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`),
  CONSTRAINT `transactionlog_ibfk_2` FOREIGN KEY (`SalespersonID`) REFERENCES `salespersons` (`SalespersonID`),
  CONSTRAINT `transactionlog_ibfk_3` FOREIGN KEY (`PaymentID`) REFERENCES `payments` (`PaymentID`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactionlog`
--

LOCK TABLES `transactionlog` WRITE;
/*!40000 ALTER TABLE `transactionlog` DISABLE KEYS */;
INSERT INTO `transactionlog` VALUES (2,'2023-06-03',1,1,'Sale',3),(3,'2023-06-03',1,1,'Sale',4),(4,'2023-06-03',1,1,'Sale',5),(5,'2023-06-03',1,1,'Sale',6),(6,'2023-06-03',1,1,'Sale',7),(7,'2023-06-03',1,1,'Sale',9),(8,'2023-06-07',1,1,'Sale',10),(9,'2023-06-07',1,1,'Return',11),(10,'2023-06-07',1,1,'Sale',12),(11,'2023-06-07',1,1,'Return',13),(12,'2023-06-13',1,1,'Sale',14),(13,'2023-06-13',1,1,'Sale',15),(14,'2023-06-13',1,1,'Sale',16),(15,'2023-06-13',1,1,'Sale',17),(16,'2023-06-13',1,1,'Sale',18),(17,'2023-06-13',1,1,'Sale',20),(18,'2023-06-13',1,1,'Sale',21),(19,'2023-06-13',1,1,'Sale',22),(20,'2023-06-13',1,1,'Sale',23),(21,'2023-06-13',1,1,'Sale',24),(22,'2023-06-13',1,1,'Sale',29),(23,'2023-06-13',1,1,'Sale',30),(24,'2023-06-13',1,1,'Sale',33),(25,'2023-06-13',1,1,'Sale',34),(26,'2023-06-13',1,1,'Sale',35),(27,'2023-06-13',1,1,'Sale',36),(28,'2023-06-13',1,1,'Sale',37),(29,'2023-06-13',1,1,'Sale',38),(30,'2023-06-13',1,1,'Sale',39),(31,'2023-06-13',1,1,'Sale',40),(32,'2023-06-13',1,1,'Sale',41),(33,'2023-06-13',1,1,'Sale',42),(34,'2023-06-13',1,1,'Sale',43),(35,'2023-06-14',1,1,'Sale',45),(36,'2023-06-14',1,1,'Sale',46),(37,'2023-06-15',1,1,'Sale',47);
/*!40000 ALTER TABLE `transactionlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `useraccounts`
--

DROP TABLE IF EXISTS `useraccounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `useraccounts` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) DEFAULT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `Role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `useraccounts`
--

LOCK TABLES `useraccounts` WRITE;
/*!40000 ALTER TABLE `useraccounts` DISABLE KEYS */;
INSERT INTO `useraccounts` VALUES (1,'admin','admin','Admin'),(2,'s1','123456','SalesPerson'),(3,'f1','123456','Finance'),(4,'c1','123456','StockClerk');
/*!40000 ALTER TABLE `useraccounts` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-16  1:10:54
