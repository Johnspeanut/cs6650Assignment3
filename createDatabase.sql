USE databaseskier;

CREATE TABLE LiftRides(
postId int auto_increment,
resortId int,
seasonId int,
dayId int,
skierId int,
takeTime int,
liftId int,
constraint pk_postId primary key(postId));


USE databaseresort;

CREATE TABLE Resorts(
postId int auto_increment,
resortId int,
seasonId int,
dayId int,
skierId int,
takeTime int,
liftId int,
constraint pk_postId primary key(postId));

