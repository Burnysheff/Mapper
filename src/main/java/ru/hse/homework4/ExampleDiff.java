package ru.hse.homework4;

import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.Ignored;
import ru.hse.homework4.annotations.PropertyName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Exported
public class ExampleDiff {
    public int a = 10;
    public int b = 20;
    @PropertyName("number")
    public double c = 30;
    public List<Integer> list = new ArrayList<Integer>();
    public Set<Character> set = new HashSet<Character>();
    @Ignored
    public float f = (float) 45.32;
    public short s = 23;
}
