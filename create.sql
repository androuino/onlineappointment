CREATE TABLE `appointments` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`firstName` VARCHAR(100),
`lastName` VARCHAR(100),
`email` VARCHAR(100),
`contactNumber` VARCHAR(20),
`apptType` VARCHAR(100) NOT NULL,
`apptDate` VARCHAR(100) NOT NULL);
CREATE INDEX byfirst ON appointments(`firstName`);
CREATE INDEX bylast ON appointments(`lastName`);

INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Sarah",        "Harrison",     "email@gmail.com", "08010000000", "passport", "2021-05-03T09:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("John",         "Grove",        "email@gmail.com", "08011000000", "rob",      "2021-05-04T10:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Retta",        "Ausherman",    "email@gmail.com", "08012000000", "visa",     "2021-05-05T10:30");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Mina",         "Churchwell",   "email@gmail.com", "08013000000", "passport", "2021-05-06T09:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Kala",         "Gilliland",    "email@gmail.com", "08014000000", "passport", "2021-05-07T11:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Tamie",        "Grace",        "email@gmail.com", "08015000000", "rob",      "2021-05-09T12:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Tom",          "Hiller",       "email@gmail.com", "08016000000", "passport", "2021-05-10T09:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Mike",         "Pedersen",     "email@gmail.com", "08017000000", "visa",     "2021-05-11T10:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Bridgette",    "Korus",        "email@gmail.com", "08018000000", "visa",     "2021-05-12T10:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Dorian",       "Cieslak",      "email@gmail.com", "08019000000", "passport", "2021-05-13T10:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Ehtel",        "Mena",         "email@gmail.com", "08019100000", "visa",     "2021-05-14T11:30");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Deena",        "Beddingfield", "email@gmail.com", "08019200000", "passport", "2021-05-17T09:20");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Nanci",        "Tempah",       "email@gmail.com", "08019300000", "visa",     "2021-05-18T09:40");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Maranda",      "Snow",         "email@gmail.com", "08019400000", "rob",      "2021-05-19T10:20");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Colin",        "Lymon",        "email@gmail.com", "08019500000", "rob",      "2021-05-20T11:20");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Leonila",      "Wein",         "email@gmail.com", "08019600000", "visa",     "2021-05-21T09:00");
INSERT INTO `appointments` (`firstName`,`lastName`,`email`,`contactNumber`,`apptType`,`apptDate`) VALUES("Lien",         "Dileo",        "email@gmail.com", "08019700000", "passport", "2021-05-24T09:00");

CREATE TABLE `auth` (
`user` VARCHAR(20) PRIMARY KEY NOT NULL,
`pass` VARCHAR(60)
);
INSERT INTO `auth` (`user`,`pass`) VALUES ("admin", "UCDMOzHjLRTMaCbBSkvfLOzF/5CWhDivPGUWYGucl57tXlgCH/atu");