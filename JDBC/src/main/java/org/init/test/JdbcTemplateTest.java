package org.init.test;

import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.core.JdbcTemplate;
import org.init.core.io.ClassPathResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcTemplateTest {
    public static void main(String[] args) throws BeansException, ClassNotFoundException, SQLException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("beans.xml");
        JdbcTemplate jdbcTemplate = (JdbcTemplate) beanFactory.getBean("jdbcTemplate");
        //Connection connection = jdbcTemplate.getDataSource().getConnection();
        //Statement statement = connection.createStatement();
        //statement.execute("insert into user(username,age,gender) values('666',18,'dd')");
        jdbcTemplate.query("select * from user", (rs) -> {
            System.out.println(rs.getString(2));
        });
    }
}
