package com.shindhe.writer;

import com.shindhe.model.*;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstItemWriter implements ItemWriter<StudentResponse> {
    @Override
    public void write(List<? extends StudentResponse> list) throws Exception {
        System.out.println("Inside Item Writer");
        list.stream().forEach(System.out::println);
    }
}
