package org.boyamihungry.andbackagain;

import net.jafama.FastMath;
import org.boyamihungry.managedvalues.ManagedValueManager;
import org.boyamihungry.managedvalues.controllers.OscillatorValueController;
import org.boyamihungry.managedvalues.controllers.ValueController;
import org.boyamihungry.managedvalues.valuegenerators.Oscillator;
import org.boyamihungry.managedvalues.valuegenerators.SinusoidalOscillatorBuilder;
import org.boyamihungry.managedvalues.values.ManagedValue;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by patwheaton on 12/11/16.
 */
public class AndBackAgain extends PApplet {

    public static final int WIDTH = 1800;
    public static final int HEIGHT = 1200;
    public static final int POINTS = 200;
    public static final int POINT_SPREAD = 3;
    public static final int HISTORY_HEIGHT = 100;

    ManagedValueManager mgr;

    List<Oscillator<? extends Number>> oscillators = new ArrayList<>();
    Map<String, List<Number>> pointMap = new HashMap<>();

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup()  {
        frameRate(60);
        textFont(createFont("Arial", 28));
        mgr = new ManagedValueManager() {
            @Override
            public String getName() {
                return "demo";
            }
            @Override
            public <T extends Number> boolean validateController(ValueController<T> controller) {
                return true;
            }
        };

        // variables that control what happens as output
        ManagedValue<Float> exitSize = mgr.createManagedValue("exitSize", 20f, 20f, 20f, this);
        ManagedValue<Float> worldRadius = mgr.createManagedValue("worldRadius", 299f, 1200f, 1199f, this);
        ManagedValue<Integer> depthOfPassage = mgr.createManagedValue("depthOfPassage", 300, 900, 500, this);
        ManagedValue<Integer> wallSwirlCount = mgr.createManagedValue("wallSwirlCount", 50, 200, 200, this);
        //ManagedValue<Float> wallSwirlCount = mgr.createManagedValue("wallSwirlCount", 50f, 500f, 300f, this);
        ManagedValue<Integer> wallSwirlRadius = mgr.createManagedValue("wallSwirlRadius", 20, 700, 190, this);
        ManagedValue<Integer> depthLayers = mgr.createManagedValue("depthLayers", 1, 33, 15, this);;

        // this type of thing should be done in the manager
        //pointMap.put(exitSize.getKey(), new ArrayList<>(POINTS));
        Oscillator sin99 = new SinusoidalOscillatorBuilder<Float>().withFrequency(99f).withName("99").withTimecodeGetter(
                new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return (long)frameCount;
                    }
                }
        ).build();
        Oscillator sin33 = new SinusoidalOscillatorBuilder<Float>().withFrequency(33f).withName("33").withTimecodeGetter(
                new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return (long)frameCount;
                    }
                }
        ).build();
        Oscillator sin200 = new SinusoidalOscillatorBuilder<Float>().withFrequency(200f).withName("200").withTimecodeGetter(
                new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return (long)frameCount;
                    }
                }
        ).build();
        Oscillator sin2 = new SinusoidalOscillatorBuilder<Float>().withFrequency(3f).withName("2").withTimecodeGetter(
                new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return (long)frameCount;
                    }
                }
        ).build();


        //        Oscillator sin66 = new SinusoidalOscillator(.0066f);
