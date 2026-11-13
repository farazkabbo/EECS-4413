package ca.yorku.eecs4413.fritzev; 

import org.junit.jupiter.api.Test; 
import org.springframework.core.io.ClassPathResource; 
import org.springframework.jdbc.datasource.DriverManagerDataSource; 
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator; 
import org.springframework.jdbc.core.JdbcTemplate; 

import static org.junit.jupiter.api.Assertions.assertEquals; 

class CatalogSeedSqlTests { 

    @Test 
    void supplementaryCatalogSeedRunsAfterMasterSeed() { 
        DriverManagerDataSource dataSource = new DriverManagerDataSource( 
                "jdbc:h2:mem:catalogSeedTest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1", "sa", ""); 
        new ResourceDatabasePopulator( 
                new ClassPathResource("schema.sql"), 
                new ClassPathResource("data.sql"), 
                new ClassPathResource("catalog-seed.sql")).execute(dataSource); 
        JdbcTemplate jdbc = new JdbcTemplate(dataSource); 
        assertEquals(26, jdbc.queryForObject("select count(*) from vehicles", Integer.class)); 
        assertEquals(12, jdbc.queryForObject( 
                "select count(*) from vehicles where id between 1001 and 1012", Integer.class)); 
        assertEquals(48, jdbc.queryForObject( 
                "select count(*) from vehicle_options where vehicle_id between 1001 and 1012", Integer.class)); 
    } 
} 
