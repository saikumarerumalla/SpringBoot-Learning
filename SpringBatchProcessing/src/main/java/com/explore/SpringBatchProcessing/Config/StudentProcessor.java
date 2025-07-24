package com.explore.SpringBatchProcessing.Config;

import com.explore.SpringBatchProcessing.student.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student, Student> {

    @Override
    public Student process(Student student){
        return student;
    }
}
