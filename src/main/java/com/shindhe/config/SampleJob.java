package com.shindhe.config;

import com.shindhe.model.StudentCsv;
import com.shindhe.model.StudentJdbc;
import com.shindhe.model.StudentJson;
import com.shindhe.model.StudentXml;
import com.shindhe.processor.FirstItemProcessor;
import com.shindhe.reader.FirstItemReader;
import com.shindhe.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class SampleJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private FirstItemReader itemReader;

    @Autowired
    private FirstItemProcessor itemProcessor;

    @Autowired
    private FirstItemWriter itemWriter;

    @Autowired
    private DataSource datasource;



    @Bean
    public Job chunkJob() {
        return jobBuilderFactory.get("First Job")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .build();
    }

    private Step firstChunkStep() {
        return stepBuilderFactory.get("First Chunk Step")
                .<StudentJdbc, StudentJdbc>chunk(3)
//                .reader(flatFileItemReader(null))
//                .reader(flatFileItemReader(null))
//                .reader(staxEventItemReader(null))
                .reader(jdbcCursorItemReader())
//                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    /*@StepScope
    @Bean
    public FlatFileItemReader<StudentCsv> flatFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource ) {
        FlatFileItemReader<StudentCsv> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(fileSystemResource);
        *//*flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("ID", "First Name", "Last Name", "Email");
                    }
                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
                    {
                        setTargetType(StudentCsv.class);
                    }
                });
            }
        });*//*

        DefaultLineMapper<StudentCsv> defaultLineMapper =
                new DefaultLineMapper<StudentCsv>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");

        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper =
                new BeanWrapperFieldSetMapper<StudentCsv>();
        fieldSetMapper.setTargetType(StudentCsv.class);

        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        flatFileItemReader.setLineMapper(defaultLineMapper);

        flatFileItemReader.setLinesToSkip(1);
        return flatFileItemReader;
    }*/

    @StepScope
    @Bean
    public JsonItemReader<StudentJson> flatFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource ) {
        JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<StudentJson>();
        jsonItemReader.setResource(fileSystemResource);
        jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader(StudentJson.class));
        return jsonItemReader;
    }

    @StepScope
    @Bean
    public StaxEventItemReader<StudentXml>  staxEventItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource){
        StaxEventItemReader<StudentXml> reader = new StaxEventItemReader<>();
        reader.setResource(fileSystemResource);
        reader.setFragmentRootElementName("student");
        reader.setUnmarshaller(new Jaxb2Marshaller(){
            {
                setClassesToBeBound(StudentXml.class);
            }
        });
        return reader;
    }

    public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
        JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader =
                new JdbcCursorItemReader<StudentJdbc>();

        jdbcCursorItemReader.setDataSource(datasource);
        jdbcCursorItemReader.setSql(
                "select id, first_name as firstName, last_name as lastName,"
                        + "email from student");

        jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
            {
                setMappedClass(StudentJdbc.class);
            }
        });
//        jdbcCursorItemReader.setCurrentItemCount(5); //start reading from 6th record
//        jdbcCursorItemReader.setMaxItemCount(10);
        return jdbcCursorItemReader;
    }
}


//run application with arguments inputFile=C:\Users\WA661DW\Downloads\Spring-Batch-Application\InputFiles\students.csv