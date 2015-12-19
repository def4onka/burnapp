create table if not exists tasks(
	id serial primary key,
	title text not null unique,
	note text,
	labor_vol integer not null,
	status text not null check(status in ('Выполнено','Запланировано')),
	readyday date check(status = 'Выполнено' and readyday is not null or status = 'Запланировано' and readyday is null)
);

create table if not exists workdays(
	id serial primary key,
	workday date not null unique,
	labor_vol int not null
);

create table if not exists sprintdates(
	id serial primary key,
	begindate date not null,
	enddate date not null);
