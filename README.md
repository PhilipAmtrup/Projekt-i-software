# Projekt-i-software
These are steps to download and get the database to work. (Mac) 

1. Install Docker desktop from this link: https://docs.docker.com/desktop/install/mac-install/
2. Then download mariaDB from this link: https://downloads.mariadb.org/mariadb/10.3.12/, then run this command in the terminal: docker pull mariadb:10.6
3. Then create a MariaDB container instance, (this is matched to work with this project):
   docker run --name localhost -e MYSQL_ROOT_PASSWORD=Password -
   p 3306:3306 -d docker.io/library/mariadb:10.6
4. Then download MySQL: https://downloads.mysql.com/archives/workbench/
5. Then configure the MySQL, with the data in Connector.java
