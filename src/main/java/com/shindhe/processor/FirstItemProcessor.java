package com.shindhe.processor;

import com.shindhe.model.StudentCsv;
import com.shindhe.model.StudentJson;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentCsv, StudentJson> {
    @Override
    public StudentJson process(StudentCsv item) throws Exception {
        System.out.println("Inside Item Processor");

        if(item.getId() == 101) {
            throw new NullPointerException();
        }

        StudentJson studentJson = new StudentJson();
        studentJson.setId(item.getId());
        studentJson.setFirstName(item.getFirstName());
        studentJson.setLastName(item.getLastName());
        studentJson.setEmail(item.getEmail());

        return studentJson;
    }
}
