# aggregate-persistence

## Prepare MySQL

Run MySQL in Docker

```sbtshell
docker run --name mysql -v /data/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpwd -d -p 3306:3306 mysql:5
```

Enter MySQL command line:

```sbtshell
docker exec -it mysql /usr/bin/mysql -u root --password=rootpwd
```

Create Database and user:

```sql
CREATE DATABASE IF NOT EXISTS dddproject character set UTF8mb4 collate utf8mb4_bin;

GRANT ALL ON dddproject.* TO 'ddduser'@'%' IDENTIFIED BY 'dddpassword';
GRANT ALL ON dddproject.* TO 'ddduser'@'localhoslst' IDENTIFIED BY 'dddpassword';
```

Login use the new user `ddduser`

```sbtshell
docker exec -it mysql /usr/bin/mysql -u ddduser --password=dddpassword
```

Create User Table:

```sql
create table user (
  id varchar(10) not null,
  name varchar(40) not null,
  phone varchar(20),
  address varchar(200),
  primary key(id)
);
```




