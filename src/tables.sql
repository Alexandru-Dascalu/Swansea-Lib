BEGIN TRANSACTION;
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
	`username`	TEXT NOT NULL UNIQUE,
	`firstName`	TEXT,
	`lastName`	TEXT,
	`telephone`	TEXT,
	`address`	TEXT,
	`postcode`	TEXT,
	`avatarPath`	TEXT,
	`accountBalance`	TEXT,
	`lastLogin`	TEXT,
	PRIMARY KEY(`username`)
);
INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance') VALUES ('Alexandru','Alex','Dascalu','079999999','3 Maple Street','SA2 8PP','/SavedAvatars/Avatar1.png','0');
INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance')  VALUES ('Manny','Jake','Manford','0108097352','6 Main Street','SA2 GFY','/SavedAvatars/Avatar1.png','0');
INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance')  VALUES ('Steveo','Steve','Jamerson','0108035473','17 Lil Line','SA2 HRU','/SavedAvatars/Avatar1.png','0');
INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance')  VALUES ('Queeny','McNiel','Nelson','0108037642','45 Avenue Drive','SA2 HFY','/SavedAvatars/Avatar1.png','0');
INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance')  VALUES ('Jackie','Janet','Smith','0108034627','16 Cramford Way','SA2 I9L','/SavedAvatars/Avatar1.png','0');

INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance')  VALUES ('Helper1','Carl','Walker','0108098743','7 Low Street','SA2 HFS','/SavedAvatars/Avatar1.png','0');
INSERT INTO `users`('username','firstname','lastname','telephone','address','postcode','avatarPath','accountBalance')  VALUES ('Manager1','Ben','Dover','0108034738','9 High street','SA2 IFA','/SavedAvatars/Avatar1.png','0');
DROP TABLE IF EXISTS `staff`;
CREATE TABLE IF NOT EXISTS `staff` (
	`username`	TEXT,
	`staffId`	INTEGER NOT NULL UNIQUE,
	`employmentDate`	TEXT,
	FOREIGN KEY(`username`) REFERENCES `users`(`username`)
);

INSERT INTO `staff` VALUES ('Helper1','1','03/11/2018');
INSERT INTO `staff` VALUES ('Manager1','2','21/02/2016');

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
	`transactionId`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	`username`	TEXT,
	`paid`	REAL,
	`dateTime`	TEXT,
	FOREIGN KEY(`username`) REFERENCES `users`(`username`)
);
INSERT INTO `transactions` VALUES (1,'test',5.0,"sometime");
INSERT INTO `transactions` VALUES (2,'test',10.0,"othertime");

DROP TABLE IF EXISTS `system`;
CREATE TABLE IF NOT EXISTS `system` (
	`ver`	INTEGER
);
INSERT INTO `system` VALUES (16);

