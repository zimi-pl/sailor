package pl.zimi.example.simple;

import pl.zimi.context.Context;
import pl.zimi.example.simple.clean.InitialData;
import pl.zimi.example.simple.clean.Student;
import pl.zimi.example.simple.clean.StudentRepository;
import pl.zimi.example.simple.clean.StudentService;
import pl.zimi.repository.contract.MemoryPort;

import java.util.List;

public class DependencyInjectionExample {

    public static void main(String[] args) {
        Context context = Context.create();

        StudentRepository studentRepository = MemoryPort.port(StudentRepository.class);
        InitialData.init(studentRepository);

        context.register(StudentRepository.class, studentRepository);

        StudentService studentService = context.getBean(StudentService.class);

        List<Student> students = studentService.listAdults();

        System.out.println(students);
    }
}