//        Oscillator sin33 = new SinusoidalOscillator(.003f);
//        Oscillator sin200 = new SinusoidalOscillator(.0020f);
//        Oscillator sin500 = new SinusoidalOscillator(.0050f);

        exitSize.addValueController(new OscillatorValueController<>(exitSize, sin2));
        worldRadius.addValueController(new OscillatorValueController<>(worldRadius, sin2));
        depthOfPassage.addValueController(new OscillatorValueController<>(depthOfPassage, sin2));
        wallSwirlCount.addValueController(new OscillatorValueController<>(wallSwirlCount, sin200));
        wallSwirlRadius.addValueController(new OscillatorValueController<>(wallSwirlRadius, sin99));
        depthLayers.addValueController(new OscillatorValueController<>(depthLayers, sin33));
        try {
            exitSize.setValueController(exitSize.getAvailableValueControllers().stream().findFirst().get());
            worldRadius.setValueController(worldRadius.getAvailableValueControllers().stream().findFirst().get());
            depthOfPassage.setValueController(depthOfPassage.getAvailableValueControllers().stream().findFirst().get());
            wallSwirlCount.setValueController(wallSwirlCount.getAvailableValueControllers().stream().findFirst().get());
            wallSwirlRadius.setValueController(wallSwirlRadius.getAvailableValueControllers().stream().findFirst().get());
            depthLayers.setValueController(depthLayers.getAvailableValueControllers().stream().findFirst().get());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void draw() {

        long start = System.currentTimeMillis();
        float exitSize = mgr.getManagedValue("exitSize").getValue().floatValue();
        float wallSwirlCount = mgr.getManagedValue("wallSwirlCount").getValue().intValue();
        float worldRadius = mgr.getManagedValue("worldRadius").getValue().floatValue();
        int depthOfPassage = mgr.getManagedValue("depthOfPassage").getValue().intValue();
        int wallSwirlRadius = mgr.getManagedValue("wallSwirlRadius").getValue().intValue();
        int depthLayers = mgr.getManagedValue("depthLayers").getValue().intValue();

        int worldColor = color(255, 0, 0);
        int swirlColor = color(0,255,0);



        background(128);
        pushMatrix();
        translate(width / 2, height / 2);
        pushStyle();

        stroke(255);
        strokeWeight(3f);
        text("wooooooooo" + frameRate, 0,0);

        // exit
        ellipse(0, 0, exitSize * 2, exitSize * 2);

        // worldradius
        noFill();
        stroke(worldColor);
        ellipse(0, 0, worldRadius, worldRadius);

        strokeWeight(1f);

        float r = worldRadius - exitSize;
        float rStep = r / (depthLayers + 1);
        int adornmentCount = 0;

        float theta = 0;
        float costheta;
        float sintheta;

        long startLoop = System.currentTimeMillis();
        stroke(swirlColor);
        do {
            costheta = (float)FastMath.cos(theta);
            sintheta = (float)FastMath.sin(theta);

            for (int layerCount=0; layerCount<depthLayers; layerCount++) {
                ellipse(((exitSize / 2) + (rStep * layerCount)) * costheta,
                        ((exitSize / 2) + (rStep * layerCount)) * sintheta,
                        wallSwirlRadius,
                        wallSwirlRadius);
            }
            theta = theta + ((2f * PI) / ((float) wallSwirlCount / (float) depthLayers));

            adornmentCount++;

        } while (adornmentCount < (wallSwirlCount / depthLayers));
        System.out.println("finish loop in " + (System.currentTimeMillis() - startLoop) + " ms");

        popStyle();
        popMatrix();

        pushMatrix();
        translate(0,mgr.getManagedValue("worldRadius").drawControlPanel().y);
        translate(0,mgr.getManagedValue("wallSwirlCount").drawControlPanel().y);
        translate(0,mgr.getManagedValue("wallSwirlRadius").drawControlPanel().y);
        translate(0,mgr.getManagedValue("depthLayers").drawControlPanel().y);
        popMatrix();
//
//
//
//        background(190);
//        PVector origin = new PVector(0, 0);
//        mgr.getManagedValues().forEach(mv -> {
//            pushStyle();
//            pushMatrix();
//            translate(origin.x, origin.y);
//            PVector howMuchTheyDrew = mv.drawControlPanel();
//            popMatrix();
//            noFill();
//            rectMode(CORNER);
//            rect(origin.x,
//                    origin.y,
//                    howMuchTheyDrew.x * 1.05f,
//                    howMuchTheyDrew.y * 1.05f);
//            popStyle();
//            origin.add(0, howMuchTheyDrew.y * 1.1f);
//            pointMap.get(mv.getKey()).add(mv.getValue());
//            if (pointMap.get(mv.getKey()).size() > POINTS) {
//                pointMap.get(mv.getKey()).remove(0);
//            }
//        });
//
//        // draw the history of the mvs
//        mgr.getManagedValues().forEach(mv -> {
//            pushMatrix();
//            noFill();
//            rectMode(CORNER);
//            rect(origin.x, origin.y, POINTS * POINT_SPREAD, HISTORY_HEIGHT);
//            PVector historyX = new PVector(0, 0);
//            translate(origin.x, origin.y);
//            PVector lastVal =  new PVector(0f,0F);
//            pointMap.get(mv.getKey()).stream().map(
//                    n -> HISTORY_HEIGHT * ((n.floatValue() - mv.getRange().getMin().floatValue()) / (mv.getRange().getMax().floatValue() - mv.getRange().getMin().floatValue())))
//                    .forEach(number -> {
//                        point(historyX.x, number);
//                        line(historyX.x, number, historyX.x - 1, lastVal.y);
//                        historyX.add(POINT_SPREAD,0);
//                        lastVal.y = number;
//                    });
//
//            popMatrix();
//            origin.add(0,HISTORY_HEIGHT + 5);
//        });
    }


    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"org.boyamihungry.andbackagain.AndBackAgain"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