DROP TABLE IF EXISTS `resource`;
CREATE TABLE IF NOT EXISTS `resource` (
	`rID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`title`	TEXT,
	`year`  INTEGER,
	`thumbnail`	TEXT,
	`timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (1,'Homo Deus',2017,'/graphics/homoDeus.png');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (2,'Iron Man',2008,'/graphics/ironMan.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (3,'Captain America: The Winter Soldier',2014,'/graphics/winterSoldier.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (4,'Thor',2011,'/graphics/thor.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (5,'Creed II',2018,'/graphics/creed2.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (6,'Creed',2015,'/graphics/creed.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (7,'The Martian',2015,'/graphics/theMartian.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (8,'Sapiens',2015,'/graphics/sapiens.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (9,'Waking Gods',2015,'/graphics/wakingGods.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (10,'American Gods',2015,'/graphics/americanGods.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (11,'Iron Man 3',2013,'/graphics/ironMan3.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (12,'Lenovo',2017,'/graphics/laptop.jpg');

INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (13,'Counter-Strike: Global Offensive',2012,'/graphics/csgo.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (14,'Grand Theft Auto V',2013,'/graphics/gta-v.jpg');
INSERT INTO `resource`(rID,title,year,thumbnail) VALUES (15,'Fortnite',2017,'/graphics/fortnite.jpg');


DROP TABLE IF EXISTS `fines`;
CREATE TABLE IF NOT EXISTS `fines` (
	`fineID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	`username`	TEXT,
	`rID`	INTEGER,
	`daysOver`	INTEGER,
	`amount`	REAL,
	`dateTime`	TEXT,
	`paid`	INTEGER,
	`timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY(`username`) REFERENCES `users`(`username`),
	FOREIGN KEY(`rID`) REFERENCES `resource`(`rID`)
);
INSERT INTO `fines` VALUES (1,"Manny",1,3,5.0,'hi',0,"2019-03-10 11:01:01");
INSERT INTO `fines` VALUES (2,"Steveo",2,100,10.0,'always',0,"2019-03-10 01:01:01");
INSERT INTO `fines` VALUES (3,"Manny",1,3,5.0,'hi',1,"2019-03-10 01:01:01");
INSERT INTO `fines` VALUES (4,"Steveo",2,100,10.0,'always',1,"2019-03-10 01:01:01");
INSERT INTO `fines` VALUES (5,"Manny",1,3,5.0,'hi',1,"2019-03-10 01:01:01");
INSERT INTO `fines` VALUES (6,"Steveo",2,100,10.0,'always',1,"2019-03-10 01:01:01");
INSERT INTO `fines` VALUES (7,"Manny",1,3,5.0,'hi',1,"2019-03-10 01:01:01");
INSERT INTO `fines` VALUES (8,"Steveo",2,100,10.0,'always',1,"2019-03-10 01:01:01");

DROP TABLE IF EXISTS `book`;
CREATE TABLE IF NOT EXISTS `book` (
	`author`	TEXT,
	`publisher`	TEXT,
	`genre`		TEXT,
	`ISBN`		TEXT,
	`language`	TEXT,
	`rID`		INTEGER,
	PRIMARY KEY (rID),
	FOREIGN KEY (rID) REFERENCES `resource`(`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `book` VALUES ('Yuval Noah Harari','Harvill Secker','Non-Fiction','978-191-070-187-4','English',1);
INSERT INTO `book` VALUES ('Andy Weir','Harvill Secker','Fiction','978-191-070-187-5','English',7);
INSERT INTO `book` VALUES ('Yuval Noah Harari','Harvill Secker','Non-Fiction','978-191-070-187-5','English',8);
INSERT INTO `book` VALUES ('Unknown Author','Harvill Secker','Fiction','978-191-070-187-6','English',9);
INSERT INTO `book` VALUES ('Neil Gaimen','Harvill Secker','Fiction','978-191-070-187-7','English',10);

DROP TABLE IF EXISTS `game`;
CREATE TABLE IF NOT EXISTS `game` (
	`publisher`		TEXT,
	`genre`			TEXT,
	`rating`		TEXT,
	`multiplayer` 	TEXT,
	`rID`			INTEGER,
	PRIMARY KEY (rID),
	FOREIGN KEY (rID) REFERENCES `resource`(`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `game` VALUES ('Valve','First-Person Shooter','18+',"Yes",13);
INSERT INTO `game` VALUES ('Rockstar Games','Action-Adventure','18+',"Yes",14);
INSERT INTO `game` VALUES ('Epic Games','Battle Royale','12',"Yes",15);

DROP TABLE IF EXISTS `events`;
CREATE TABLE IF NOT EXISTS `events` (
	`eID`		INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	`title`		TEXT,
	`details`	TEXT,
	`date`		TEXT,
	`maxAllowed` INTEGER
);

INSERT INTO `events` VALUES (1,'Zilele ASMI','Jocuri sociale si training ASMI','2019-04-18 13:00', 30);
INSERT INTO `events` VALUES (2,'Cardiff Varsity Cup Theft','We are going to sneak into Cardiff University 
and steal the Varsity Cup and Shield and escape to 
bring it back to Swanse. We just need 20 good men.
#FuckCardiff','2019-04-16 11:00', 20);

DROP TABLE IF EXISTS `dvd`;
CREATE TABLE IF NOT EXISTS `dvd` (
	`director`	TEXT,
	`runtime`	INTEGER,
	`language`	TEXT,
	`rID`		INTEGER,
	PRIMARY KEY (rID),
	FOREIGN KEY (rID) REFERENCES `resource`(`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `dvd` VALUES ('Jon Favreau',126,'english',2);
INSERT INTO `dvd` VALUES ('Joe Russo',136,'english',3);
INSERT INTO `dvd` VALUES ('Kenneth Branagh',115,'english',4);
INSERT INTO `dvd` VALUES ('Steven Caple Jr',130,'english',5);
INSERT INTO `dvd` VALUES ('Ryan Coogler',133,'english',6);
INSERT INTO `dvd` VALUES ('Jon Favreau',131,'english',11);

drop table if exists `subtitles`;
create table if not exists `subtitles` (
	`dvdID` integer,
	`subtitleLanguage` TEXT,
	primary key (dvdID,subtitleLanguage),
	foreign key (dvdID) references `dvd`(`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

drop table if exists `userEvents`;
create table if not exists `userEvents` (
	`eID` INTEGER,
	`username` TEXT,
	primary key (eID, username),
	foreign key (username) references `users`(`username`) ON UPDATE CASCADE ON DELETE CASCADE
);


INSERT INTO `subtitles` VALUES (1,'romanian');
INSERT INTO `subtitles` VALUES (1,'greek');
INSERT INTO `subtitles` VALUES (2,'welsh');

DROP TABLE IF EXISTS `laptop`;
CREATE TABLE IF NOT EXISTS `laptop` (
	`manufacturer`	TEXT,
	`model`			INTEGER,
	`os`			TEXT,
	`rID`			INTEGER,
	PRIMARY KEY (rID),
	FOREIGN KEY (rID) REFERENCES `resource`(`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `laptop` VALUES ('Lenovo',1,'Windows `10',12);

DROP TABLE IF EXISTS `copies`;
CREATE TABLE IF NOT EXISTS `copies` (
	`copyID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	`rID`	INTEGER NOT NULL,
	`keeper`	TEXT,
	loanDuration INTEGER,
	borrowDate TEXT,
	lastRenewal TEXT,
	dueDate TEXT,
	FOREIGN KEY(`keeper`) REFERENCES `users`(`username`),
	FOREIGN KEY(`rID`) REFERENCES `resource`(`rID`)
);

INSERT INTO `copies` VALUES (1,1,'Manny',8,'09/12/2018',NULL,NULL);
INSERT INTO `copies` VALUES (3,1,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (4,1,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (5,1,NULL,7,NULL,NULL,NULL);

INSERT INTO `copies` VALUES (6,2,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (7,2,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (8,2,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (9,2,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (10,2,NULL,7,NULL,NULL,NULL);

INSERT INTO `copies` VALUES (11,3,'Manny',7,'09/12/2018',NULL,NULL);
INSERT INTO `copies` VALUES (12,3,NULL,6,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (13,3,NULL,5,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (14,3,NULL,4,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (15,3,NULL,3,NULL,NULL,NULL);

INSERT INTO `copies` VALUES (16,4,NULL,8,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (17,4,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (18,4,NULL,6,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (19,4,NULL,5,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (20,4,NULL,4,NULL,NULL,NULL);

INSERT INTO `copies` VALUES (21,5,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (22,5,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (23,5,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (24,5,NULL,7,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (25,5,NULL,7,NULL,NULL,NULL);

INSERT INTO `copies` VALUES (26,12,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (27,7,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `copies` VALUES (28,8,NULL,NULL,NULL,NULL,NULL);

DROP TABLE IF EXISTS `borrowRecords`;
CREATE TABLE IF NOT EXISTS `borrowRecords` (
	`borrowId`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	`copyId`	INTEGER NOT NULL,
	`username`	TEXT NOT NULL,
	`description`	TEXT,
	`timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (copyId) REFERENCES `copies`(`copyID`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (username) REFERENCES `users`(`username`) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (1,1,'Manny','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (2,11,'Manny','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (3,10,'Jackie','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (4,10,'Steveo','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (5,10,'Manny','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (6,1,'Jackie','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (7,26,'Manny','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (8,27,'Steveo','Returned on 08/12/2018');
INSERT INTO `borrowRecords`('borrowId','copyId','username','description') VALUES (9,28,'Jackie','Returned on 08/12/2018');

DROP TABLE IF EXISTS `requestsToApprove`;
CREATE TABLE IF NOT EXISTS `requestsToApprove` (
	`rID` INTEGER,
	`userName` TEXT,
	`timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (userName,rID),
	FOREIGN KEY (userName) REFERENCES `users` (`username`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (rID) REFERENCES `resource` (`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

DROP TABLE IF EXISTS `userRequests`;
CREATE TABLE IF NOT EXISTS `userRequests` (
	`rID` INTEGER,
	`userName` TEXT,
	`orderNumber` INTEGER,
	PRIMARY KEY (userName,rID),
	FOREIGN KEY (userName) REFERENCES `users` (`username`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (rID) REFERENCES `resource` (`rID`) ON UPDATE CASCADE ON DELETE CASCADE
);

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE IF NOT EXISTS `reviews` (
	`reviewId`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
	`resourceId`	INTEGER,
	`username`	TEXT,
	`star`	INTEGER,
	`review`	TEXT,
	`timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
		FOREIGN KEY (resourceId) REFERENCES `resource` (`rID`) ON UPDATE CASCADE ON DELETE CASCADE,
		FOREIGN KEY (username) REFERENCES 'users' (`username`) ON UPDATE CASCADE ON DELETE CASCADE
);
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (1, 2, 'Jackie', 1, 'Massive oof');
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (2, 2, 'Steveo', 5, 'Square root of oof');
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (3, 2, 'Manny', 3, 'Just a weird flex but ok');
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (4, 1, 'Jackie', 5, 'Book of the century');
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (5, 12, 'Manny', 2, 'Too cold');
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (6, 7, 'Steveo', 4, 'Incredible');
INSERT INTO `reviews` (reviewId, resourceId, username, star, review) VALUES (7, 8, 'Jackie', 3, 'Ok but could do with more mayo');

DROP TABLE IF EXISTS `majorStat`;
CREATE TABLE `majorStat` (
	`statId`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
	`resource`	INTEGER NOT NULL,
	`timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (resource) REFERENCES `resource` (`rID`) ON UPDATE CASCADE ON DELETE CASCADE);


COMMIT;
