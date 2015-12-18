create table if not exists Tasks(
	id serial primary key,
	title text not null unique,
	labor_vol integer not null,
	status text not null check(status in ('Выполнено','Запланировано')),
	readyday date check(status = 'Выполнено' or null) 
	);

create table if not exists Days(
	id serial primary key,
	workday date not null unique,
	labor_vol int not null
);
