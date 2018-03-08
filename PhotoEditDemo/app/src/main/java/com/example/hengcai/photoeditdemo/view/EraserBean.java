package com.example.hengcai.photoeditdemo.view;

import java.util.LinkedList;

/**
 * Created by heng.cai on 2018/2/27.
 */

public class EraserBean {

    public LinkedList<Line> lines=new LinkedList<>(); //橡皮擦线集合

    public EraserBean() {
    }

    public static class Line {
        public LinkedList<Point> points=new LinkedList<>();//橡皮擦点集合
        public String color;//橡皮擦颜色
        public int width;//橡皮擦宽度
        public Line() {
        }
    }

    public static class Point {
        public float x;//点横坐标
        public float y;//点纵坐标
        public Point() {
        }
    }
}
