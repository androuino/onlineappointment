CREATE TABLE `appointments` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`first_name` VARCHAR(100),
`last_name` VARCHAR(100),
`email` VARCHAR(100),
`contact_number` VARCHAR(20),
`appointment_type` VARCHAR(100) NOT NULL,
`appointment_date` VARCHAR(100) NOT NULL);
CREATE INDEX byfirst ON appointments(`first_name`);
CREATE INDEX bylast ON appointments(`last_name`);

INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Sarah",        "Harrison",     "email@gmail.com", "080-1000-0000", "passport", "2021-5-3 09:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("John",         "Grove",        "email@gmail.com", "080-1100-0000", "rob",      "2021-5-4 10:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Retta",        "Ausherman",    "email@gmail.com", "080-1200-0000", "visa",     "2021-5-5 10:30");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Mina",         "Churchwell",   "email@gmail.com", "080-1300-0000", "passport", "2021-5-6 09:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Kala",         "Gilliland",    "email@gmail.com", "080-1400-0000", "passport", "2021-5-7 11:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Tamie",        "Grace",        "email@gmail.com", "080-1500-0000", "rob",      "2021-5-9 12:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Tom",          "Hiller",       "email@gmail.com", "080-1600-0000", "passport", "2021-5-10 09:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Mike",         "Pedersen",     "email@gmail.com", "080-1700-0000", "visa",     "2021-5-11 10:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Bridgette",    "Korus",        "email@gmail.com", "080-1800-0000", "visa",     "2021-5-12 10:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Dorian",       "Cieslak",      "email@gmail.com", "080-1900-0000", "passport", "2021-5-13 10:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Ehtel",        "Mena",         "email@gmail.com", "080-1910-0000", "visa",     "2021-5-14 11:30");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Deena",        "Beddingfield", "email@gmail.com", "080-1920-0000", "passport", "2021-5-17 09:20");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Nanci",        "Tempah",       "email@gmail.com", "080-1930-0000", "visa",     "2021-5-18 09:40");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Maranda",      "Snow",         "email@gmail.com", "080-1940-0000", "rob",      "2021-5-19 10:20");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Colin",        "Lymon",        "email@gmail.com", "080-1950-0000", "rob",      "2021-5-20 11:20");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Leonila",      "Wein",         "email@gmail.com", "080-1960-0000", "visa",     "2021-5-21 09:00");
INSERT INTO `appointments` (`first_name`,`last_name`,`email`,`contact_number`,`appointment_type`,`appointment_date`) VALUES("Lien",         "Dileo",        "email@gmail.com", "080-1970-0000", "passport", "2021-5-24 09:00");

CREATE TABLE `auth` (
`user` VARCHAR(20) PRIMARY KEY NOT NULL,
`pass` VARCHAR(60)
);
INSERT INTO `auth` (`user`,`pass`) VALUES ("admin", "UCDMOzHjLRTMaCbBSkvfLOzF/5CWhDivPGUWYGucl57tXlgCH/atu");