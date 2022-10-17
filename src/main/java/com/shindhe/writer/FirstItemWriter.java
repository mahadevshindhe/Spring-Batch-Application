package com.shindhe.writer;

import com.shindhe.model.StudentCsv;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstItemWriter implements ItemWriter<StudentCsv> {
    @Override
    public void write(List<? extends StudentCsv> list) throws Exception {
        System.out.println("Inside Item Writer");
        list.stream().forEach(System.out::println);
    }
}
