package testMapper;

import MapperImpl.MapperImpl;
import org.junit.jupiter.api.*;
import ru.hse.homework4.Example;
import ru.hse.homework4.ExampleDiff;
import ru.hse.homework4.ExampleInto;
import ru.hse.homework4.Mapper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

    @Test
    void toStringAn() throws ClassNotFoundException, IllegalAccessException {
        Example example = new Example();
        Mapper mapper = new MapperImpl();
        String string = mapper.writeToString(example);
        assertEquals(string, "ru.hse.homework4.Example|||a=10\nru.hse.homework4.Example|||b=20\nru.hse.homework4.Example|||c=30.0\n");
    }

    @Test
    void readBack() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Example example = new Example();
        example.a = -4;
        Mapper mapper = new MapperImpl();
        String string = mapper.writeToString(example);
        Example newOne = mapper.readFromString(Example.class, string);
        assertEquals(-4, newOne.a);
    }

    @Test
    void different() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ExampleDiff exampleDiff = new ExampleDiff();
        Mapper mapper = new MapperImpl();
        exampleDiff.list.add(345);
        exampleDiff.set.add('!');
        String result = mapper.writeToString(exampleDiff);
        assertEquals(result, "ru.hse.homework4.ExampleDiff|||a=10\nru.hse.homework4.ExampleDiff|||b=20\n" +
            "ru.hse.homework4.ExampleDiff|||number=30.0\nru.hse.homework4.ExampleDiff|||list=345,\nru.hse.homework4.ExampleDiff|||set=!,\n" +
            "ru.hse.homework4.ExampleDiff|||s=23\n");
    }

    @Test
    void differentComp() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ExampleDiff exampleDiff = new ExampleDiff();
        Mapper mapper = new MapperImpl();
        exampleDiff.list.add(345);
        exampleDiff.set.add('!');
        String result = mapper.writeToString(exampleDiff);
        ExampleDiff exampleDiff1 = new ExampleDiff();
        exampleDiff1 = mapper.readFromString(ExampleDiff.class, result);
        assertEquals(exampleDiff1.list.get(0), 345);
    }

    @Test
    void streamsTry()
        throws ClassNotFoundException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Example example = new Example();
        example.a = 0;
        Mapper mapper = new MapperImpl();
        String res = mapper.writeToString(example);
        InputStream is = new ByteArrayInputStream(StandardCharsets.UTF_8.encode(res).array());
        Example newOne;
        newOne = mapper.read(Example.class, is);
        assertEquals(newOne.a, 0);
    }

    @Test
    void streamsTryFile()
        throws ClassNotFoundException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Example newOne;
        Mapper mapper = new MapperImpl();
        File file = new File("text1.txt");
        newOne = mapper.read(Example.class, file);
        assertEquals(newOne.c, 0);
    }

    @Test
    void Into()
        throws ClassNotFoundException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ExampleInto example = new ExampleInto();
        Example example1 = new Example();
        example1.a = 0;
        example.ex = example1;
        Mapper mapper = new MapperImpl();
        String res = mapper.writeToString(example);
        ExampleInto example2 = mapper.readFromString(ExampleInto.class, res);
        assertEquals(example2.ex.a, 0);
    }
}
