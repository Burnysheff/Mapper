package MapperImpl;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import ru.hse.homework4.Mapper;
import ru.hse.homework4.annotations.DateFormat;
import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.Ignored;
import ru.hse.homework4.annotations.PropertyName;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Class for Serializing and deserializing
 * */
public class MapperImpl implements Mapper {

    /**
     * Method for serialising
     * @param object - object to serialize;
     * @param fields - his fields
     * @return String of data of object
     * */
    private String getStr(Object object, Field[] fields) throws IllegalAccessException, ClassNotFoundException {
        StringBuilder result = new StringBuilder();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Ignored.class)) {
                continue;
            }
            String preKey = object.getClass().getName();
            String key = field.getName();
            if (field.isAnnotationPresent(PropertyName.class)) {
                PropertyName propertyName = field.getAnnotation(PropertyName.class);
                key = preKey + "|||" + propertyName.value();
            } else {
                key = preKey + "|||" + key;
            }
            result.append(key);
            result.append("=");
            if (field.getType().isAnnotationPresent(Exported.class)) {
                result.append("\n");
                result.append(writeToString(field.get(object)));
                continue;
            }
            if (field.getType() == List.class) {
                for (Object obj : (ArrayList<?>) field.get(object)) {
                    result.append(obj.toString());
                    result.append(",");
                }
                result.append("\n");
                continue;
            }
            if (field.getType() == Set.class) {
                for (Object obj : (HashSet<?>) field.get(object)) {
                    result.append(obj.toString());
                    result.append(",");
                }
                result.append("\n");
                continue;
            }
            if (field.getType() == LocalDate.class) {
                DateFormat dateFormat = field.getAnnotation(DateFormat.class);
                String str = field.get(object).toString();
                LocalDate localDateTime = LocalDate.parse(str);
                String res = localDateTime.format(DateTimeFormatter.ofPattern(dateFormat.value()));
                result.append(res);
                result.append("\n");
                continue;
            }
            if (field.getType() == LocalTime.class) {
                DateFormat dateFormat = field.getAnnotation(DateFormat.class);
                String str = field.get(object).toString();
                LocalTime localDateTime = LocalTime.parse(str);
                String res = localDateTime.format(DateTimeFormatter.ofPattern(dateFormat.value()));
                result.append(res);
                result.append("\n");
                continue;
            }
            if (field.getType() == LocalDateTime.class) {
                DateFormat dateFormat = field.getAnnotation(DateFormat.class);
                String str = field.get(object).toString();
                LocalDateTime localDateTime = LocalDateTime.parse(str);
                String res = localDateTime.format(DateTimeFormatter.ofPattern(dateFormat.value()));
                result.append(res);
                result.append("\n");
                continue;
            }
            result.append(field.get(object));
            result.append("\n");
        }
        return new String(result);
    }

    /**
     * Method to read from serialized Set
     * @param inst - object to build via deserializing
     * @param field - field which is Set
     * @param value - String of data (Seria)
     * @return object with build field
     * */
    private <T> void readFromSet(T inst, Field field, String value) throws IllegalAccessException {
        int indexStr = 0;
        Set<String> strings = new HashSet<>();
        StringBuilder subStr = new StringBuilder();
        while (indexStr != value.length()) {
            if (value.charAt(indexStr) != ',') {
                subStr.append(value.charAt(indexStr));
            } else {
                strings.add(new String(subStr));
                subStr.delete(0, subStr.length() - 1);
            }
            ++indexStr;
        }
        ParameterizedType clazzParams = (ParameterizedType)field.getGenericType();
        Type clazz = clazzParams.getActualTypeArguments()[0];
        if (clazz == String.class) {
            field.set(inst, strings);
            return;
        }
        if (clazz == int.class || clazz == Integer.class) {
            Set<Integer> list = new HashSet<>();
            for (String string : strings) {
                Integer in = Integer.parseInt(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == short.class || clazz == Short.class) {
            Set<Short> list = new HashSet<>();
            for (String string : strings) {
                Short in = Short.parseShort(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == long.class || clazz == Long.class) {
            Set<Long> list = new HashSet<>();
            for (String string : strings) {
                Long in = Long.parseLong(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            Set<Byte> list = new HashSet<>();
            for (String string : strings) {
                Byte in = Byte.parseByte(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == float.class || clazz == Float.class) {
            Set<Float> list = new HashSet<>();
            for (String string : strings) {
                Float in = Float.parseFloat(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == double.class || clazz == Double.class) {
            Set<Double> list = new HashSet<>();
            for (String string : strings) {
                Double in = Double.parseDouble(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == char.class || clazz == Character.class) {
            Set<Character> list = new HashSet<>();
            for (String string : strings) {
                Character in = string.charAt(0);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            Set<Boolean> list = new HashSet<>();
            for (String string : strings) {
                if (Objects.equals(string, "false")) {
                    list.add(false);
                } else {
                    list.add(true);
                }
            }
            field.set(inst, list);
        }
    }

    /**
     * The same as Set but with list
     * */
    private <T> void readFromList(T inst, Field field, String value) throws IllegalAccessException {
        int indexStr = 0;
        List<String> strings = new ArrayList<>();
        StringBuilder subStr = new StringBuilder();
        while (indexStr != value.length()) {
            if (value.charAt(indexStr) != ',') {
                subStr.append(value.charAt(indexStr));
            } else {
                strings.add(new String(subStr));
                subStr.delete(0, subStr.length() - 1);
            }
            ++indexStr;
        }
        ParameterizedType clazzParams = (ParameterizedType)field.getGenericType();
        Type clazz = clazzParams.getActualTypeArguments()[0];
        if (clazz == String.class) {
            field.set(inst, strings);
            return;
        }
        if (clazz == int.class || clazz == Integer.class) {
            List<Integer> list = new ArrayList<>();
            for (String string : strings) {
                Integer in = Integer.parseInt(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == short.class || clazz == Short.class) {
            List<Short> list = new ArrayList<>();
            for (String string : strings) {
                Short in = Short.parseShort(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == long.class || clazz == Long.class) {
            List<Long> list = new ArrayList<>();
            for (String string : strings) {
                Long in = Long.parseLong(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            List<Byte> list = new ArrayList<>();
            for (String string : strings) {
                Byte in = Byte.parseByte(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == float.class || clazz == Float.class) {
            List<Float> list = new ArrayList<>();
            for (String string : strings) {
                Float in = Float.parseFloat(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == double.class || clazz == Double.class) {
            List<Double> list = new ArrayList<>();
            for (String string : strings) {
                Double in = Double.parseDouble(string);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == char.class || clazz == Character.class) {
            List<Character> list = new ArrayList<>();
            for (String string : strings) {
                Character in = string.charAt(0);
                list.add(in);
            }
            field.set(inst, list);
            return;
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            List<Boolean> list = new ArrayList<>();
            for (String string : strings) {
                if (Objects.equals(string, "false")) {
                    list.add(false);
                } else {
                    list.add(true);
                }
            }
            field.set(inst, list);
        }
    }

    /**
     * @param inst - object to build via deserialize
     * @param field - his field we are look through
     * @param value - string of data (Seria)
     * @return object with correct field
     * */
    private <T> void setTypeField(T inst, Field field, String value)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Class<?> clazz = field.getType();
        if (clazz == String.class) {
            field.set(inst, value);
            return;
        }
        if (clazz == int.class || clazz == Integer.class) {
            Integer i = Integer.parseInt(value);
            field.set(inst, i);
            return;
        }
        if (clazz == short.class || clazz == Short.class) {
            Short i = Short.parseShort(value);
            field.set(inst, i);
            return;
        }
        if (clazz == long.class || clazz == Long.class) {
            Long i = Long.parseLong(value);
            field.set(inst, i);
            return;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            Byte i = Byte.parseByte(value);
            field.set(inst, i);
            return;
        }
        if (clazz == float.class || clazz == Float.class) {
            Float i = Float.parseFloat(value);
            field.set(inst, i);
            return;
        }
        if (clazz == double.class || clazz == Double.class) {
            Double i = Double.parseDouble(value);
            field.set(inst, i);
            return;
        }
        if (clazz == char.class || clazz == Character.class) {
            Character i = value.charAt(0);
            field.set(inst, i);
            return;
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            if (Objects.equals(value, "false")) {
                field.set(inst, false);
            } else {
                field.set(inst, true);
            }
            return;
        }
        if (clazz.isAnnotationPresent(Exported.class)) {
            field.set(inst, readFromString(clazz, value));
            return;
        }
        if (clazz == List.class) {
            readFromList(inst, field, value);
            return;
        }
        if (clazz == Set.class) {
            readFromSet(inst, field, value);
            return;
        }
        if (clazz == Enum.class) {
            Constructor<?> c = clazz.getConstructor();
            var in = c.newInstance();
            field.set(inst, in);
            return;
        }
        if (clazz == LocalDate.class) {
            DateFormat dateFormat = field.getAnnotation(DateFormat.class);
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormat.value()));
            field.set(inst, localDate);
            return;
        }
        if (clazz == LocalTime.class) {
            DateFormat dateFormat = field.getAnnotation(DateFormat.class);
            LocalTime localTime = LocalTime.parse(value, DateTimeFormatter.ofPattern(dateFormat.value()));
            field.set(inst, localTime);
            return;
        }
        if (clazz == LocalDateTime.class) {
            DateFormat dateFormat = field.getAnnotation(DateFormat.class);
            LocalDateTime dateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(dateFormat.value()));
            field.set(inst, dateTime);
        }
    }

    /**
     * A "dispatcher" method, which looks though fields of object, compare with string of data and
     * send to methods to build the field
     * @param inst - object to build
     * @param fields - array of fields of object
     * @param result - string of data (Seria)
     * */
    private <T> T setFields(T inst, Field[] fields, String result)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        for (Field field : fields) {
            String preName = inst.getClass().getName() + "|||";
            String name = preName + field.getName();
            if (field.getType().isAnnotationPresent(Exported.class)) {
                Class<?> clazz = field.getType();
                Constructor<?> c = clazz.getConstructor();
                var inside = c.newInstance();
                inside = readFromString(clazz, result);
                field.set(inst, inside);
                continue;
            }
            if (field.isAnnotationPresent(Ignored.class)) {
                continue;
            }
            if (field.isAnnotationPresent(PropertyName.class)) {
                PropertyName propertyName = field.getAnnotation(PropertyName.class);
                name = preName + propertyName.value();
            }
            int lastIndex = result.lastIndexOf(name);
            lastIndex += name.length() + 1;
            StringBuilder value = new StringBuilder();
            while (lastIndex != result.length() && result.charAt(lastIndex) != '\n') {
                value.append(result.charAt(lastIndex));
                ++lastIndex;
            }
            String str = String.valueOf(value);
            setTypeField(inst, field, str);
        }
        return inst;
    }

    /**
     * Here and further method to write and read in different way (they are described in interface)
     * */
    public <T> T readFromString(Class<T> clazz, String input)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> c = clazz.getConstructor();
        T inst = c.newInstance();
        Field[] fields = inst.getClass().getFields();
        return setFields(inst, fields, input);
    }

    public <T> T read(Class<T> clazz, InputStream inputStream)
        throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> c = clazz.getConstructor();
        T inst = c.newInstance();
        Field[] fields = inst.getClass().getFields();
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        return setFields(inst, fields, result);
    }

    public <T> T read(Class<T> clazz, File file)
        throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FileInputStream fileInputStream = new FileInputStream(file);
        int symbol;
        List<Character> buffer = new ArrayList<>();
        while((symbol = fileInputStream.read()) != -1){
            buffer.add((char)symbol);
        }
        char[] buff = new char[buffer.size()];
        for (int i = 0; i < buffer.size(); ++i) {
            buff[i] = buffer.get(i);
        }
        String result = new String(buff);
        Constructor<T> c = clazz.getConstructor();
        T inst = c.newInstance();
        Field[] fields = inst.getClass().getFields();
        return setFields(inst, fields, result);
    }

    public String writeToString(Object object) throws ClassNotFoundException, IllegalAccessException {
        if (!object.getClass().isAnnotationPresent(Exported.class)) {
            throw new ClassNotFoundException("No class or appropriate annotation!");
        }
        Field[] fields = object.getClass().getFields();
        return getStr(object, fields);
    }

    public void write(Object object, OutputStream outputStream) throws IOException, ClassNotFoundException, IllegalAccessException {
        if (!object.getClass().isAnnotationPresent(Exported.class)) {
            throw new ClassNotFoundException("No class or appropriate annotation!");
        }
        Field[] fields = object.getClass().getFields();
        String result = getStr(object, fields);
        for (int i = 0; i < result.length(); ++i) {
            outputStream.write((byte)result.charAt(i));
        }
        outputStream.close();
    }

    public void write(Object object, File file) throws IOException, ClassNotFoundException, IllegalAccessException {
        if (!object.getClass().isAnnotationPresent(Exported.class)) {
            throw new ClassNotFoundException("No class or appropriate annotation!");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        Field[] fields = object.getClass().getFields();
        String result = getStr(object, fields);
        for (int i = 0; i < result.length(); ++i) {
            fileOutputStream.write((byte)result.charAt(i));
        }
        fileOutputStream.close();
    }
}
