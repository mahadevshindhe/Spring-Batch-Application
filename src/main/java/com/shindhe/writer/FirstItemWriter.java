package com.shindhe.writer;

import com.shindhe.model.StudentCsv;
import com.shindhe.model.StudentJson;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstItemWriter implements ItemWriter<StudentJson> {
    @Override
    public void write(List<? extends StudentJson> list) throws Exception {
        System.out.println("Inside Item Writer");
        list.stream().forEach(System.out::println);
    }
}
