-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: image_gallery_database
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(128) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `creator_username` varchar(45) DEFAULT NULL,
  `ordering` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `creator_username_idx` (`creator_username`),
  CONSTRAINT `album_username` FOREIGN KEY (`creator_username`) REFERENCES `user` (`username`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (10,'Mi piacciono gli animali :)','2022-06-20 00:31:34','user1',0),(12,'Buongiorno mondo non mi piacciono gli animali','2022-06-20 10:43:21','user3',0),(15,'album for hating animals','2022-06-20 12:29:50','animalHater87',0),(16,'My favourite animals','2022-06-21 21:50:55','user1',0),(17,'my otters','2022-06-27 19:31:05','OtterLover',0),(18,'Beautiful buildings','2022-06-27 20:21:49','animalHater87',0),(19,'Sono così belle che ho deciso di dedicare loro un altro album','2022-06-27 21:59:34','OtterLover',0),(20,'Stunning birds','2022-06-29 12:41:15','user1',0),(21,'Fantasy creatures','2022-06-29 12:41:57','user1',0),(22,'Quadrupeds','2022-06-29 12:42:17','user1',0);
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `progressive` int NOT NULL AUTO_INCREMENT,
  `image_id` int NOT NULL,
  `user` varchar(128) DEFAULT NULL,
  `text` varchar(10000) DEFAULT NULL,
  PRIMARY KEY (`progressive`,`image_id`),
  KEY `user_commente_idx` (`user`),
  KEY `image_comment_idx` (`image_id`),
  CONSTRAINT `image_comment` FOREIGN KEY (`image_id`) REFERENCES `image` (`ID`),
  CONSTRAINT `user_commente` FOREIGN KEY (`user`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,2,'user1','che bel serval :)'),(6,3,'user1','ma quanto è carina no dimmi tu se non è bellissima\r\n'),(7,3,'user2','hai proprio ragione user1 è un animale fantastico'),(9,7,'user1','l\'upupa è l\'unico animale con la u che conoscevo alle elementari'),(10,6,'user1','gotta go fast'),(11,4,'user1','ferrets*\r\n'),(12,7,'animalHater87','i hate animals grrrrr'),(13,4,'animalHater87','i hate them too'),(14,4,'animalHater87','i really do'),(17,5,'user1','you don\'t want to see them walking on all fours'),(18,17,'user1','What a meanie >:('),(19,17,'OtterLover','Needs more otters'),(20,16,'user3','Meh'),(21,16,'user1','Molto carina :)'),(22,12,'user1','È forse l\'uccello più maestoso che abbia mai visto'),(23,12,'OtterLover','Non è una lontra però capisco perchè possa piacere!'),(24,4,'OtterLover','CHE BELLE! Sembrano quasi lontre!'),(25,9,'user3',')\";'),(26,9,'user1','a fantasy creature indeed O.O');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `containment`
--

DROP TABLE IF EXISTS `containment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `containment` (
  `image_id` int NOT NULL,
  `album_id` int NOT NULL,
  PRIMARY KEY (`image_id`,`album_id`),
  KEY `album_containing_idx` (`album_id`),
  CONSTRAINT `album_containing` FOREIGN KEY (`album_id`) REFERENCES `album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `image_contained` FOREIGN KEY (`image_id`) REFERENCES `image` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `containment`
--

LOCK TABLES `containment` WRITE;
/*!40000 ALTER TABLE `containment` DISABLE KEYS */;
INSERT INTO `containment` VALUES (2,10),(3,10),(4,10),(5,10),(6,10),(7,10),(8,10),(9,10),(10,10),(11,10),(12,10),(3,16),(4,16),(15,17),(16,17),(17,18),(18,18),(15,19),(16,19),(7,20),(11,20),(12,20),(9,21),(12,21),(2,22),(3,22),(4,22),(5,22),(6,22),(8,22),(9,22);
/*!40000 ALTER TABLE `containment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `path` varchar(512) NOT NULL,
  `title` varchar(128) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `uploader_username` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `path_UNIQUE` (`path`),
  KEY `uploader_username_idx` (`uploader_username`),
  CONSTRAINT `image_user` FOREIGN KEY (`uploader_username`) REFERENCES `user` (`username`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,'images/animals/PineMarten1.jpg','HE STOLE MY PIZZA','2022-06-29 20:17:23','Someone stop that scoundrel!','ciao'),(2,'images/animals/Serval1.jpg','Serval walking','2022-06-16 18:54:45','A serval walking on dirt','user1'),(3,'images/animals/Fox1.jpg','Fox :)','2022-06-20 00:52:55','An orange and grey fox overlooking a grassy field','user1'),(4,'images/animals/Ferret1.jpg','<3','2022-06-20 10:28:25','Two ferret shaped like a heart','user1'),(5,'images/animals/Sunbear1.jpg','Praise the sun','2022-06-20 10:29:06','A sun bear sitting','user1'),(6,'images/animals/Hedgehog1.jpg','Spiky','2022-06-20 10:29:50','A close-up of a hedgehog','user1'),(7,'images/animals/Hoopoe1.jpg','Orange','2022-06-20 10:31:01','A hoopoe perching on a branch, with its head feathers spread out','user1'),(8,'images/animals/Badger1.webp','B&W','2022-06-27 19:11:17','A badger curiously looking in front of them','user1'),(9,'images/animals/Dog1.jpg','Lavender','2022-06-27 19:11:57','Golden retriever standing in a lavender field','user1'),(10,'images/animals/GreySquirrel1.jpg','Chubby','2022-06-27 19:14:42','A specimen of american grey squirrel, that\'s unfortunately endangering the native european red squirrel with its invasive presence in the european territory','user1'),(11,'images/animals/Hummingbird1.jpg','Stunning colors','2022-06-27 19:15:51','A hummingbird feeding off of the nectar of a flower','user1'),(12,'images/animals/Nightjar1.jpg','Dragon','2022-06-27 19:17:20','This nightjar is probably the closest we\'ll ever get to a real dragon','user1'),(13,'images/animals/Lynx1.jpg','Pointy','2022-06-27 19:20:06','I\'ve always liked the shape of their ears','user2'),(14,'images/animals/Moth1.jpg','Candy','2022-06-27 19:20:28','It looks like a piece of candy!','user2'),(15,'images/animals/Otter1.jpg','Wet','2022-06-27 19:30:15','Look at that fur!','OtterLover'),(16,'images/animals/Otter2.jpg','Not as wet','2022-06-27 19:30:57','I like these the most, look at that face','OtterLover'),(17,'images/buildings/Brutalism1.jpg','Brutalism','2022-06-27 20:16:02','Not a sliver of nature, just how i like it','animalHater87'),(18,'images/buildings/Brutalism2.webp','Concrete','2022-06-27 20:16:47','To whomever says that buildings are just boring rectangles','animalHater87'),(19,'images/animals/PineMarten2.jpg','Pine marten','2022-06-29 20:17:23','A beautiful pine marten in the wild.','ciao'),(20,'images/animals/SnowLeopard1.jpg','Snow leopard','2022-06-29 20:17:23','A snow leopard looking into the photographer\'s camera.','ciao'),(21,'images/animals/SugarGlider1.jpg','Sugar glider','2022-06-29 20:17:23','What a nice little... weird... mouse.','ciao');
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `username` varchar(128) NOT NULL,
  `email` varchar(128) DEFAULT NULL,
  `password` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('animalHater87','ihateanimals@gmail.com','buongiorno'),('ciao','ciaooidn@gmail.com','123'),('OtterLover','otters@acquario.genova','lontra'),('user1','mail1@polimi.it','pass1'),('user2','mail2@polimi.it','pass2'),('user3','mail3@polimi.it','pass3');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-29 20:20:40
