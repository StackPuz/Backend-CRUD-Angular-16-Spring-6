# Boilerplate-CRUD-Angular-16-Spring 6
Boilerplate CRUD Web App created with Angular 16 + Spring 6 by [StackPuz](https://stackpuz.com).

## Demo
Checkout the live demo at https://demo-spa.stackpuz.com

## Features
- Fully Responsive Layout.
- Sorting, Filtering and Pagination on Data List.
- User Management, User Authentication and Authorization, User Profile, Reset Password.
- Input Mask and Date Picker for date and time input field with Form Validation.

![Responsive Layout](https://stackpuz.com/img/feature/responsive.gif)  
![Data List](https://stackpuz.com/img/feature/list.gif)  
![User Module](https://stackpuz.com/img/feature/user.png)  
![Input Mask and Date Picker](https://stackpuz.com/img/feature/date.gif)

## Minimum requirements
- Maven 3.5.0
- Node.js 14.18
- JAVA 17
- MySQL 5.7

## Installation
1. Clone this repository. `git clone https://github.com/stackpuz/Boilerplate-CRUD-Angular-16-Spring-6.git .`
2. Change directory to Angular project. `cd angular`
3. Install the Angular dependencies. `npm install`
4. Create a new database and run [/database.sql](/database.sql) script to create tables and import data.
5. Edit the database configuration in [/spring_api/src/main/webapp/WEB-INF/app-servlet.xml](/spring_api/src/main/webapp/WEB-INF/app-servlet.xml) file.
    ```
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://127.0.0.1:3306/testdb?characterEncoding=UTF-8&amp;useSSL=false" />
        <property name="username" value="root" />
        <property name="password" value="" />
    </bean>
    ```

## Run project

1. Run Angular project. `npm start`
2. Run Spring project. `mvn package cargo:run`
3. Navigate to `http://localhost:4200`
4. Login with user `admin` password `1234`

## Customization
To customize this project to use other Database Engines, CSS Frameworks, Icons, Input Mask, Date picker, Date format, Font and more. Please visit [stackpuz.com](https://stackpuz.com).

## License

[MIT](https://opensource.org/licenses/MIT)