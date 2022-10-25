package com.shindhe.config;

import com.shindhe.model.*;
import com.shindhe.processor.FirstItemProcessor;
import com.shindhe.reader.FirstItemReader;
import com.shindhe.service.StudentService;
import com.shindhe.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

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
    private StudentService studentService;


    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource(){
        return DataSourceBuilder.create().build();
    };

    @Bean
    @ConfigurationProperties(prefix = "spring.universitydatasource")
    public DataSource universityDatasource(){
        return DataSourceBuilder.create().build();
    };

    @Bean
    public Job chunkJob() {
        return jobBuilderFactory.get("First Job")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .build();
    }

    private Step firstChunkStep() {
        return stepBuilderFactory.get("First Chunk Step")
                .<StudentCsv, StudentCsv>chunk(3)
                .reader(flatFileItemReader(null))
//                .reader(flatFileItemReader(null))
//                .reader(staxEventItemReader(null))
//                .reader(jdbcCursorItemReader())
//                .reader(itemReaderAdapter())
//                .processor(itemProcessor)
//                .writer(jsonFileItemWriter(null))
//                .writer(staxEventItemWriter(null))
                . writer(jdbcBatchItemWriter())

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
    public FlatFileItemReader<StudentCsv> flatFileItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemReader<StudentCsv> flatFileItemReader =
                new FlatFileItemReader<StudentCsv>();

        flatFileItemReader.setResource(fileSystemResource);

        flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
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
        });

		/*
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
		*/

        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

/*    @StepScope
    @Bean
    public JsonItemReader<StudentJson> flatFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource ) {
        JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<StudentJson>();
        jsonItemReader.setResource(fileSystemResource);
        jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader(StudentJson.class));
        return jsonItemReader;
    }*/

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

        jdbcCursorItemReader.setDataSource(universityDatasource());
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

    public ItemReaderAdapter<StudentResponse> itemReaderAdapter() {
        ItemReaderAdapter<StudentResponse> itemReaderAdapter =
                new ItemReaderAdapter<StudentResponse>();

        itemReaderAdapter.setTargetObject(studentService);
        itemReaderAdapter.setTargetMethod("getStudent");
        itemReaderAdapter.setArguments(new Object[] {1L, "Test"});

        return itemReaderAdapter;
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemWriter<StudentJdbc> flatFileItemWriter =
                new FlatFileItemWriter<StudentJdbc>();

        flatFileItemWriter.setResource(fileSystemResource);

        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id,First Name,Last Name,Email");
            }
        });

        flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
            {
                //setDelimiter("|");
                setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
                    {
                        setNames(new String[] {"id", "firstName", "lastName", "email"});
                    }
                });
            }
        });

        flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("Created @ " + new Date());
            }
        });

        return flatFileItemWriter;
    }

    @StepScope
    @Bean
    public JsonFileItemWriter<StudentJson> jsonFileItemWriter(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        JsonFileItemWriter<StudentJson> jsonFileItemWriter =
                new JsonFileItemWriter<>(fileSystemResource,
                        new JacksonJsonObjectMarshaller<StudentJson>());

        return jsonFileItemWriter;
    }

    @StepScope
    @Bean
    public StaxEventItemWriter<StudentJdbc> staxEventItemWriter(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemWriter<StudentJdbc> staxEventItemWriter =
                new StaxEventItemWriter<StudentJdbc>();

        staxEventItemWriter.setResource(fileSystemResource);
        staxEventItemWriter.setRootTagName("students");

        staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentJdbc.class);
            }
        });

        return staxEventItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter =
                new JdbcBatchItemWriter<StudentCsv>();

        jdbcBatchItemWriter.setDataSource(universityDatasource());
        jdbcBatchItemWriter.setSql(
                "insert into student(id, first_name, last_name, email) "
                        + "values (:id, :firstName, :lastName, :email)");

        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());

        return jdbcBatchItemWriter;
    }
}


//run application with arguments inputFile=C:\Users\WA661DW\Downloads\Spring-Batch-Application\InputFiles\students.csv